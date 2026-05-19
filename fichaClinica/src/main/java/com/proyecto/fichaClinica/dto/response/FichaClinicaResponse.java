package com.proyecto.fichaClinica.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

// DTO de salida con los datos de la ficha clínica enriquecidos con paciente y estante
@Data
@Builder
public class FichaClinicaResponse {

    private Long folioFicha;
    private LocalDate fechaCreacion;

    // ID foráneo almacenado en la entidad
    private Long idPaciente;

    // ID foráneo almacenado en la entidad
    private Long idEstante;

    // Objeto completo obtenido desde el MS Paciente
    private PacienteResponse paciente;

    // Objeto completo obtenido desde el MS Estante
    private EstanteResponse estante;
}
