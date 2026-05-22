package com.proyecto.registroArchivado.controller;

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

import com.proyecto.registroArchivado.dto.RegistroArchivadoRequestDTO;
import com.proyecto.registroArchivado.dto.RegistroArchivadoResponseDTO;
import com.proyecto.registroArchivado.service.RegistroArchivadoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/registros-archivado")
public class RegistroArchivadoController {

    private final RegistroArchivadoService service;

    public RegistroArchivadoController(RegistroArchivadoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RegistroArchivadoResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroArchivadoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<RegistroArchivadoResponseDTO> create(@Valid @RequestBody RegistroArchivadoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistroArchivadoResponseDTO> update(@PathVariable Long id, @Valid @RequestBody RegistroArchivadoRequestDTO request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

