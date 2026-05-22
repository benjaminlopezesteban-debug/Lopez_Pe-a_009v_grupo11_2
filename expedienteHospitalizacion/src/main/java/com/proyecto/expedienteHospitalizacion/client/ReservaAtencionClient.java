package com.proyecto.expedienteHospitalizacion.client;

import com.proyecto.expedienteHospitalizacion.dto.response.ReservaHoraResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// Cliente Feign que consume el MS de ReservaAtencion
@FeignClient(name = "reserva-atencion", url = "${reservaAtencion.service.url}", path = "/api/v1/reservas")
public interface ReservaAtencionClient {

    @GetMapping("/{id}")
    ReservaHoraResponse obtenerReservaPorId(@PathVariable Long id);

    @GetMapping("/paciente/{idPaciente}")
    List<ReservaHoraResponse> obtenerReservasPorPaciente(@PathVariable Long idPaciente);

    // Actualiza el id_expediente en la reserva una vez que el expediente fue creado
    // El MS ReservaAtencion debe exponer este endpoint
    @PutMapping("/{id}/expediente")
    void asignarExpediente(@PathVariable Long id, @RequestParam Long idExpediente);
}
