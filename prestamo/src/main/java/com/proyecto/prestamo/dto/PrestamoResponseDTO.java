package com.proyecto.prestamo.dto;

import java.time.LocalDate;

public record PrestamoResponseDTO(
        Long idPrestamo,
        Long idAdministrativo,
        String folioFicha,
        LocalDate fechaPrestamo,
        LocalDate fechaDevolucion,
        String estado
) {
}
