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

import com.proyecto.gestionArchivo.dto.EstanteRequestDTO;
import com.proyecto.gestionArchivo.dto.EstanteResponseDTO;
import com.proyecto.gestionArchivo.service.EstanteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/estantes")
public class EstanteController {

    private final EstanteService estanteService;

    public EstanteController(EstanteService estanteService) {
        this.estanteService = estanteService;
    }

    @GetMapping
    public ResponseEntity<List<EstanteResponseDTO>> findAll() {
        return ResponseEntity.ok(estanteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstanteResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(estanteService.findById(id));
    }

    @PostMapping
    public ResponseEntity<EstanteResponseDTO> create(@Valid @RequestBody EstanteRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estanteService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstanteResponseDTO> update(@PathVariable Long id, @Valid @RequestBody EstanteRequestDTO request) {
        return ResponseEntity.ok(estanteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        estanteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
