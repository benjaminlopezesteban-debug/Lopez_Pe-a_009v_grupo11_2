package com.proyecto.microservicios.reservaatencion;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@SpringBootApplication
public class ReservaAtencionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReservaAtencionServiceApplication.class, args);
    }
}

@Configuration
@EnableWebSecurity
class AppConfig {
    @Bean RestTemplate restTemplate() { return new RestTemplate(); }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.GET, "/api/v1/**").permitAll().anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    UserDetailsService users(@Value("${app.auth.username}") String username, @Value("${app.auth.password}") String password) {
        UserDetails user = User.withUsername(username).password("{noop}" + password).roles("ADMIN").build();
        return new InMemoryUserDetailsManager(user);
    }
}

@Entity
@Table(name = "reserva_atencion")
class ReservaAtencionModel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long idReservaAtencion;
    @Column(nullable = false)
    public LocalDate fechaReservada;
    @Column(nullable = false)
    public LocalTime horaReservada;
    @Column(nullable = false, length = 120)
    public String especialidad;
    @Column(nullable = false, length = 150)
    public String profesional;
    @Column(nullable = false)
    public Long idPaciente;
}

record ReservaAtencionRequestDTO(
        @NotNull(message = "La fecha reservada es obligatoria") LocalDate fechaReservada,
        @NotNull(message = "La hora reservada es obligatoria") LocalTime horaReservada,
        @NotBlank(message = "La especialidad es obligatoria") String especialidad,
        @NotBlank(message = "El profesional es obligatorio") String profesional,
        @NotNull(message = "El paciente es obligatorio") Long idPaciente) {
}

record ReservaAtencionResponseDTO(Long idReservaAtencion, LocalDate fechaReservada, LocalTime horaReservada, String especialidad, String profesional, Long idPaciente) {
}

record ErrorResponseDTO(Instant timestamp, int status, String error, String message, String path, Map<String, String> validationErrors) {
    static ErrorResponseDTO of(int status, String error, String message, String path) { return new ErrorResponseDTO(Instant.now(), status, error, message, path, null); }
    static ErrorResponseDTO withValidationErrors(int status, String error, String message, String path, Map<String, String> errors) { return new ErrorResponseDTO(Instant.now(), status, error, message, path, errors); }
}

class ResourceNotFoundException extends RuntimeException { ResourceNotFoundException(String message) { super(message); } }
class BadRequestException extends RuntimeException { BadRequestException(String message) { super(message); } }

@Repository
interface ReservaAtencionRepository extends JpaRepository<ReservaAtencionModel, Long> {
}

@Service
class PacienteClient {
    private static final Logger log = LoggerFactory.getLogger(PacienteClient.class);
    private final RestTemplate restTemplate;
    private final String pacienteUrl;

    PacienteClient(RestTemplate restTemplate, @Value("${services.paciente.url}") String pacienteUrl) {
        this.restTemplate = restTemplate;
        this.pacienteUrl = pacienteUrl;
    }

    void validarPaciente(Long idPaciente) {
        String url = pacienteUrl + "/api/v1/pacientes/{id}";
        log.info("event=rest_call target=paciente-service method=GET url={} idPaciente={}", url, idPaciente);
        try {
            restTemplate.getForEntity(url, String.class, idPaciente);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new BadRequestException("Paciente no encontrado con id: " + idPaciente);
        } catch (RestClientException ex) {
            log.error("event=rest_error target=paciente-service idPaciente={} message={}", idPaciente, ex.getMessage());
            throw new BadRequestException("No fue posible validar el paciente " + idPaciente);
        }
    }
}

@Service
@Transactional
class ReservaAtencionService {
    private static final Logger log = LoggerFactory.getLogger(ReservaAtencionService.class);
    private final ReservaAtencionRepository repository;
    private final PacienteClient pacienteClient;

    ReservaAtencionService(ReservaAtencionRepository repository, PacienteClient pacienteClient) {
        this.repository = repository;
        this.pacienteClient = pacienteClient;
    }

