package com.proyecto.prestamo.dto.response;

import java.time.LocalDate;

public record FichaClinicaResponseDTO(
        String folioFicha,
        LocalDate fechaCreacion,
        Long idPaciente,
        String pacienteNombre,
        Long idEstante,
        Integer numEstante
) {}
