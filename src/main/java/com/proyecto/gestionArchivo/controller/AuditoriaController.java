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

import com.proyecto.gestionArchivo.dto.AuditoriaRequestDTO;
import com.proyecto.gestionArchivo.dto.AuditoriaResponseDTO;
import com.proyecto.gestionArchivo.service.AuditoriaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auditorias")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public ResponseEntity<List<AuditoriaResponseDTO>> findAll() {
        return ResponseEntity.ok(auditoriaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditoriaResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(auditoriaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AuditoriaResponseDTO> create(@Valid @RequestBody AuditoriaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auditoriaService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuditoriaResponseDTO> update(@PathVariable Long id, @Valid @RequestBody AuditoriaRequestDTO request) {
        return ResponseEntity.ok(auditoriaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        auditoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
