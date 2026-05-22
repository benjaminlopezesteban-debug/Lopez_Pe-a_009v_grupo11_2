package com.proyecto.prestamo.controller;

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

import com.proyecto.prestamo.dto.PrestamoRequestDTO;
import com.proyecto.prestamo.dto.PrestamoResponseDTO;
import com.proyecto.prestamo.service.PrestamoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @GetMapping
    public ResponseEntity<List<PrestamoResponseDTO>> findAll() {
        return ResponseEntity.ok(prestamoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestamoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PrestamoResponseDTO> create(@Valid @RequestBody PrestamoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prestamoService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrestamoResponseDTO> update(@PathVariable Long id, @Valid @RequestBody PrestamoRequestDTO request) {
        return ResponseEntity.ok(prestamoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        prestamoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

