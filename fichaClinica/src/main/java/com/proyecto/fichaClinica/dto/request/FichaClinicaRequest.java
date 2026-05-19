package com.proyecto.fichaClinica.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

// DTO de entrada para crear o actualizar una ficha clínica
@Data
public class FichaClinicaRequest {

    @NotNull(message = "El id del paciente es obligatorio")
    @Positive(message = "El id del paciente debe ser mayor a 0")
    private Long idPaciente;

    @NotNull(message = "El id del estante es obligatorio")
    @Positive(message = "El id del estante debe ser mayor a 0")
    private Long idEstante;
}
