package com.proyecto.registroIngresoArchivo.client;

import com.proyecto.registroIngresoArchivo.dto.response.ExpedienteHospitalizacionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hospital-vm-expediente-hospitalizacion", url = "${expedienteHospitalizacion.service.url}", path = "/api/v1/expedientes")
public interface ExpedienteHospitalizacionClient {

    @GetMapping("/{id}")
    ExpedienteHospitalizacionResponse obtenerPorId(@PathVariable Long id);
}
