package com.proyecto.gestionArchivo.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReservaAtencionRequestDTO(
        @NotNull(message = "La fecha reservada es obligatoria")
        LocalDate fechaReservada,

        @NotNull(message = "La hora reservada es obligatoria")
        LocalTime horaReservada,

        @NotBlank(message = "La especialidad es obligatoria")
        String especialidad,

        @NotBlank(message = "El profesional es obligatorio")
        String profesional,

        @NotNull(message = "El paciente es obligatorio")
        Long idPaciente
) {
}
