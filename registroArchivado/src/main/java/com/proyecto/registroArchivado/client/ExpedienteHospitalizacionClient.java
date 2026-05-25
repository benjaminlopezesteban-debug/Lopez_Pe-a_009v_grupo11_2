package com.proyecto.registroArchivado.client;

import com.proyecto.registroArchivado.dto.response.ExpedienteHospitalizacionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hospital-vm-expediente-hospitalizacion", url = "${expedienteHospitalizacion.service.url}", path = "/api/v1/expedientes")
public interface ExpedienteHospitalizacionClient {

    @GetMapping("/{id}")
    ExpedienteHospitalizacionResponse obtenerPorId(@PathVariable Long id);

    @PatchMapping("/{id}/registro-archivado")
    void asignarRegistroArchivado(@PathVariable Long id, @RequestParam Long idRegistroArchivado);
}
