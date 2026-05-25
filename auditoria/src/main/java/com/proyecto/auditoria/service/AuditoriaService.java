package com.proyecto.auditoria.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.auditoria.client.AdministrativoClient;
import com.proyecto.auditoria.client.FichaClinicaClient;
import com.proyecto.auditoria.dto.request.AuditoriaRequestDTO;
import com.proyecto.auditoria.dto.response.AuditoriaResponseDTO;
import com.proyecto.auditoria.exceptions.NotFoundException;
import com.proyecto.auditoria.exceptions.RemoteServiceException;
import com.proyecto.auditoria.model.AuditoriaModel;
import com.proyecto.auditoria.repository.AuditoriaRepository;

import feign.FeignException;

@Service
@Transactional
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final AdministrativoClient administrativoClient;
    private final FichaClinicaClient fichaClinicaClient;

    public AuditoriaService(AuditoriaRepository auditoriaRepository,
                            AdministrativoClient administrativoClient,
                            FichaClinicaClient fichaClinicaClient) {
        this.auditoriaRepository = auditoriaRepository;
        this.administrativoClient = administrativoClient;
        this.fichaClinicaClient = fichaClinicaClient;
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
        validarAdministrativo(request.idAdministrativo());
        validarFichaClinica(request.folioFicha());
        AuditoriaModel auditoria = new AuditoriaModel();
        copy(request, auditoria);
        return toResponse(auditoriaRepository.save(auditoria));
    }

    public AuditoriaResponseDTO update(Long id, AuditoriaRequestDTO request) {
        AuditoriaModel auditoria = findEntity(id);
        validarAdministrativo(request.idAdministrativo());
        validarFichaClinica(request.folioFicha());
        copy(request, auditoria);
        return toResponse(auditoriaRepository.save(auditoria));
    }

    public void delete(Long id) {
        auditoriaRepository.delete(findEntity(id));
    }

    private void validarAdministrativo(Long idAdministrativo) {
        try {
            administrativoClient.obtenerPorId(idAdministrativo);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("No existe el administrativo con id: " + idAdministrativo);
        } catch (FeignException e) {
            throw new RemoteServiceException("Error al comunicarse con el microservicio de administrativos");
        }
    }

    private void validarFichaClinica(String folioFichaStr) {
        try {
            fichaClinicaClient.obtenerPorFolio(Long.parseLong(folioFichaStr));
        } catch (NumberFormatException e) {
            throw new NotFoundException("Folio de ficha clínica inválido: " + folioFichaStr);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("No existe ficha clínica con folio: " + folioFichaStr);
        } catch (FeignException e) {
            throw new RemoteServiceException("Error al comunicarse con el microservicio de ficha clínica");
        }
    }

    private AuditoriaModel findEntity(Long id) {
        return auditoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Auditoría no encontrada con id: " + id));
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
                auditoria.getDetalle());
    }
}
