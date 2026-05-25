package com.proyecto.auditoria.dto.response;

import java.time.LocalDate;

public record FichaClinicaResponse(
        Long folioFicha,
        LocalDate fechaCreacion,
        Long idPaciente,
        Long idEstante
) {}
