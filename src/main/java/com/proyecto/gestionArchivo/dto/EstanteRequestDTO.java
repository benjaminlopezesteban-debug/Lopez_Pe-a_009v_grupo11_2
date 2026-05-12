package com.proyecto.gestionArchivo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EstanteRequestDTO(
        @NotNull(message = "El numero de estante es obligatorio")
        @Min(value = 1, message = "El numero de estante debe ser mayor a cero")
        Integer numEstante,

        @NotNull(message = "El numero de bodega es obligatorio")
        @Min(value = 1, message = "El numero de bodega debe ser mayor a cero")
        Integer numBodega,

        @Size(max = 120, message = "La ubicacion no puede superar 120 caracteres")
        String ubicacion
) {
}
