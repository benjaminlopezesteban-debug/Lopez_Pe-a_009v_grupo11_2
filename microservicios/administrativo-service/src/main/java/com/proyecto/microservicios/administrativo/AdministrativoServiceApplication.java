package com.proyecto.microservicios.administrativo;

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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@SpringBootApplication
public class AdministrativoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdministrativoServiceApplication.class, args);
    }
}

@Configuration
@EnableWebSecurity
class SecurityConfig {
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
@Table(name = "administrativo")
class AdministrativoModel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long idAdministrativo;
    @Column(nullable = false, unique = true, length = 13)
    public String rut;
    @Column(nullable = false, length = 100)
    public String pnombre;
    @Column(length = 100)
    public String snombre;
    @Column(nullable = false, length = 150)
    public String papellido;
    @Column(nullable = false, length = 150)
    public String sapellido;
    public LocalDate fechaNaci;
    public LocalDate fechaContrato;
    @Column(nullable = false, unique = true, length = 150)
    public String email;
    @Column(nullable = false, length = 150)
    public String cargo;
}

record AdministrativoRequestDTO(
        @NotBlank(message = "El rut es obligatorio") @Size(max = 13, message = "El rut no puede superar 13 caracteres") String rut,
        @NotBlank(message = "El primer nombre es obligatorio") String pnombre,
        String snombre,
        @NotBlank(message = "El apellido paterno es obligatorio") String papellido,
        @NotBlank(message = "El apellido materno es obligatorio") String sapellido,
        LocalDate fechaNaci,
        LocalDate fechaContrato,
        @NotBlank(message = "El email es obligatorio") @Email(message = "El email debe ser valido") String email,
        @NotBlank(message = "El cargo es obligatorio") String cargo) {
}

record AdministrativoResponseDTO(Long idAdministrativo, String rut, String nombreCompleto, String email, String cargo) {
}

record ErrorResponseDTO(Instant timestamp, int status, String error, String message, String path, Map<String, String> validationErrors) {
    static ErrorResponseDTO of(int status, String error, String message, String path) {
        return new ErrorResponseDTO(Instant.now(), status, error, message, path, null);
    }

    static ErrorResponseDTO withValidationErrors(int status, String error, String message, String path, Map<String, String> errors) {
        return new ErrorResponseDTO(Instant.now(), status, error, message, path, errors);
    }
}

class ResourceNotFoundException extends RuntimeException {
    ResourceNotFoundException(String message) { super(message); }
}

class BadRequestException extends RuntimeException {
    BadRequestException(String message) { super(message); }
}

@Repository
interface AdministrativoRepository extends JpaRepository<AdministrativoModel, Long> {
    boolean existsByRut(String rut);
    boolean existsByEmail(String email);
}

@Service
@Transactional
class AdministrativoService {
    private static final Logger log = LoggerFactory.getLogger(AdministrativoService.class);
    private final AdministrativoRepository repository;

