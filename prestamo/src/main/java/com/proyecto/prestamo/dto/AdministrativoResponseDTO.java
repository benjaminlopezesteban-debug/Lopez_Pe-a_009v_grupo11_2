package com.proyecto.prestamo.dto;

public record AdministrativoResponseDTO(
        Long idAdministrativo,
        String rut,
        String nombreCompleto,
        String email,
        String cargo
) {
}
