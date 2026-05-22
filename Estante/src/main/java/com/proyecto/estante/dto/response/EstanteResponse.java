package com.proyecto.estante.dto.response;

import lombok.Builder;
import lombok.Data;

// DTO de salida con los datos de un estante
@Data
@Builder
public class EstanteResponse {

    private Long idEstante;
    private int numEstante;
    private int numBodega;
}
