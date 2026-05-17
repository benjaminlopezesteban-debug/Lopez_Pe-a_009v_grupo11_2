package com.proyecto.microservicios.fichaclinica;

import java.time.Instant;
import java.time.LocalDate;
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
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@SpringBootApplication
public class FichaClinicaServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FichaClinicaServiceApplication.class, args);
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
@Table(name = "ficha_clinica")
class FichaClinicaModel {
    @Id
    @Column(nullable = false, length = 40)
    public String folioFicha;
    @Column(nullable = false)
    public LocalDate fechaCreacion;
    @Column(nullable = false)
    public Long idPaciente;
    @Column(nullable = false)
    public Long idEstante;
}

record FichaClinicaRequestDTO(
        @NotBlank(message = "El folio de ficha es obligatorio") String folioFicha,
        @NotNull(message = "La fecha de creacion es obligatoria") LocalDate fechaCreacion,
        @NotNull(message = "El paciente es obligatorio") Long idPaciente,
        @NotNull(message = "El estante es obligatorio") Long idEstante) {
}

record FichaClinicaResponseDTO(String folioFicha, LocalDate fechaCreacion, Long idPaciente, String pacienteNombre, Long idEstante, Integer numEstante) {
}

record ErrorResponseDTO(Instant timestamp, int status, String error, String message, String path, Map<String, String> validationErrors) {
    static ErrorResponseDTO of(int status, String error, String message, String path) { return new ErrorResponseDTO(Instant.now(), status, error, message, path, null); }
    static ErrorResponseDTO withValidationErrors(int status, String error, String message, String path, Map<String, String> errors) { return new ErrorResponseDTO(Instant.now(), status, error, message, path, errors); }
}

class ResourceNotFoundException extends RuntimeException { ResourceNotFoundException(String message) { super(message); } }
class BadRequestException extends RuntimeException { BadRequestException(String message) { super(message); } }

@Repository
interface FichaClinicaRepository extends JpaRepository<FichaClinicaModel, String> {
}

@Service
class ExternalValidationClient {
    private static final Logger log = LoggerFactory.getLogger(ExternalValidationClient.class);
    private final RestTemplate restTemplate;
    private final String pacienteUrl;
    private final String estanteUrl;

    ExternalValidationClient(RestTemplate restTemplate, @Value("${services.paciente.url}") String pacienteUrl, @Value("${services.estante.url}") String estanteUrl) {
        this.restTemplate = restTemplate;
        this.pacienteUrl = pacienteUrl;
        this.estanteUrl = estanteUrl;
    }

    Map<?, ?> obtenerPaciente(Long idPaciente) {
        return getResource(pacienteUrl + "/api/v1/pacientes/{id}", idPaciente, "paciente-service", "Paciente no encontrado con id: " + idPaciente);
    }

    Map<?, ?> obtenerEstante(Long idEstante) {
        return getResource(estanteUrl + "/api/v1/estantes/{id}", idEstante, "estante-service", "Estante no encontrado con id: " + idEstante);
    }

    private Map<?, ?> getResource(String url, Object id, String target, String notFoundMessage) {
        log.info("event=rest_call target={} method=GET url={} id={}", target, url, id);
        try {
            return restTemplate.getForObject(url, Map.class, id);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new BadRequestException(notFoundMessage);
        } catch (RestClientException ex) {
            log.error("event=rest_error target={} id={} message={}", target, id, ex.getMessage());
            throw new BadRequestException("No fue posible validar recurso en " + target);
        }
    }
}

@Service
@Transactional
class FichaClinicaService {
    private static final Logger log = LoggerFactory.getLogger(FichaClinicaService.class);
    private final FichaClinicaRepository repository;
    private final ExternalValidationClient client;

    FichaClinicaService(FichaClinicaRepository repository, ExternalValidationClient client) {
        this.repository = repository;
        this.client = client;
    }

    @Transactional(readOnly = true)
    List<FichaClinicaResponseDTO> findAll() {
        log.info("event=find_all_fichas_clinicas");
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    FichaClinicaResponseDTO findByFolio(String folio) {
        log.info("event=find_ficha_clinica folio={}", folio);
        return toResponse(findEntity(folio));
    }

    FichaClinicaResponseDTO create(FichaClinicaRequestDTO request) {
        log.info("event=create_ficha_clinica folio={} idPaciente={} idEstante={}", request.folioFicha(), request.idPaciente(), request.idEstante());
        client.obtenerPaciente(request.idPaciente());
        client.obtenerEstante(request.idEstante());
        FichaClinicaModel ficha = new FichaClinicaModel();
        copy(request, ficha);
        return toResponse(repository.save(ficha));
    }

    FichaClinicaResponseDTO update(String folio, FichaClinicaRequestDTO request) {
        log.info("event=update_ficha_clinica folio={}", folio);
        client.obtenerPaciente(request.idPaciente());
        client.obtenerEstante(request.idEstante());
        FichaClinicaModel ficha = findEntity(folio);
        copy(request, ficha);
        ficha.folioFicha = folio;
        return toResponse(repository.save(ficha));
    }

    void delete(String folio) {
        log.info("event=delete_ficha_clinica folio={}", folio);
        repository.delete(findEntity(folio));
    }

    private FichaClinicaModel findEntity(String folio) {
        return repository.findById(folio).orElseThrow(() -> new ResourceNotFoundException("FichaClinica no encontrada con folio: " + folio));
    }

    private void copy(FichaClinicaRequestDTO request, FichaClinicaModel ficha) {
        ficha.folioFicha = request.folioFicha();
        ficha.fechaCreacion = request.fechaCreacion();
        ficha.idPaciente = request.idPaciente();
        ficha.idEstante = request.idEstante();
    }

    private FichaClinicaResponseDTO toResponse(FichaClinicaModel ficha) {
        Map<?, ?> paciente = client.obtenerPaciente(ficha.idPaciente);
        Map<?, ?> estante = client.obtenerEstante(ficha.idEstante);
        Object nombreCompleto = paciente.get("nombreCompleto");
        String nombre = nombreCompleto == null ? "" : String.valueOf(nombreCompleto);
        Integer numEstante = estante.get("numEstante") instanceof Number number ? number.intValue() : null;
        return new FichaClinicaResponseDTO(ficha.folioFicha, ficha.fechaCreacion, ficha.idPaciente, nombre, ficha.idEstante, numEstante);
    }
}

@RestController
@RequestMapping("/api/v1/fichas-clinicas")
class FichaClinicaController {
    private final FichaClinicaService service;
    FichaClinicaController(FichaClinicaService service) { this.service = service; }
    @GetMapping ResponseEntity<List<FichaClinicaResponseDTO>> findAll() { return ResponseEntity.ok(service.findAll()); }
    @GetMapping("/{folio}") ResponseEntity<FichaClinicaResponseDTO> findByFolio(@PathVariable String folio) { return ResponseEntity.ok(service.findByFolio(folio)); }
    @PostMapping ResponseEntity<FichaClinicaResponseDTO> create(@Valid @RequestBody FichaClinicaRequestDTO request) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request)); }
    @PutMapping("/{folio}") ResponseEntity<FichaClinicaResponseDTO> update(@PathVariable String folio, @Valid @RequestBody FichaClinicaRequestDTO request) { return ResponseEntity.ok(service.update(folio, request)); }
    @DeleteMapping("/{folio}") ResponseEntity<Void> delete(@PathVariable String folio) { service.delete(folio); return ResponseEntity.noContent().build(); }
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
