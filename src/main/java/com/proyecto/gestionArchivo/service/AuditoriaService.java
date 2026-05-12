package com.proyecto.gestionArchivo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.gestionArchivo.dto.AuditoriaRequestDTO;
import com.proyecto.gestionArchivo.dto.AuditoriaResponseDTO;
import com.proyecto.gestionArchivo.exception.ResourceNotFoundException;
import com.proyecto.gestionArchivo.model.AuditoriaModel;
import com.proyecto.gestionArchivo.repository.AuditoriaRepository;

@Service
@Transactional
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final AdministrativoService administrativoService;
    private final FichaClinicaService fichaClinicaService;

    public AuditoriaService(
            AuditoriaRepository auditoriaRepository,
            AdministrativoService administrativoService,
            FichaClinicaService fichaClinicaService
    ) {
        this.auditoriaRepository = auditoriaRepository;
        this.administrativoService = administrativoService;
        this.fichaClinicaService = fichaClinicaService;
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
        auditoria.setAdministrativo(administrativoService.findEntity(request.idAdministrativo()));
        auditoria.setFichaClinica(fichaClinicaService.findEntity(request.folioFicha()));
        auditoria.setFechaAuditoria(request.fechaAuditoria());
        auditoria.setAccion(request.accion());
        auditoria.setDetalle(request.detalle());
    }

    private AuditoriaResponseDTO toResponse(AuditoriaModel auditoria) {
        return new AuditoriaResponseDTO(
                auditoria.getIdAuditoria(),
                auditoria.getAdministrativo().getIdAdministrativo(),
                auditoria.getFichaClinica().getFolioFicha(),
                auditoria.getFechaAuditoria(),
                auditoria.getAccion(),
                auditoria.getDetalle()
        );
    }
}
