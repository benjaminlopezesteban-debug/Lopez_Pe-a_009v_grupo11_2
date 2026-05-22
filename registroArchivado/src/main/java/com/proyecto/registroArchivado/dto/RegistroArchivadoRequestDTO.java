package com.proyecto.registroArchivado.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegistroArchivadoRequestDTO(
        @NotBlank(message = "El folio de la ficha es obligatorio")
        String folioFicha,

        @NotNull @Positive
        Long idAdministrativo,

        @NotNull @Positive
        Long idExpediente,

        @NotNull
        LocalDateTime fechaArchivado,

        String observacion
) {}

