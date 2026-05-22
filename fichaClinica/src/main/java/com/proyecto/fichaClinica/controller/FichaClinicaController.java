package com.proyecto.fichaClinica.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyecto.fichaClinica.dto.request.FichaClinicaRequest;
import com.proyecto.fichaClinica.dto.response.FichaClinicaResponse;
import com.proyecto.fichaClinica.service.FichaClinicaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/fichaClinica")
public class FichaClinicaController {

    private final FichaClinicaService fichaClinicaService;

    public FichaClinicaController(FichaClinicaService fichaClinicaService){
        this.fichaClinicaService = fichaClinicaService;
    }

    @GetMapping
    public List<FichaClinicaResponse> listar() {
        return fichaClinicaService.obtenerTodas();
    }

    @GetMapping("/{folioFicha}")
    public ResponseEntity<FichaClinicaResponse> obtenerPorFolio(@PathVariable Long folioFicha) {
        return ResponseEntity.ok(fichaClinicaService.obtenerPorFolio(folioFicha));
    }

    @PostMapping
    public ResponseEntity<FichaClinicaResponse> guardar(@Valid @RequestBody FichaClinicaRequest fichaClinicaRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(fichaClinicaService.guardar(fichaClinicaRequest)); 
    }
}

