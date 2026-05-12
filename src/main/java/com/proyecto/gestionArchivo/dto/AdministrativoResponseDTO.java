package com.proyecto.gestionArchivo.dto;

public record AdministrativoResponseDTO(
        Long idAdministrativo,
        String rut,
        String nombreCompleto,
        String email,
        String cargo
) {
}
