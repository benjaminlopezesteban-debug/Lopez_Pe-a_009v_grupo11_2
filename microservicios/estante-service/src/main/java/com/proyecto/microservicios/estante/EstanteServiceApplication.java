package com.proyecto.microservicios.estante;

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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@SpringBootApplication
public class EstanteServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EstanteServiceApplication.class, args);
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
@Table(name = "estante")
class EstanteModel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long idEstante;
    @Column(nullable = false, unique = true)
    public Integer numEstante;
    @Column(nullable = false)
    public Integer numBodega;
    @Column(length = 120)
    public String ubicacion;
}

record EstanteRequestDTO(
        @NotNull(message = "El numero de estante es obligatorio") @Min(value = 1, message = "El numero de estante debe ser mayor a cero") Integer numEstante,
        @NotNull(message = "El numero de bodega es obligatorio") @Min(value = 1, message = "El numero de bodega debe ser mayor a cero") Integer numBodega,
        @Size(max = 120, message = "La ubicacion no puede superar 120 caracteres") String ubicacion) {
}

record EstanteResponseDTO(Long idEstante, Integer numEstante, Integer numBodega, String ubicacion) {
}

record ErrorResponseDTO(Instant timestamp, int status, String error, String message, String path, Map<String, String> validationErrors) {
    static ErrorResponseDTO of(int status, String error, String message, String path) { return new ErrorResponseDTO(Instant.now(), status, error, message, path, null); }
    static ErrorResponseDTO withValidationErrors(int status, String error, String message, String path, Map<String, String> errors) { return new ErrorResponseDTO(Instant.now(), status, error, message, path, errors); }
}

class ResourceNotFoundException extends RuntimeException { ResourceNotFoundException(String message) { super(message); } }
class BadRequestException extends RuntimeException { BadRequestException(String message) { super(message); } }

@Repository
interface EstanteRepository extends JpaRepository<EstanteModel, Long> {
    boolean existsByNumEstante(Integer numEstante);
}

@Service
@Transactional
class EstanteService {
    private static final Logger log = LoggerFactory.getLogger(EstanteService.class);
    private final EstanteRepository repository;

    EstanteService(EstanteRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    List<EstanteResponseDTO> findAll() {
        log.info("event=find_all_estantes");
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    EstanteResponseDTO findById(Long id) {
        log.info("event=find_estante id={}", id);
        return toResponse(findEntity(id));
    }

    EstanteResponseDTO create(EstanteRequestDTO request) {
        log.info("event=create_estante numEstante={} numBodega={}", request.numEstante(), request.numBodega());
        if (repository.existsByNumEstante(request.numEstante())) throw new BadRequestException("Ya existe un estante con numero: " + request.numEstante());
        EstanteModel estante = new EstanteModel();
        copy(request, estante);
        return toResponse(repository.save(estante));
    }

    EstanteResponseDTO update(Long id, EstanteRequestDTO request) {
        log.info("event=update_estante id={}", id);
        EstanteModel estante = findEntity(id);
        copy(request, estante);
        return toResponse(repository.save(estante));
    }

    void delete(Long id) {
        log.info("event=delete_estante id={}", id);
        repository.delete(findEntity(id));
    }

    private EstanteModel findEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Estante no encontrado con id: " + id));
    }

    private void copy(EstanteRequestDTO request, EstanteModel estante) {
        estante.numEstante = request.numEstante();
        estante.numBodega = request.numBodega();
        estante.ubicacion = request.ubicacion();
    }

    private EstanteResponseDTO toResponse(EstanteModel estante) {
        return new EstanteResponseDTO(estante.idEstante, estante.numEstante, estante.numBodega, estante.ubicacion);
    }
}

@RestController
@RequestMapping("/api/v1/estantes")
class EstanteController {
    private final EstanteService service;
    EstanteController(EstanteService service) { this.service = service; }
    @GetMapping ResponseEntity<List<EstanteResponseDTO>> findAll() { return ResponseEntity.ok(service.findAll()); }
    @GetMapping("/{id}") ResponseEntity<EstanteResponseDTO> findById(@PathVariable Long id) { return ResponseEntity.ok(service.findById(id)); }
    @PostMapping ResponseEntity<EstanteResponseDTO> create(@Valid @RequestBody EstanteRequestDTO request) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request)); }
    @PutMapping("/{id}") ResponseEntity<EstanteResponseDTO> update(@PathVariable Long id, @Valid @RequestBody EstanteRequestDTO request) { return ResponseEntity.ok(service.update(id, request)); }
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
