package com.proyecto.gestionArchivo.dto;

import java.time.LocalDateTime;

public record AuditoriaResponseDTO(
        Long idAuditoria,
        Long idAdministrativo,
        String folioFicha,
        LocalDateTime fechaAuditoria,
        String accion,
        String detalle
) {
}
