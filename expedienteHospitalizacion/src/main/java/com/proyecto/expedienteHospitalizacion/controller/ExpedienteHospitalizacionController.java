package com.proyecto.expedienteHospitalizacion.controller;

import com.proyecto.expedienteHospitalizacion.dto.request.ExpedienteHospitalizacionRequest;
import com.proyecto.expedienteHospitalizacion.dto.response.ExpedienteHospitalizacionResponse;
import com.proyecto.expedienteHospitalizacion.service.ExpedienteHospitalizacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expedientes")
public class ExpedienteHospitalizacionController {

    private final ExpedienteHospitalizacionService expedienteService;

    public ExpedienteHospitalizacionController(ExpedienteHospitalizacionService expedienteService) {
        this.expedienteService = expedienteService;
    }

    @GetMapping
    public ResponseEntity<List<ExpedienteHospitalizacionResponse>> listar() {
        return ResponseEntity.ok(expedienteService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpedienteHospitalizacionResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(expedienteService.obtenerPorId(id));
    }

    @GetMapping("/codigo/{codExpediente}")
    public ResponseEntity<ExpedienteHospitalizacionResponse> obtenerPorCodigo(
            @PathVariable String codExpediente) {
        return ResponseEntity.ok(expedienteService.obtenerPorCodigo(codExpediente));
    }

    @GetMapping("/paciente/{rutPaciente}")
    public ResponseEntity<List<ExpedienteHospitalizacionResponse>> obtenerPorRutPaciente(
            @PathVariable String rutPaciente) {
        return ResponseEntity.ok(expedienteService.obtenerPorRutPaciente(rutPaciente));
    }

    @GetMapping("/digitalizacion/{estado}")
    public ResponseEntity<List<ExpedienteHospitalizacionResponse>> obtenerPorDigitalizacion(
            @PathVariable boolean estado) {
        return ResponseEntity.ok(expedienteService.obtenerPorDigitalizacion(estado));
    }

    @PostMapping
    public ResponseEntity<ExpedienteHospitalizacionResponse> guardar(
            @Valid @RequestBody ExpedienteHospitalizacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expedienteService.guardar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpedienteHospitalizacionResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ExpedienteHospitalizacionRequest request) {
        return ResponseEntity.ok(expedienteService.actualizar(id, request));
    }

    @PatchMapping("/{id}/registro-archivado")
    public ResponseEntity<ExpedienteHospitalizacionResponse> asignarRegistroArchivado(
            @PathVariable Long id,
            @RequestParam Long idRegistroArchivado) {
        return ResponseEntity.ok(expedienteService.asignarRegistroArchivado(id, idRegistroArchivado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        expedienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

