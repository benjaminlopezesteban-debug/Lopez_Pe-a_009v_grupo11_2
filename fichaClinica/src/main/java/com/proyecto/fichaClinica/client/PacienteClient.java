package com.hospital_vm_cl.hospital_vm_ficha.client;

import com.hospital_vm_cl.hospital_vm_ficha.dtos.response.PacienteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

// Cliente Feign que consume el MS de Pacientes
// El name debe coincidir con spring.application.name del MS Paciente
@FeignClient(name = "hospital-vm-paciente", path = "/api/v1/pacientes")
public interface PacienteClient {

    @GetMapping("/{id}")
    PacienteResponse obtenerPacientePorId(@PathVariable Long id);

    // Endpoint para búsqueda por rut — el MS Paciente debe exponer este endpoint
    @GetMapping("/rut/{rut}")
    PacienteResponse obtenerPacientePorRut(@PathVariable String rut);

    // Endpoint para búsqueda por nombres/apellido — el MS Paciente debe exponer este endpoint
    @GetMapping("/buscar")
    List<PacienteResponse> buscarPorNombreOApellido(
            @PathVariable String pnombre,
            @PathVariable String snombre,
            @PathVariable String appaterno
    );
}
