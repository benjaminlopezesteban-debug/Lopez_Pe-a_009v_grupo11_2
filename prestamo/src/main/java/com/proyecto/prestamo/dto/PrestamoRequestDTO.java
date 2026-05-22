package com.proyecto.prestamo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PrestamoRequestDTO(
        @NotNull(message = "El administrativo es obligatorio")
        Long idAdministrativo,

        @NotBlank(message = "La ficha clinica es obligatoria")
        String folioFicha,

        @NotNull(message = "La fecha de prestamo es obligatoria")
        LocalDate fechaPrestamo,

        LocalDate fechaDevolucion,

        @NotBlank(message = "El estado es obligatorio")
        String estado
) {
}
