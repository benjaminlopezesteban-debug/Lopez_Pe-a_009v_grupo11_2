package com.proyecto.prestamo.client;

import com.proyecto.prestamo.dto.response.AdministrativoResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hospital-vm-administrativo", url = "${administrativo.service.url}", path = "/api/v1/administrativos")
public interface AdministrativoClient {

    @GetMapping("/{id}")
    AdministrativoResponseDTO obtenerPorId(@PathVariable Long id);
}
