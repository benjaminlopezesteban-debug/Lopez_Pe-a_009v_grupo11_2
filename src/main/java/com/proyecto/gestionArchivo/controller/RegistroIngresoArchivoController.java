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

import com.proyecto.gestionArchivo.dto.RegistroIngresoArchivoRequestDTO;
import com.proyecto.gestionArchivo.dto.RegistroIngresoArchivoResponseDTO;
import com.proyecto.gestionArchivo.service.RegistroIngresoArchivoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/registros-ingreso-archivo")
public class RegistroIngresoArchivoController {

    private final RegistroIngresoArchivoService registroIngresoArchivoService;

    public RegistroIngresoArchivoController(RegistroIngresoArchivoService registroIngresoArchivoService) {
        this.registroIngresoArchivoService = registroIngresoArchivoService;
    }

    @GetMapping
    public ResponseEntity<List<RegistroIngresoArchivoResponseDTO>> findAll() {
        return ResponseEntity.ok(registroIngresoArchivoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroIngresoArchivoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(registroIngresoArchivoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<RegistroIngresoArchivoResponseDTO> create(@Valid @RequestBody RegistroIngresoArchivoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registroIngresoArchivoService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistroIngresoArchivoResponseDTO> update(@PathVariable Long id, @Valid @RequestBody RegistroIngresoArchivoRequestDTO request) {
        return ResponseEntity.ok(registroIngresoArchivoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        registroIngresoArchivoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
