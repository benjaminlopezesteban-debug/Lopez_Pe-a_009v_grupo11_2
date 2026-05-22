package com.proyecto.registroIngresoArchivo.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegistroIngresoArchivoRequestDTO(
        @NotNull @Positive Long idExpediente,
        @NotNull @Positive Long idAdministrativo,
        @NotNull LocalDateTime fechaIngreso,
        String observacion
) {}

