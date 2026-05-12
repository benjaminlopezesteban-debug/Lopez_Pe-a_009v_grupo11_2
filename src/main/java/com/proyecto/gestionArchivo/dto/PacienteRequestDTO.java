package com.proyecto.gestionArchivo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record PacienteRequestDTO(
        @NotBlank(message = "El rut es obligatorio")
        @Size(max = 13, message = "El rut no puede superar 13 caracteres")
        String numRut,

        @NotBlank(message = "El primer nombre es obligatorio")
        @Size(max = 100, message = "El primer nombre no puede superar 100 caracteres")
        String pnombre,

        @Size(max = 100, message = "El segundo nombre no puede superar 100 caracteres")
        String snombre,

        @NotBlank(message = "El apellido paterno es obligatorio")
        @Size(max = 150, message = "El apellido paterno no puede superar 150 caracteres")
        String papellido,

        @NotBlank(message = "El apellido materno es obligatorio")
        @Size(max = 150, message = "El apellido materno no puede superar 150 caracteres")
        String sapellido,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser pasada")
        LocalDate fechaNaci,

        @NotBlank(message = "La direccion es obligatoria")
        @Size(max = 150, message = "La direccion no puede superar 150 caracteres")
        String direccion,

        @NotBlank(message = "La nacionalidad es obligatoria")
        @Size(max = 25, message = "La nacionalidad no puede superar 25 caracteres")
        String nacionalidad
) {
}
