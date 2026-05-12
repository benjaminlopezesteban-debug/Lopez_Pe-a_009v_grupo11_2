package com.proyecto.gestionArchivo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdministrativoRequestDTO(
        @NotBlank(message = "El rut es obligatorio")
        @Size(max = 13, message = "El rut no puede superar 13 caracteres")
        String rut,

        @NotBlank(message = "El primer nombre es obligatorio")
        String pnombre,

        String snombre,

        @NotBlank(message = "El apellido paterno es obligatorio")
        String papellido,

        @NotBlank(message = "El apellido materno es obligatorio")
        String sapellido,

        LocalDate fechaNaci,
        LocalDate fechaContrato,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe ser valido")
        String email,

        @NotBlank(message = "El cargo es obligatorio")
        String cargo
) {
}
