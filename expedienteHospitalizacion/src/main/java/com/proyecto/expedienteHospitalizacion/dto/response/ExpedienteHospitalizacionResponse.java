package com.proyecto.expedienteHospitalizacion.dto.response;

import lombok.Builder;
import lombok.Data;

// DTO de salida con los datos del expediente enriquecidos con reserva y ficha clínica
@Data
@Builder
public class ExpedienteHospitalizacionResponse {

    private Long idExpediente;
    private String codExpediente;
    private String rutPaciente;
    private boolean digitalizacion;

    // IDs foráneos almacenados en la entidad
    private Long idBooking;
    private Long idRegistroArchivado;

    // Objeto completo obtenido desde el MS ReservaAtencion
    private ReservaHoraResponse reserva;

    // Objeto completo obtenido desde el MS FichaClinica
    private FichaClinicaResponse fichaClinica;
}
