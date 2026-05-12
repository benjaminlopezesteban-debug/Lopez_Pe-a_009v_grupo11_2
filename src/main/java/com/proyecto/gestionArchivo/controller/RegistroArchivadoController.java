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

import com.proyecto.gestionArchivo.dto.RegistroArchivadoRequestDTO;
import com.proyecto.gestionArchivo.dto.RegistroArchivadoResponseDTO;
import com.proyecto.gestionArchivo.service.RegistroArchivadoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/registros-archivados")
public class RegistroArchivadoController {

    private final RegistroArchivadoService registroArchivadoService;

    public RegistroArchivadoController(RegistroArchivadoService registroArchivadoService) {
        this.registroArchivadoService = registroArchivadoService;
    }

    @GetMapping
    public ResponseEntity<List<RegistroArchivadoResponseDTO>> findAll() {
        return ResponseEntity.ok(registroArchivadoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroArchivadoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(registroArchivadoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<RegistroArchivadoResponseDTO> create(@Valid @RequestBody RegistroArchivadoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registroArchivadoService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistroArchivadoResponseDTO> update(@PathVariable Long id, @Valid @RequestBody RegistroArchivadoRequestDTO request) {
        return ResponseEntity.ok(registroArchivadoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        registroArchivadoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
