package com.proyecto.gestionArchivo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FichaClinicaRequestDTO(
        @NotBlank(message = "El folio de ficha es obligatorio")
        String folioFicha,

        @NotNull(message = "La fecha de creacion es obligatoria")
        LocalDate fechaCreacion,

        @NotNull(message = "El paciente es obligatorio")
        Long idPaciente,

        @NotNull(message = "El estante es obligatorio")
        Long idEstante
) {
}
