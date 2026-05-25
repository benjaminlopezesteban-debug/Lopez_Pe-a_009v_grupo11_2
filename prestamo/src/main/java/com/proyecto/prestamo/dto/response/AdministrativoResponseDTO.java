package com.proyecto.prestamo.dto.response;

public record AdministrativoResponseDTO(
        Long idAdministrativo,
        String rut,
        String nombreCompleto,
        String email,
        String cargo
) {}
