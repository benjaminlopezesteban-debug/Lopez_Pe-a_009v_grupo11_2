package com.proyecto.administrativo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.administrativo.model.AdministrativoModel;
import com.proyecto.administrativo.service.AdministrativoService;

@RestController
@RequestMapping("/api/v1/administrativos")
public class AdministrativoController {

    private final AdministrativoService administrativoService;

    public AdministrativoController(AdministrativoService administrativoService) {
        this.administrativoService = administrativoService;
    }

    @GetMapping
    public ResponseEntity<List<AdministrativoModel>> listAll() {
        return ResponseEntity.ok(administrativoService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministrativoModel> findById(@PathVariable Long id) {
        return ResponseEntity.ok(administrativoService.findById(id));
    }

    @GetMapping("/rut/{rut}")
    public ResponseEntity<AdministrativoModel> findByRut(@PathVariable String rut) {
        return ResponseEntity.ok(administrativoService.findByRut(rut));
    }

    @GetMapping("/cargo/{charge}")
    public ResponseEntity<List<AdministrativoModel>> findByCharge(@PathVariable String charge) {
        return ResponseEntity.ok(administrativoService.findByCharge(charge));
    }

    @PostMapping
    public ResponseEntity<AdministrativoModel> save(@RequestBody AdministrativoModel administrativo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(administrativoService.save(administrativo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        administrativoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
