package com.proyecto.fichaClinica.dto.response;
import lombok.Builder;
import lombok.Data;

// DTO que representa los datos del estante recibidos desde el MS Estante
@Data
@Builder
public class EstanteResponse {

    private Long idEstante;
    private int numEstante;
    private int numBodega;
}