    AdministrativoService(AdministrativoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    List<AdministrativoResponseDTO> findAll() {
        log.info("event=find_all_administrativos");
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    AdministrativoResponseDTO findById(Long id) {
        log.info("event=find_administrativo id={}", id);
        return toResponse(findEntity(id));
    }

    AdministrativoResponseDTO create(AdministrativoRequestDTO request) {
        log.info("event=create_administrativo rut={} email={}", request.rut(), request.email());
        if (repository.existsByRut(request.rut())) throw new BadRequestException("Ya existe un administrativo con rut: " + request.rut());
        if (repository.existsByEmail(request.email())) throw new BadRequestException("Ya existe un administrativo con email: " + request.email());
        AdministrativoModel administrativo = new AdministrativoModel();
        copy(request, administrativo);
        return toResponse(repository.save(administrativo));
    }

    AdministrativoResponseDTO update(Long id, AdministrativoRequestDTO request) {
        log.info("event=update_administrativo id={}", id);
        AdministrativoModel administrativo = findEntity(id);
        copy(request, administrativo);
        return toResponse(repository.save(administrativo));
    }

    void delete(Long id) {
        log.info("event=delete_administrativo id={}", id);
        repository.delete(findEntity(id));
    }

    private AdministrativoModel findEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Administrativo no encontrado con id: " + id));
    }

    private void copy(AdministrativoRequestDTO request, AdministrativoModel administrativo) {
        administrativo.rut = request.rut();
        administrativo.pnombre = request.pnombre();
        administrativo.snombre = request.snombre();
        administrativo.papellido = request.papellido();
        administrativo.sapellido = request.sapellido();
        administrativo.fechaNaci = request.fechaNaci();
        administrativo.fechaContrato = request.fechaContrato();
        administrativo.email = request.email();
        administrativo.cargo = request.cargo();
    }

    private AdministrativoResponseDTO toResponse(AdministrativoModel administrativo) {
        String nombre = String.join(" ", administrativo.pnombre, administrativo.snombre == null ? "" : administrativo.snombre,
                administrativo.papellido, administrativo.sapellido).replaceAll("\\s+", " ").trim();
        return new AdministrativoResponseDTO(administrativo.idAdministrativo, administrativo.rut, nombre, administrativo.email, administrativo.cargo);
    }
}

@RestController
@RequestMapping("/api/v1/administrativos")
class AdministrativoController {
    private final AdministrativoService service;

    AdministrativoController(AdministrativoService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<AdministrativoResponseDTO>> findAll() { return ResponseEntity.ok(service.findAll()); }

    @GetMapping("/{id}")
    ResponseEntity<AdministrativoResponseDTO> findById(@PathVariable Long id) { return ResponseEntity.ok(service.findById(id)); }

    @PostMapping
    ResponseEntity<AdministrativoResponseDTO> create(@Valid @RequestBody AdministrativoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    ResponseEntity<AdministrativoResponseDTO> update(@PathVariable Long id, @Valid @RequestBody AdministrativoRequestDTO request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

@RestControllerAdvice
class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        log.warn("event=validation_error path={} errors={}", request.getRequestURI(), errors);
        return ResponseEntity.badRequest().body(ErrorResponseDTO.withValidationErrors(400, "Bad Request", "La solicitud contiene campos invalidos", request.getRequestURI(), errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ErrorResponseDTO> handleConstraint(ConstraintViolationException exception, HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getConstraintViolations().forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));
        return ResponseEntity.badRequest().body(ErrorResponseDTO.withValidationErrors(400, "Bad Request", "La solicitud contiene campos invalidos", request.getRequestURI(), errors));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ErrorResponseDTO> handleNotFound(ResourceNotFoundException exception, HttpServletRequest request) { return build(HttpStatus.NOT_FOUND, exception.getMessage(), request); }

    @ExceptionHandler(BadRequestException.class)
    ResponseEntity<ErrorResponseDTO> handleBadRequest(BadRequestException exception, HttpServletRequest request) { return build(HttpStatus.BAD_REQUEST, exception.getMessage(), request); }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ErrorResponseDTO> handleForbidden(AccessDeniedException exception, HttpServletRequest request) { return build(HttpStatus.FORBIDDEN, "No tienes permisos para acceder a este recurso", request); }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ErrorResponseDTO> handleDataIntegrity(DataIntegrityViolationException exception, HttpServletRequest request) { return build(HttpStatus.BAD_REQUEST, "El recurso viola una restriccion de datos", request); }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponseDTO> handleGeneric(Exception exception, HttpServletRequest request) {
        log.error("event=internal_error path={} message={}", request.getRequestURI(), exception.getMessage(), exception);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", request);
    }

    private ResponseEntity<ErrorResponseDTO> build(HttpStatus status, String message, HttpServletRequest request) {
        log.warn("event=api_error status={} path={} message={}", status.value(), request.getRequestURI(), message);
        return ResponseEntity.status(status).body(ErrorResponseDTO.of(status.value(), status.getReasonPhrase(), message, request.getRequestURI()));
    }
}
