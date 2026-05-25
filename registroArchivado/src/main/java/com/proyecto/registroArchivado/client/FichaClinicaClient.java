package com.proyecto.registroArchivado.client;

import com.proyecto.registroArchivado.dto.response.FichaClinicaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hospital-vm-ficha-clinica", url = "${fichaClinica.service.url}", path = "/api/v1/fichas")
public interface FichaClinicaClient {

    @GetMapping("/{folioFicha}")
    FichaClinicaResponse obtenerPorFolio(@PathVariable Long folioFicha);
}
