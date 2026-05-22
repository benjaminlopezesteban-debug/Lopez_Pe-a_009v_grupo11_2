package com.proyecto.fichaClinica.client;

import com.proyecto.fichaClinica.dto.response.PacienteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// Cliente Feign que consume el MS de Pacientes
// El name debe coincidir con spring.application.name del MS Paciente
@FeignClient(name = "paciente", path = "/api/v1/pacientes")
public interface PacienteClient {

    @GetMapping("/{id}")
    PacienteResponse obtenerPacientePorId(@PathVariable Long id);

    // Endpoint para búsqueda por rut — el MS Paciente debe exponer este endpoint
    @GetMapping("/rut/{rut}")
    PacienteResponse obtenerPacientePorRut(@PathVariable String rut);

    // Endpoint para búsqueda por nombres/apellido — el MS Paciente debe exponer este endpoint
    @GetMapping(value = "/buscar")
    List<PacienteResponse> buscarPorNombreOApellido(
            @RequestParam String pnombre,
            @RequestParam(required = false) String snombre,
            @RequestParam String appaterno
    );
}
