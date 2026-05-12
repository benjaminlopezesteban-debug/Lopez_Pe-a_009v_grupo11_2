package com.proyecto.gestionArchivo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.gestionArchivo.dto.PrestamoRequestDTO;
import com.proyecto.gestionArchivo.dto.PrestamoResponseDTO;
import com.proyecto.gestionArchivo.exception.ResourceNotFoundException;
import com.proyecto.gestionArchivo.model.PrestamoModel;
import com.proyecto.gestionArchivo.repository.PrestamoRepository;

@Service
@Transactional
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final AdministrativoService administrativoService;
    private final FichaClinicaService fichaClinicaService;

    public PrestamoService(
            PrestamoRepository prestamoRepository,
            AdministrativoService administrativoService,
            FichaClinicaService fichaClinicaService
    ) {
        this.prestamoRepository = prestamoRepository;
        this.administrativoService = administrativoService;
        this.fichaClinicaService = fichaClinicaService;
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
        prestamo.setAdministrativo(administrativoService.findEntity(request.idAdministrativo()));
        prestamo.setFichaClinica(fichaClinicaService.findEntity(request.folioFicha()));
        prestamo.setFechaPrestamo(request.fechaPrestamo());
        prestamo.setFechaDevolucion(request.fechaDevolucion());
        prestamo.setEstado(request.estado());
    }

    private PrestamoResponseDTO toResponse(PrestamoModel prestamo) {
        return new PrestamoResponseDTO(
                prestamo.getIdPrestamo(),
                prestamo.getAdministrativo().getIdAdministrativo(),
                prestamo.getFichaClinica().getFolioFicha(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucion(),
                prestamo.getEstado()
        );
    }
}
