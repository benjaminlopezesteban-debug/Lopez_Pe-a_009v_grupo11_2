package com.proyecto.fichaClinica.dto.response;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

// DTO que representa los datos del paciente recibidos desde el MS Paciente
@Data
@Builder
public class PacienteResponse {

    private Long id;              // id del paciente en MS Paciente
    private String numRut;        // rut del paciente
    private String pnombre;
    private String snombre;
    private String papellido;
    private String sapellido;
    private LocalDate fechaNaci;
    private String direccion;
    private String nacionalidad;
}
