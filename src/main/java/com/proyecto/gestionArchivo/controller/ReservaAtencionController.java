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

import com.proyecto.gestionArchivo.dto.ReservaAtencionRequestDTO;
import com.proyecto.gestionArchivo.dto.ReservaAtencionResponseDTO;
import com.proyecto.gestionArchivo.service.ReservaAtencionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/reservas-atencion")
public class ReservaAtencionController {

    private final ReservaAtencionService reservaAtencionService;

    public ReservaAtencionController(ReservaAtencionService reservaAtencionService) {
        this.reservaAtencionService = reservaAtencionService;
    }

    @GetMapping
    public ResponseEntity<List<ReservaAtencionResponseDTO>> findAll() {
        return ResponseEntity.ok(reservaAtencionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaAtencionResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(reservaAtencionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ReservaAtencionResponseDTO> create(@Valid @RequestBody ReservaAtencionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaAtencionService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaAtencionResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ReservaAtencionRequestDTO request) {
        return ResponseEntity.ok(reservaAtencionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservaAtencionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
