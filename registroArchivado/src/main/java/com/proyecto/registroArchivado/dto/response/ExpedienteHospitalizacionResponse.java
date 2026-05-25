package com.proyecto.registroArchivado.dto.response;

public record ExpedienteHospitalizacionResponse(
        Long idExpediente,
        String codExpediente,
        String rutPaciente,
        boolean digitalizacion,
        Long idBooking
) {}
