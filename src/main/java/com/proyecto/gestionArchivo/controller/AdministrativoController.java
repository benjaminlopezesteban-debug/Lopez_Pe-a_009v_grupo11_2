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

import com.proyecto.gestionArchivo.dto.AdministrativoRequestDTO;
import com.proyecto.gestionArchivo.dto.AdministrativoResponseDTO;
import com.proyecto.gestionArchivo.service.AdministrativoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/administrativos")
public class AdministrativoController {

    private final AdministrativoService administrativoService;

    public AdministrativoController(AdministrativoService administrativoService) {
        this.administrativoService = administrativoService;
    }

    @GetMapping
    public ResponseEntity<List<AdministrativoResponseDTO>> findAll() {
        return ResponseEntity.ok(administrativoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministrativoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(administrativoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AdministrativoResponseDTO> create(@Valid @RequestBody AdministrativoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(administrativoService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdministrativoResponseDTO> update(@PathVariable Long id, @Valid @RequestBody AdministrativoRequestDTO request) {
        return ResponseEntity.ok(administrativoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        administrativoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
