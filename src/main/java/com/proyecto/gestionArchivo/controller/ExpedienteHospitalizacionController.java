package com.proyecto.gestionArchivo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.gestionArchivo.dto.ExpedienteHospitalizacionRequestDTO;
import com.proyecto.gestionArchivo.dto.ExpedienteHospitalizacionResponseDTO;
import com.proyecto.gestionArchivo.service.ExpedienteHospitalizacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/expedientes-hospitalizacion")
public class ExpedienteHospitalizacionController {

    private final ExpedienteHospitalizacionService expedienteService;

    public ExpedienteHospitalizacionController(ExpedienteHospitalizacionService expedienteService) {
        this.expedienteService = expedienteService;
    }

    @GetMapping
    public ResponseEntity<List<ExpedienteHospitalizacionResponseDTO>> findAll() {
        return ResponseEntity.ok(expedienteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpedienteHospitalizacionResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(expedienteService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ExpedienteHospitalizacionResponseDTO> create(@Valid @RequestBody ExpedienteHospitalizacionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expedienteService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpedienteHospitalizacionResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ExpedienteHospitalizacionRequestDTO request) {
        return ResponseEntity.ok(expedienteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        expedienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
