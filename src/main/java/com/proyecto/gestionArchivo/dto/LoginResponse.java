package com.proyecto.gestionArchivo.dto;

public record LoginResponse(
        String token,
        String tokenType,
        Long expiresIn,
        String username,
        boolean valid
) {
}
