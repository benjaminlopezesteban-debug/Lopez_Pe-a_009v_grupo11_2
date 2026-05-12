package com.proyecto.gestionArchivo.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaAtencionResponseDTO(
        Long idReservaAtencion,
        LocalDate fechaReservada,
        LocalTime horaReservada,
        String especialidad,
        String profesional,
        Long idPaciente
) {
}
