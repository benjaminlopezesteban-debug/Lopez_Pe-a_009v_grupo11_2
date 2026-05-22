package com.proyecto.expedienteHospitalizacion.client;

import com.proyecto.expedienteHospitalizacion.dto.response.FichaClinicaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

// Cliente Feign que consume el MS de FichaClinica
@FeignClient(name = "ficha-clinica", url = "${fichaClinica.service.url}", path = "/api/v1/fichas")
public interface FichaClinicaClient {

    @GetMapping("/{folioFicha}")
    FichaClinicaResponse obtenerFichaPorFolio(@PathVariable Long folioFicha);

    // Obtiene la ficha de un paciente por su id — necesario para enriquecer el expediente
    @GetMapping("/paciente/{idPaciente}")
    List<FichaClinicaResponse> obtenerFichasPorPaciente(@PathVariable Long idPaciente);
}
