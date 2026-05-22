package com.proyecto.estante.service;

import com.proyecto.estante.dto.request.EstanteRequest;
import com.proyecto.estante.dto.response.EstanteResponse;
import com.proyecto.estante.exceptions.NotFoundException;
import com.proyecto.estante.exceptions.ConflictException;
import com.proyecto.estante.model.EstanteModel;
import com.proyecto.estante.repository.EstanteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Contiene la lógica de negocio del microservicio de Estantes
@Service
@Transactional
public class EstanteService {

    private final EstanteRepository estanteRepository;

    public EstanteService(EstanteRepository estanteRepository) {
        this.estanteRepository = estanteRepository;
    }

    // Lista todos los estantes
    public List<EstanteResponse> obtenerTodos() {
        return estanteRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Obtiene un estante por su id
    public EstanteResponse obtenerPorId(Long id) {
        EstanteModel estante = buscarPorId(id);
        return mapToResponse(estante);
    }

    // Obtiene un estante por su número de estante
    public EstanteResponse obtenerPorNumEstante(int numEstante) {
        EstanteModel estante = estanteRepository.findByNumEstante(numEstante)
                .orElseThrow(() -> new NotFoundException("No existe el estante número: " + numEstante));
        return mapToResponse(estante);
    }

    // Obtiene un estante por su número de bodega
    public EstanteResponse obtenerPorNumBodega(int numBodega) {
        EstanteModel estante = estanteRepository.findByNumBodega(numBodega)
                .orElseThrow(() -> new NotFoundException("No existe la bodega número: " + numBodega));
        return mapToResponse(estante);
    }

    // Crea un nuevo estante validando que los números no estén en uso
    public EstanteResponse guardar(EstanteRequest request) {
        if (estanteRepository.existsByNumEstante(request.getNumEstante())) {
            throw new ConflictException("Ya existe un estante con el número: " + request.getNumEstante());
        }
        if (estanteRepository.existsByNumBodega(request.getNumBodega())) {
            throw new ConflictException("Ya existe una bodega con el número: " + request.getNumBodega());
        }

        EstanteModel estante = new EstanteModel();
        estante.setNumEstante(request.getNumEstante());
        estante.setNumBodega(request.getNumBodega());

        return mapToResponse(estanteRepository.save(estante));
    }

    // Actualiza los datos de un estante existente
    public EstanteResponse actualizar(Long id, EstanteRequest request) {
        EstanteModel estante = buscarPorId(id);

        // Solo validamos duplicado si el número cambió
        if (estante.getNumEstante() != request.getNumEstante()
                && estanteRepository.existsByNumEstante(request.getNumEstante())) {
            throw new ConflictException("Ya existe un estante con el número: " + request.getNumEstante());
        }
        if (estante.getNumBodega() != request.getNumBodega()
                && estanteRepository.existsByNumBodega(request.getNumBodega())) {
            throw new ConflictException("Ya existe una bodega con el número: " + request.getNumBodega());
        }

        estante.setNumEstante(request.getNumEstante());
        estante.setNumBodega(request.getNumBodega());

        return mapToResponse(estanteRepository.save(estante));
    }

    // Elimina un estante por id
    public void eliminar(Long id) {
        EstanteModel estante = buscarPorId(id);
        estanteRepository.delete(estante);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // HELPERS PRIVADOS
    // ─────────────────────────────────────────────────────────────────────────────

    private EstanteModel buscarPorId(Long id) {
        return estanteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el estante con id: " + id));
    }

    private EstanteResponse mapToResponse(EstanteModel estante) {
        return EstanteResponse.builder()
                .idEstante(estante.getIdEstante())
                .numEstante(estante.getNumEstante())
                .numBodega(estante.getNumBodega())
                .build();
    }
}
