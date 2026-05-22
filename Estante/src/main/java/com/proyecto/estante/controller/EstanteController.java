package com.proyecto.estante.controller;

import com.proyecto.estante.dto.request.EstanteRequest;
import com.proyecto.estante.dto.response.EstanteResponse;
import com.proyecto.estante.service.EstanteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/estantes")
public class EstanteController {

    private final EstanteService estanteService;

    public EstanteController(EstanteService estanteService) {
        this.estanteService = estanteService;
    }

    @GetMapping
    public ResponseEntity<List<EstanteResponse>> listar() {
        return ResponseEntity.ok(estanteService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstanteResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(estanteService.obtenerPorId(id));
    }

    @GetMapping("/numero/{numEstante}")
    public ResponseEntity<EstanteResponse> obtenerPorNumEstante(@PathVariable int numEstante) {
        return ResponseEntity.ok(estanteService.obtenerPorNumEstante(numEstante));
    }

    @GetMapping("/bodega/{numBodega}")
    public ResponseEntity<EstanteResponse> obtenerPorNumBodega(@PathVariable int numBodega) {
        return ResponseEntity.ok(estanteService.obtenerPorNumBodega(numBodega));
    }

    @PostMapping
    public ResponseEntity<EstanteResponse> guardar(@Valid @RequestBody EstanteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estanteService.guardar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstanteResponse> actualizar(@PathVariable Long id, @Valid @RequestBody EstanteRequest request) {
        return ResponseEntity.ok(estanteService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        estanteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

