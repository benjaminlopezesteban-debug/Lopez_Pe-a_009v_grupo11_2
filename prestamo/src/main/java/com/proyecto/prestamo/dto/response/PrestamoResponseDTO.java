package com.proyecto.prestamo.dto.response;

import java.time.LocalDate;

public record PrestamoResponseDTO(
        Long idPrestamo,
        Long idAdministrativo,
        String folioFicha,
        LocalDate fechaPrestamo,
        LocalDate fechaDevolucion,
        String estado
) {}
