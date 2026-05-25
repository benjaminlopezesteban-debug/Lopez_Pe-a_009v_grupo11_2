package com.proyecto.registroArchivado.dto.response;

import java.time.LocalDateTime;

public record RegistroArchivadoResponseDTO(
        Long idRegistroArchivado,
        String folioFicha,
        Long idAdministrativo,
        Long idExpediente,
        LocalDateTime fechaArchivado,
        String observacion
) {}
