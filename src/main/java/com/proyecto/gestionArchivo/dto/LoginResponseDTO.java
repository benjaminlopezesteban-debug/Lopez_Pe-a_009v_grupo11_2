package com.proyecto.gestionArchivo.dto;

public record LoginResponseDTO(
        String token,
        String type,
        String username,
        String role
) {
}
