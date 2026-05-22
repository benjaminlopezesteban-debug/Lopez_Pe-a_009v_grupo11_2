package com.proyecto.auditoria.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.auditoria.dto.AuditoriaRequestDTO;
import com.proyecto.auditoria.dto.AuditoriaResponseDTO;
import com.proyecto.auditoria.exception.ResourceNotFoundException;
import com.proyecto.auditoria.model.AuditoriaModel;
import com.proyecto.auditoria.repository.AuditoriaRepository;

@Service
@Transactional
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResponseDTO> findAll() {
        return auditoriaRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AuditoriaResponseDTO findById(Long id) {
        return toResponse(findEntity(id));
    }

    public AuditoriaResponseDTO create(AuditoriaRequestDTO request) {
        AuditoriaModel auditoria = new AuditoriaModel();
        copy(request, auditoria);
        return toResponse(auditoriaRepository.save(auditoria));
    }

    public AuditoriaResponseDTO update(Long id, AuditoriaRequestDTO request) {
        AuditoriaModel auditoria = findEntity(id);
        copy(request, auditoria);
        return toResponse(auditoriaRepository.save(auditoria));
    }

    public void delete(Long id) {
        auditoriaRepository.delete(findEntity(id));
    }

    private AuditoriaModel findEntity(Long id) {
        return auditoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auditoria no encontrada con id: " + id));
    }

    private void copy(AuditoriaRequestDTO request, AuditoriaModel auditoria) {
        auditoria.setIdAdministrativo(request.idAdministrativo());
        auditoria.setFolioFicha(request.folioFicha());
        auditoria.setFechaAuditoria(request.fechaAuditoria());
        auditoria.setAccion(request.accion());
        auditoria.setDetalle(request.detalle());
    }

    private AuditoriaResponseDTO toResponse(AuditoriaModel auditoria) {
        return new AuditoriaResponseDTO(
                auditoria.getIdAuditoria(),
                auditoria.getIdAdministrativo(),
                auditoria.getFolioFicha(),
                auditoria.getFechaAuditoria(),
                auditoria.getAccion(),
                auditoria.getDetalle()
        );
    }
}
