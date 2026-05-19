package com.proyecto.fichaClinica.dto.response;

import lombok.Builder;
import lombok.Data;

// DTO de salida especializado para mostrar la ubicación física de una ficha clínica.
// Se usa en los métodos de búsqueda por folioFicha, nombre, apellido o rut.
@Data
@Builder
public class UbicacionFichaResponse {

    private Long folioFicha;

    // Datos del paciente para identificación
    private String rutPaciente;
    private String nombreCompletoPaciente;

    // Ubicación física de la ficha
    private int numEstante;
    private int numBodega;
}
