package com.proyecto.registroIngresoArchivo.dto;

import java.time.LocalDateTime;

public record RegistroIngresoArchivoResponseDTO(
        Long idRegistroIngresoArchivo,
        Long idExpediente,
        Long idAdministrativo,
        LocalDateTime fechaIngreso,
        String observacion
) {}

