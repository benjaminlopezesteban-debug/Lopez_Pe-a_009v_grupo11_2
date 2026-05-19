package com.proyecto.fichaClinica.dto.response;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

// DTO que representa los datos del paciente recibidos desde el MS Paciente
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
