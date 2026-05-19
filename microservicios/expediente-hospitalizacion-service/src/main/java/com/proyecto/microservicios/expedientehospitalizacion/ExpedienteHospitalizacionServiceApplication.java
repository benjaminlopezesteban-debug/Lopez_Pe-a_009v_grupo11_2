package com.proyecto.microservicios.expedientehospitalizacion;

import java.time.Instant;
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
public class ExpedienteHospitalizacionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExpedienteHospitalizacionServiceApplication.class, args);
    }
}

@Configuration
@EnableWebSecurity
class AppConfig {
    @Bean RestTemplate restTemplate() { return new RestTemplate(); }
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.GET, "/api/v1/**").permitAll().anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
    @Bean UserDetailsService users(@Value("${app.auth.username}") String username, @Value("${app.auth.password}") String password) {
        UserDetails user = User.withUsername(username).password("{noop}" + password).roles("ADMIN").build();
        return new InMemoryUserDetailsManager(user);
    }
}

@Entity
@Table(name = "expediente_hospitalizacion")
class ExpedienteHospitalizacionModel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long idExpediente;
    @Column(nullable = false, unique = true, length = 60)
    public String codExpediente;
    @Column(nullable = false, length = 13)
    public String rutPaciente;
    @Column(nullable = false)
    public Boolean digitalizacion;
    @Column(nullable = false)
    public Long idReservaAtencion;
}

record ExpedienteHospitalizacionRequestDTO(
        @NotBlank(message = "El codigo de expediente es obligatorio") String codExpediente,
        @NotBlank(message = "El rut del paciente es obligatorio") String rutPaciente,
        @NotNull(message = "La digitalizacion es obligatoria") Boolean digitalizacion,
        @NotNull(message = "La reserva de atencion es obligatoria") Long idReservaAtencion) {
}

record ExpedienteHospitalizacionResponseDTO(Long idExpediente, String codExpediente, String rutPaciente, Boolean digitalizacion, Long idReservaAtencion) {
}

record ErrorResponseDTO(Instant timestamp, int status, String error, String message, String path, Map<String, String> validationErrors) {
    static ErrorResponseDTO of(int status, String error, String message, String path) { return new ErrorResponseDTO(Instant.now(), status, error, message, path, null); }
    static ErrorResponseDTO withValidationErrors(int status, String error, String message, String path, Map<String, String> errors) { return new ErrorResponseDTO(Instant.now(), status, error, message, path, errors); }
}

class ResourceNotFoundException extends RuntimeException { ResourceNotFoundException(String message) { super(message); } }
class BadRequestException extends RuntimeException { BadRequestException(String message) { super(message); } }

@Repository
interface ExpedienteHospitalizacionRepository extends JpaRepository<ExpedienteHospitalizacionModel, Long> {
    boolean existsByCodExpediente(String codExpediente);
}

@Service
class ReservaClient {
    private static final Logger log = LoggerFactory.getLogger(ReservaClient.class);
    private final RestTemplate restTemplate;
    private final String reservaUrl;
    ReservaClient(RestTemplate restTemplate, @Value("${services.reserva.url}") String reservaUrl) {
        this.restTemplate = restTemplate;
        this.reservaUrl = reservaUrl;
    }
    void validarReserva(Long idReserva) {
        String url = reservaUrl + "/api/v1/reservas-atencion/{id}";
        log.info("event=rest_call target=reserva-atencion-service method=GET url={} idReserva={}", url, idReserva);
        try {
            restTemplate.getForEntity(url, String.class, idReserva);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new BadRequestException("Reserva de atencion no encontrada con id: " + idReserva);
        } catch (RestClientException ex) {
            log.error("event=rest_error target=reserva-atencion-service idReserva={} message={}", idReserva, ex.getMessage());
            throw new BadRequestException("No fue posible validar la reserva " + idReserva);
        }
    }
}

