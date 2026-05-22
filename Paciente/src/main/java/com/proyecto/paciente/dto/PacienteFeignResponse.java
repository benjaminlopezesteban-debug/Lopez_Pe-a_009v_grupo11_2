package com.proyecto.paciente.dto;

import java.time.LocalDate;

public record PacienteFeignResponse(
        Long id,
        String numRut,
        String pnombre,
        String snombre,
        String papellido,
        String sapellido,
        LocalDate fechaNaci,
        String direccion,
        String nacionalidad
) {}

