package com.proyecto.gestionArchivo.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record RegistroIngresoArchivoRequestDTO(
        @NotNull(message = "El expediente es obligatorio")
        Long idExpediente,

        @NotNull(message = "El administrativo es obligatorio")
        Long idAdministrativo,

        @NotNull(message = "La fecha de ingreso es obligatoria")
        LocalDateTime fechaIngreso,

        String observacion
) {
}
