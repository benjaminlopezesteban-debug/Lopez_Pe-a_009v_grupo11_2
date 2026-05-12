package com.proyecto.gestionArchivo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.gestionArchivo.dto.EstanteRequestDTO;
import com.proyecto.gestionArchivo.dto.EstanteResponseDTO;
import com.proyecto.gestionArchivo.exception.ResourceNotFoundException;
import com.proyecto.gestionArchivo.model.EstanteModel;
import com.proyecto.gestionArchivo.repository.EstanteRepository;

@Service
@Transactional
public class EstanteService {

    private final EstanteRepository estanteRepository;

    public EstanteService(EstanteRepository estanteRepository) {
        this.estanteRepository = estanteRepository;
    }

    @Transactional(readOnly = true)
    public List<EstanteResponseDTO> findAll() {
        return estanteRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EstanteResponseDTO findById(Long id) {
        return toResponse(findEntity(id));
    }

    public EstanteResponseDTO create(EstanteRequestDTO request) {
        EstanteModel estante = new EstanteModel();
        copy(request, estante);
        return toResponse(estanteRepository.save(estante));
    }

    public EstanteResponseDTO update(Long id, EstanteRequestDTO request) {
        EstanteModel estante = findEntity(id);
        copy(request, estante);
        return toResponse(estanteRepository.save(estante));
    }

    public void delete(Long id) {
        estanteRepository.delete(findEntity(id));
    }

    public EstanteModel findEntity(Long id) {
        return estanteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estante no encontrado con id: " + id));
    }

    private void copy(EstanteRequestDTO request, EstanteModel estante) {
        estante.setNumEstante(request.numEstante());
        estante.setNumBodega(request.numBodega());
        estante.setUbicacion(request.ubicacion());
    }

    private EstanteResponseDTO toResponse(EstanteModel estante) {
        return new EstanteResponseDTO(
                estante.getIdEstante(),
                estante.getNumEstante(),
                estante.getNumBodega(),
                estante.getUbicacion()
        );
    }
}
