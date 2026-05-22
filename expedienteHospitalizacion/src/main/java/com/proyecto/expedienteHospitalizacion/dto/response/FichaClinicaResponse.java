package com.proyecto.expedienteHospitalizacion.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

// Espejo del DTO que expone el MS FichaClinica
@Data
@Builder
public class FichaClinicaResponse {

    private Long folioFicha;
    private LocalDate fechaCreacion;
    private Long idPaciente;
    private Long idEstante;
    private PacienteResponse paciente;
}
