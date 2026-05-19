package com.hospital_vm_cl.hospital_vm_ficha.client;

import com.hospital_vm_cl.hospital_vm_ficha.dtos.response.EstanteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

// Cliente Feign que consume el MS de Estantes
// El name debe coincidir con spring.application.name del MS Estante
@FeignClient(name = "hospital-vm-estante", path = "/api/v1/estantes")
public interface EstanteClient {

    @GetMapping("/{id}")
    EstanteResponse obtenerEstantePorId(@PathVariable Long id);

    @GetMapping
    List<EstanteResponse> listarTodos();
}
