package com.proyecto.estante.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// DTO de entrada para crear o actualizar un estante
@Data
public class EstanteRequest {

    @NotNull(message = "El número de estante es obligatorio")
    @Min(value = 1, message = "El número de estante debe ser mayor a 0")
    private Integer numEstante;

    @NotNull(message = "El número de bodega es obligatorio")
    @Min(value = 1, message = "El número de bodega debe ser mayor a 0")
    private Integer numBodega;
}
