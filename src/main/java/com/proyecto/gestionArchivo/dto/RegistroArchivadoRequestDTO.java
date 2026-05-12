package com.proyecto.gestionArchivo.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistroArchivadoRequestDTO(
        @NotBlank(message = "La ficha clinica es obligatoria")
        String folioFicha,

        @NotNull(message = "El administrativo es obligatorio")
        Long idAdministrativo,

        @NotNull(message = "El expediente es obligatorio")
        Long idExpediente,

        @NotNull(message = "La fecha de archivado es obligatoria")
        LocalDateTime fechaArchivado,

        String observacion
) {
}
