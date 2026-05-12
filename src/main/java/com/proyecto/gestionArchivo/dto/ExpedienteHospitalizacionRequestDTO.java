package com.proyecto.gestionArchivo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExpedienteHospitalizacionRequestDTO(
        @NotBlank(message = "El codigo de expediente es obligatorio")
        String codExpediente,

        @NotBlank(message = "El rut del paciente es obligatorio")
        String rutPaciente,

        @NotNull(message = "La digitalizacion es obligatoria")
        Boolean digitalizacion,

        @NotNull(message = "La reserva de atencion es obligatoria")
        Long idReservaAtencion
) {
}
