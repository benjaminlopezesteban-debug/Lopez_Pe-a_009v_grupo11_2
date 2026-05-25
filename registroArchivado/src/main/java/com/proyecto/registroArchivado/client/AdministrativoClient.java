package com.proyecto.registroArchivado.client;

import com.proyecto.registroArchivado.dto.response.AdministrativoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hospital-vm-administrativo", url = "${administrativo.service.url}", path = "/api/v1/administrativos")
public interface AdministrativoClient {

    @GetMapping("/{id}")
    AdministrativoResponse obtenerPorId(@PathVariable Long id);
}
