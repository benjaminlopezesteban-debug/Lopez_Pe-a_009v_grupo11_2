package com.proyecto.estante.service;

import com.proyecto.estante.dto.request.EstanteRequest;
import com.proyecto.estante.dto.response.EstanteResponse;
import com.proyecto.estante.exceptions.NotFoundException;
import com.proyecto.estante.model.EstanteModel;
import com.proyecto.estante.repository.EstanteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EstanteService {

    private final EstanteRepository estanteRepository;

    public EstanteService(EstanteRepository estanteRepository) {
        this.estanteRepository = estanteRepository;
    }

    public List<EstanteResponse> obtenerTodos() {
        return estanteRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public EstanteResponse obtenerPorId(Long id) {
        return mapToResponse(buscarPorId(id));
    }

    public EstanteResponse obtenerPorNumEstante(int numEstante) {
        return mapToResponse(estanteRepository.findByNumEstante(numEstante)
                .orElseThrow(() -> new NotFoundException("No existe el estante número: " + numEstante)));
    }

    public EstanteResponse obtenerPorNumBodega(int numBodega) {
        return mapToResponse(estanteRepository.findByNumBodega(numBodega)
                .orElseThrow(() -> new NotFoundException("No existe la bodega número: " + numBodega)));
    }

    public EstanteResponse guardar(EstanteRequest request) {
        EstanteModel estante = new EstanteModel();
        estante.setNumEstante(request.getNumEstante());
        estante.setNumBodega(request.getNumBodega());
        return mapToResponse(estanteRepository.save(estante));
    }

    public EstanteResponse actualizar(Long id, EstanteRequest request) {
        EstanteModel estante = buscarPorId(id);
        estante.setNumEstante(request.getNumEstante());
        estante.setNumBodega(request.getNumBodega());
        return mapToResponse(estanteRepository.save(estante));
    }

    public void eliminar(Long id) {
        estanteRepository.delete(buscarPorId(id));
    }

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
