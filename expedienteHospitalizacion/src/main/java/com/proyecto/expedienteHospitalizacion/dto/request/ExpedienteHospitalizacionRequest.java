package com.proyecto.expedienteHospitalizacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

// DTO de entrada para crear o actualizar un expediente de hospitalización
@Data
public class ExpedienteHospitalizacionRequest {

    @NotBlank(message = "El código de expediente es obligatorio")
    private String codExpediente;

    @NotBlank(message = "El rut del paciente es obligatorio")
    private String rutPaciente;

    @NotNull(message = "El campo digitalización es obligatorio")
    private Boolean digitalizacion;

    @NotNull(message = "El id de la reserva es obligatorio")
    @Positive(message = "El id de la reserva debe ser mayor a 0")
    private Long idBooking;

    // Opcional al crear — se asigna cuando el expediente es archivado
    private Long idRegistroArchivado;
}
