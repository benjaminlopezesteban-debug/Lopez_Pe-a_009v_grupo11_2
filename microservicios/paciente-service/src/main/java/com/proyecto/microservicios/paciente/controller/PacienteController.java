package com.proyecto.microservicios.paciente.controller;

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

import com.proyecto.microservicios.paciente.dto.PacienteRequestDTO;
import com.proyecto.microservicios.paciente.dto.PacienteResponseDTO;
import com.proyecto.microservicios.paciente.service.PacienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> findAll() {
        return ResponseEntity.ok(pacienteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PacienteResponseDTO> create(@Valid @RequestBody PacienteRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> update(@PathVariable Long id, @Valid @RequestBody PacienteRequestDTO request) {
        return ResponseEntity.ok(pacienteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pacienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