@Service
@Transactional
class ExpedienteHospitalizacionService {
    private static final Logger log = LoggerFactory.getLogger(ExpedienteHospitalizacionService.class);
    private final ExpedienteHospitalizacionRepository repository;
    private final ReservaClient reservaClient;
    ExpedienteHospitalizacionService(ExpedienteHospitalizacionRepository repository, ReservaClient reservaClient) {
        this.repository = repository;
        this.reservaClient = reservaClient;
    }
    @Transactional(readOnly = true) List<ExpedienteHospitalizacionResponseDTO> findAll() { log.info("event=find_all_expedientes"); return repository.findAll().stream().map(this::toResponse).toList(); }
    @Transactional(readOnly = true) ExpedienteHospitalizacionResponseDTO findById(Long id) { log.info("event=find_expediente id={}", id); return toResponse(findEntity(id)); }
    ExpedienteHospitalizacionResponseDTO create(ExpedienteHospitalizacionRequestDTO request) {
        log.info("event=create_expediente codExpediente={} idReserva={}", request.codExpediente(), request.idReservaAtencion());
        if (repository.existsByCodExpediente(request.codExpediente())) throw new BadRequestException("Ya existe un expediente con codigo: " + request.codExpediente());
        reservaClient.validarReserva(request.idReservaAtencion());
        ExpedienteHospitalizacionModel expediente = new ExpedienteHospitalizacionModel();
        copy(request, expediente);
        return toResponse(repository.save(expediente));
    }
    ExpedienteHospitalizacionResponseDTO update(Long id, ExpedienteHospitalizacionRequestDTO request) {
        log.info("event=update_expediente id={} idReserva={}", id, request.idReservaAtencion());
        reservaClient.validarReserva(request.idReservaAtencion());
        ExpedienteHospitalizacionModel expediente = findEntity(id);
        copy(request, expediente);
        return toResponse(repository.save(expediente));
    }
    void delete(Long id) { log.info("event=delete_expediente id={}", id); repository.delete(findEntity(id)); }
    private ExpedienteHospitalizacionModel findEntity(Long id) { return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Expediente no encontrado con id: " + id)); }
    private void copy(ExpedienteHospitalizacionRequestDTO request, ExpedienteHospitalizacionModel expediente) {
        expediente.codExpediente = request.codExpediente();
        expediente.rutPaciente = request.rutPaciente();
        expediente.digitalizacion = request.digitalizacion();
        expediente.idReservaAtencion = request.idReservaAtencion();
    }
    private ExpedienteHospitalizacionResponseDTO toResponse(ExpedienteHospitalizacionModel e) { return new ExpedienteHospitalizacionResponseDTO(e.idExpediente, e.codExpediente, e.rutPaciente, e.digitalizacion, e.idReservaAtencion); }
}

@RestController
@RequestMapping("/api/v1/expedientes-hospitalizacion")
class ExpedienteHospitalizacionController {
    private final ExpedienteHospitalizacionService service;
    ExpedienteHospitalizacionController(ExpedienteHospitalizacionService service) { this.service = service; }
    @GetMapping ResponseEntity<List<ExpedienteHospitalizacionResponseDTO>> findAll() { return ResponseEntity.ok(service.findAll()); }
    @GetMapping("/{id}") ResponseEntity<ExpedienteHospitalizacionResponseDTO> findById(@PathVariable Long id) { return ResponseEntity.ok(service.findById(id)); }
    @PostMapping ResponseEntity<ExpedienteHospitalizacionResponseDTO> create(@Valid @RequestBody ExpedienteHospitalizacionRequestDTO request) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request)); }
    @PutMapping("/{id}") ResponseEntity<ExpedienteHospitalizacionResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ExpedienteHospitalizacionRequestDTO request) { return ResponseEntity.ok(service.update(id, request)); }
    @DeleteMapping("/{id}") ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}

@RestControllerAdvice
class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        return ResponseEntity.badRequest().body(ErrorResponseDTO.withValidationErrors(400, "Bad Request", "La solicitud contiene campos invalidos", request.getRequestURI(), errors));
    }
    @ExceptionHandler(ConstraintViolationException.class) ResponseEntity<ErrorResponseDTO> handleConstraint(ConstraintViolationException exception, HttpServletRequest request) {
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
