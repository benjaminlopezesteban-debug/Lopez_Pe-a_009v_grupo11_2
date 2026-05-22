package com.proyecto.paciente.dto;

import java.time.LocalDate;

public record PacienteResponseDTO(
        Long idPaciente,
        String numRut,
        String nombreCompleto,
        LocalDate fechaNaci,
        String direccion,
        String nacionalidad
) {}

