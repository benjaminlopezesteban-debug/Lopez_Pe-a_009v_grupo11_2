package com.proyecto.prestamo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.prestamo.dto.PrestamoRequestDTO;
import com.proyecto.prestamo.dto.PrestamoResponseDTO;
import com.proyecto.prestamo.exception.ResourceNotFoundException;
import com.proyecto.prestamo.model.PrestamoModel;
import com.proyecto.prestamo.repository.PrestamoRepository;

@Service
@Transactional
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    public PrestamoService(PrestamoRepository prestamoRepository) {
        this.prestamoRepository = prestamoRepository;
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponseDTO> findAll() {
        return prestamoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PrestamoResponseDTO findById(Long id) {
        return toResponse(findEntity(id));
    }

    public PrestamoResponseDTO create(PrestamoRequestDTO request) {
        PrestamoModel prestamo = new PrestamoModel();
        copy(request, prestamo);
        return toResponse(prestamoRepository.save(prestamo));
    }

    public PrestamoResponseDTO update(Long id, PrestamoRequestDTO request) {
        PrestamoModel prestamo = findEntity(id);
        copy(request, prestamo);
        return toResponse(prestamoRepository.save(prestamo));
    }

    public void delete(Long id) {
        prestamoRepository.delete(findEntity(id));
    }

    private PrestamoModel findEntity(Long id) {
        return prestamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prestamo no encontrado con id: " + id));
    }

    private void copy(PrestamoRequestDTO request, PrestamoModel prestamo) {
        prestamo.setIdAdministrativo(request.idAdministrativo());
        prestamo.setFolioFicha(request.folioFicha());
        prestamo.setFechaPrestamo(request.fechaPrestamo());
        prestamo.setFechaDevolucion(request.fechaDevolucion());
        prestamo.setEstado(request.estado());
    }

    private PrestamoResponseDTO toResponse(PrestamoModel prestamo) {
        return new PrestamoResponseDTO(
                prestamo.getIdPrestamo(),
                prestamo.getIdAdministrativo(),
                prestamo.getFolioFicha(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucion(),
                prestamo.getEstado()
        );
    }
}
