package com.proyecto.gestionArchivo.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuditoriaRequestDTO(
        @NotNull(message = "El administrativo es obligatorio")
        Long idAdministrativo,

        @NotBlank(message = "La ficha clinica es obligatoria")
        String folioFicha,

        @NotNull(message = "La fecha de auditoria es obligatoria")
        LocalDateTime fechaAuditoria,

        @NotBlank(message = "La accion es obligatoria")
        String accion,

        String detalle
) {
}
