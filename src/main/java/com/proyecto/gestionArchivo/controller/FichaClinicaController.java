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

import com.proyecto.gestionArchivo.dto.FichaClinicaRequestDTO;
import com.proyecto.gestionArchivo.dto.FichaClinicaResponseDTO;
import com.proyecto.gestionArchivo.service.FichaClinicaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/fichas-clinicas")
public class FichaClinicaController {

    private final FichaClinicaService fichaClinicaService;

    public FichaClinicaController(FichaClinicaService fichaClinicaService) {
        this.fichaClinicaService = fichaClinicaService;
    }

    @GetMapping
    public ResponseEntity<List<FichaClinicaResponseDTO>> findAll() {
        return ResponseEntity.ok(fichaClinicaService.findAll());
    }

    @GetMapping("/{folio}")
    public ResponseEntity<FichaClinicaResponseDTO> findByFolio(@PathVariable String folio) {
        return ResponseEntity.ok(fichaClinicaService.findByFolio(folio));
    }

    @PostMapping
    public ResponseEntity<FichaClinicaResponseDTO> create(@Valid @RequestBody FichaClinicaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fichaClinicaService.create(request));
    }

    @PutMapping("/{folio}")
    public ResponseEntity<FichaClinicaResponseDTO> update(@PathVariable String folio, @Valid @RequestBody FichaClinicaRequestDTO request) {
        return ResponseEntity.ok(fichaClinicaService.update(folio, request));
    }

    @DeleteMapping("/{folio}")
    public ResponseEntity<Void> delete(@PathVariable String folio) {
        fichaClinicaService.delete(folio);
        return ResponseEntity.noContent().build();
    }
}
