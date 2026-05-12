package com.proyecto.gestionArchivo.dto;

public record ExpedienteHospitalizacionResponseDTO(
        Long idExpediente,
        String codExpediente,
        String rutPaciente,
        Boolean digitalizacion,
        Long idReservaAtencion
) {
}
