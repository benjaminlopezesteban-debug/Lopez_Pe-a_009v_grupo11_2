package com.proyecto.prestamo.client;

import com.proyecto.prestamo.dto.response.FichaClinicaResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hospital-vm-ficha-clinica", url = "${fichaClinica.service.url}", path = "/api/v1/fichas")
public interface FichaClinicaClient {

    @GetMapping("/{folioFicha}")
    FichaClinicaResponseDTO obtenerPorFolio(@PathVariable Long folioFicha);
}
