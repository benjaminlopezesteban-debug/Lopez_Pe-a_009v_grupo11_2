package com.proyecto.microservicios.registroarchivado;

import java.time.Instant;
import java.time.LocalDateTime;
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
public class RegistroArchivadoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RegistroArchivadoServiceApplication.class, args);
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
@Table(name = "registro_archivado")
class RegistroArchivadoModel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long idRegistroArchivado;
    @Column(nullable = false, length = 40)
    public String folioFicha;
    @Column(nullable = false)
    public Long idAdministrativo;
    @Column(nullable = false)
    public Long idExpediente;
    @Column(nullable = false)
    public LocalDateTime fechaArchivado;
    @Column(length = 500)
    public String observacion;
}

record RegistroArchivadoRequestDTO(
        @NotBlank(message = "La ficha clinica es obligatoria") String folioFicha,
        @NotNull(message = "El administrativo es obligatorio") Long idAdministrativo,
        @NotNull(message = "El expediente es obligatorio") Long idExpediente,
        @NotNull(message = "La fecha de archivado es obligatoria") LocalDateTime fechaArchivado,
        String observacion) {
}

record RegistroArchivadoResponseDTO(Long idRegistroArchivado, String folioFicha, Long idAdministrativo, Long idExpediente, LocalDateTime fechaArchivado, String observacion) {
}

record ErrorResponseDTO(Instant timestamp, int status, String error, String message, String path, Map<String, String> validationErrors) {
    static ErrorResponseDTO of(int status, String error, String message, String path) { return new ErrorResponseDTO(Instant.now(), status, error, message, path, null); }
    static ErrorResponseDTO withValidationErrors(int status, String error, String message, String path, Map<String, String> errors) { return new ErrorResponseDTO(Instant.now(), status, error, message, path, errors); }
}

class ResourceNotFoundException extends RuntimeException { ResourceNotFoundException(String message) { super(message); } }
class BadRequestException extends RuntimeException { BadRequestException(String message) { super(message); } }

@Repository
interface RegistroArchivadoRepository extends JpaRepository<RegistroArchivadoModel, Long> {
}

@Service
class ExternalValidationClient {
    private static final Logger log = LoggerFactory.getLogger(ExternalValidationClient.class);
    private final RestTemplate restTemplate;
    private final String administrativoUrl;
    private final String fichaUrl;
    private final String expedienteUrl;
    ExternalValidationClient(RestTemplate restTemplate, @Value("${services.administrativo.url}") String administrativoUrl,
                             @Value("${services.ficha.url}") String fichaUrl, @Value("${services.expediente.url}") String expedienteUrl) {
        this.restTemplate = restTemplate;
        this.administrativoUrl = administrativoUrl;
        this.fichaUrl = fichaUrl;
        this.expedienteUrl = expedienteUrl;
    }
    void validarAdministrativo(Long id) { validar(administrativoUrl + "/api/v1/administrativos/{id}", id, "administrativo-service", "Administrativo no encontrado con id: " + id); }
    void validarFicha(String folio) { validar(fichaUrl + "/api/v1/fichas-clinicas/{folio}", folio, "ficha-clinica-service", "Ficha clinica no encontrada con folio: " + folio); }
    void validarExpediente(Long id) { validar(expedienteUrl + "/api/v1/expedientes-hospitalizacion/{id}", id, "expediente-hospitalizacion-service", "Expediente no encontrado con id: " + id); }
    private void validar(String url, Object id, String target, String notFoundMessage) {
        log.info("event=rest_call target={} method=GET url={} id={}", target, url, id);
        try {
            restTemplate.getForEntity(url, String.class, id);
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
class RegistroArchivadoService {
    private static final Logger log = LoggerFactory.getLogger(RegistroArchivadoService.class);
    private final RegistroArchivadoRepository repository;
    private final ExternalValidationClient client;
    RegistroArchivadoService(RegistroArchivadoRepository repository, ExternalValidationClient client) { this.repository = repository; this.client = client; }
    @Transactional(readOnly = true) List<RegistroArchivadoResponseDTO> findAll() { log.info("event=find_all_registros_archivados"); return repository.findAll().stream().map(this::toResponse).toList(); }
    @Transactional(readOnly = true) RegistroArchivadoResponseDTO findById(Long id) { log.info("event=find_registro_archivado id={}", id); return toResponse(findEntity(id)); }
    RegistroArchivadoResponseDTO create(RegistroArchivadoRequestDTO request) {
        log.info("event=create_registro_archivado folioFicha={} idAdministrativo={} idExpediente={}", request.folioFicha(), request.idAdministrativo(), request.idExpediente());
        validarDependencias(request);
        RegistroArchivadoModel registro = new RegistroArchivadoModel();
        copy(request, registro);
        return toResponse(repository.save(registro));
    }
    RegistroArchivadoResponseDTO update(Long id, RegistroArchivadoRequestDTO request) {
        log.info("event=update_registro_archivado id={} folioFicha={} idExpediente={}", id, request.folioFicha(), request.idExpediente());
        validarDependencias(request);
        RegistroArchivadoModel registro = findEntity(id);
        copy(request, registro);
        return toResponse(repository.save(registro));
    }
    void delete(Long id) { log.info("event=delete_registro_archivado id={}", id); repository.delete(findEntity(id)); }
    private void validarDependencias(RegistroArchivadoRequestDTO request) { client.validarAdministrativo(request.idAdministrativo()); client.validarFicha(request.folioFicha()); client.validarExpediente(request.idExpediente()); }
    private RegistroArchivadoModel findEntity(Long id) { return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Registro archivado no encontrado con id: " + id)); }
    private void copy(RegistroArchivadoRequestDTO r, RegistroArchivadoModel m) { m.folioFicha = r.folioFicha(); m.idAdministrativo = r.idAdministrativo(); m.idExpediente = r.idExpediente(); m.fechaArchivado = r.fechaArchivado(); m.observacion = r.observacion(); }
    private RegistroArchivadoResponseDTO toResponse(RegistroArchivadoModel r) { return new RegistroArchivadoResponseDTO(r.idRegistroArchivado, r.folioFicha, r.idAdministrativo, r.idExpediente, r.fechaArchivado, r.observacion); }
}

@RestController
@RequestMapping("/api/v1/registros-archivados")
class RegistroArchivadoController {
    private final RegistroArchivadoService service;
    RegistroArchivadoController(RegistroArchivadoService service) { this.service = service; }
    @GetMapping ResponseEntity<List<RegistroArchivadoResponseDTO>> findAll() { return ResponseEntity.ok(service.findAll()); }
    @GetMapping("/{id}") ResponseEntity<RegistroArchivadoResponseDTO> findById(@PathVariable Long id) { return ResponseEntity.ok(service.findById(id)); }
    @PostMapping ResponseEntity<RegistroArchivadoResponseDTO> create(@Valid @RequestBody RegistroArchivadoRequestDTO request) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request)); }
    @PutMapping("/{id}") ResponseEntity<RegistroArchivadoResponseDTO> update(@PathVariable Long id, @Valid @RequestBody RegistroArchivadoRequestDTO request) { return ResponseEntity.ok(service.update(id, request)); }
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