    @Transactional(readOnly = true)
    List<ReservaAtencionResponseDTO> findAll() {
        log.info("event=find_all_reservas");
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    ReservaAtencionResponseDTO findById(Long id) {
        log.info("event=find_reserva id={}", id);
        return toResponse(findEntity(id));
    }

    ReservaAtencionResponseDTO create(ReservaAtencionRequestDTO request) {
        log.info("event=create_reserva idPaciente={} fecha={}", request.idPaciente(), request.fechaReservada());
        pacienteClient.validarPaciente(request.idPaciente());
        ReservaAtencionModel reserva = new ReservaAtencionModel();
        copy(request, reserva);
        return toResponse(repository.save(reserva));
    }

    ReservaAtencionResponseDTO update(Long id, ReservaAtencionRequestDTO request) {
        log.info("event=update_reserva id={} idPaciente={}", id, request.idPaciente());
        pacienteClient.validarPaciente(request.idPaciente());
        ReservaAtencionModel reserva = findEntity(id);
        copy(request, reserva);
        return toResponse(repository.save(reserva));
    }

    void delete(Long id) {
        log.info("event=delete_reserva id={}", id);
        repository.delete(findEntity(id));
    }

    private ReservaAtencionModel findEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ReservaAtencion no encontrada con id: " + id));
    }

    private void copy(ReservaAtencionRequestDTO request, ReservaAtencionModel reserva) {
        reserva.fechaReservada = request.fechaReservada();
        reserva.horaReservada = request.horaReservada();
        reserva.especialidad = request.especialidad();
        reserva.profesional = request.profesional();
        reserva.idPaciente = request.idPaciente();
    }

    private ReservaAtencionResponseDTO toResponse(ReservaAtencionModel reserva) {
        return new ReservaAtencionResponseDTO(reserva.idReservaAtencion, reserva.fechaReservada, reserva.horaReservada, reserva.especialidad, reserva.profesional, reserva.idPaciente);
    }
}

@RestController
@RequestMapping("/api/v1/reservas-atencion")
class ReservaAtencionController {
    private final ReservaAtencionService service;
    ReservaAtencionController(ReservaAtencionService service) { this.service = service; }
    @GetMapping ResponseEntity<List<ReservaAtencionResponseDTO>> findAll() { return ResponseEntity.ok(service.findAll()); }
    @GetMapping("/{id}") ResponseEntity<ReservaAtencionResponseDTO> findById(@PathVariable Long id) { return ResponseEntity.ok(service.findById(id)); }
    @PostMapping ResponseEntity<ReservaAtencionResponseDTO> create(@Valid @RequestBody ReservaAtencionRequestDTO request) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request)); }
    @PutMapping("/{id}") ResponseEntity<ReservaAtencionResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ReservaAtencionRequestDTO request) { return ResponseEntity.ok(service.update(id, request)); }
    @DeleteMapping("/{id}") ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}

@RestControllerAdvice
class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        return ResponseEntity.badRequest().body(ErrorResponseDTO.withValidationErrors(400, "Bad Request", "La solicitud contiene campos invalidos", request.getRequestURI(), errors));
    }
    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ErrorResponseDTO> handleConstraint(ConstraintViolationException exception, HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getConstraintViolations().forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));
        return ResponseEntity.badRequest().body(ErrorResponseDTO.withValidationErrors(400, "Bad Request", "La solicitud contiene campos invalidos", request.getRequestURI(), errors));
    }
    @ExceptionHandler(ResourceNotFoundException.class) ResponseEntity<ErrorResponseDTO> handleNotFound(ResourceNotFoundException e, HttpServletRequest r) { return build(HttpStatus.NOT_FOUND, e.getMessage(), r); }
    @ExceptionHandler(BadRequestException.class) ResponseEntity<ErrorResponseDTO> handleBad(BadRequestException e, HttpServletRequest r) { return build(HttpStatus.BAD_REQUEST, e.getMessage(), r); }
    @ExceptionHandler(AccessDeniedException.class) ResponseEntity<ErrorResponseDTO> handleForbidden(AccessDeniedException e, HttpServletRequest r) { return build(HttpStatus.FORBIDDEN, "No tienes permisos para acceder a este recurso", r); }
    @ExceptionHandler(DataIntegrityViolationException.class) ResponseEntity<ErrorResponseDTO> handleData(DataIntegrityViolationException e, HttpServletRequest r) { return build(HttpStatus.BAD_REQUEST, "El recurso viola una restriccion de datos", r); }
    @ExceptionHandler(Exception.class) ResponseEntity<ErrorResponseDTO> handleGeneric(Exception e, HttpServletRequest r) { log.error("event=internal_error path={} message={}", r.getRequestURI(), e.getMessage(), e); return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", r); }
    private ResponseEntity<ErrorResponseDTO> build(HttpStatus status, String message, HttpServletRequest request) {
        log.warn("event=api_error status={} path={} message={}", status.value(), request.getRequestURI(), message);
        return ResponseEntity.status(status).body(ErrorResponseDTO.of(status.value(), status.getReasonPhrase(), message, request.getRequestURI()));
    }
}
