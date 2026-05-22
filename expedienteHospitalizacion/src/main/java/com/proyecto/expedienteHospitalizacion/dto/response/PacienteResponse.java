package com.proyecto.expedienteHospitalizacion.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

// Espejo del DTO que expone el MS Paciente
@Data
@Builder
public class PacienteResponse {

    private Long id;
    private String rut;
    private String pnombre;
    private String snombre;
    private String appaterno;
    private String apmaterno;
    private LocalDate bornDate;
    private String direction;
    private String nacionality;
}
