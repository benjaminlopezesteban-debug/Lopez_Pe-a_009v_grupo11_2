package com.proyecto.gestionArchivo.controler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.proyecto.gestionArchivo.dto.LoginRequest;
import com.proyecto.gestionArchivo.dto.LoginResponse;
import com.proyecto.gestionArchivo.security.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        String token = jwtService.generateToken(authentication.getName());
        LoginResponse response = new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationMillis(),
                authentication.getName(),
                true
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<LoginResponse> validate(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new BadCredentialsException("Token ausente");
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        if (!jwtService.isTokenValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalido o expirado");
        }

        LoginResponse response = new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationMillis(),
                jwtService.extractUsername(token),
                true
        );

        return ResponseEntity.ok(response);
    }
}
