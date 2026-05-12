package com.proyecto.gestionArchivo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.gestionArchivo.dto.RegistroArchivadoRequestDTO;
import com.proyecto.gestionArchivo.dto.RegistroArchivadoResponseDTO;
import com.proyecto.gestionArchivo.exception.ResourceNotFoundException;
import com.proyecto.gestionArchivo.model.RegistroArchivadoModel;
import com.proyecto.gestionArchivo.repository.RegistroArchivadoRepository;

@Service
@Transactional
public class RegistroArchivadoService {

    private final RegistroArchivadoRepository registroArchivadoRepository;
    private final FichaClinicaService fichaClinicaService;
    private final AdministrativoService administrativoService;
    private final ExpedienteHospitalizacionService expedienteService;

    public RegistroArchivadoService(
            RegistroArchivadoRepository registroArchivadoRepository,
            FichaClinicaService fichaClinicaService,
            AdministrativoService administrativoService,
            ExpedienteHospitalizacionService expedienteService
    ) {
        this.registroArchivadoRepository = registroArchivadoRepository;
        this.fichaClinicaService = fichaClinicaService;
        this.administrativoService = administrativoService;
        this.expedienteService = expedienteService;
    }

    @Transactional(readOnly = true)
    public List<RegistroArchivadoResponseDTO> findAll() {
        return registroArchivadoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public RegistroArchivadoResponseDTO findById(Long id) {
        return toResponse(findEntity(id));
    }

    public RegistroArchivadoResponseDTO create(RegistroArchivadoRequestDTO request) {
        RegistroArchivadoModel registro = new RegistroArchivadoModel();
        copy(request, registro);
        return toResponse(registroArchivadoRepository.save(registro));
    }

    public RegistroArchivadoResponseDTO update(Long id, RegistroArchivadoRequestDTO request) {
        RegistroArchivadoModel registro = findEntity(id);
        copy(request, registro);
        return toResponse(registroArchivadoRepository.save(registro));
    }

    public void delete(Long id) {
        registroArchivadoRepository.delete(findEntity(id));
    }

    private RegistroArchivadoModel findEntity(Long id) {
        return registroArchivadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RegistroArchivado no encontrado con id: " + id));
    }

    private void copy(RegistroArchivadoRequestDTO request, RegistroArchivadoModel registro) {
        registro.setFichaClinica(fichaClinicaService.findEntity(request.folioFicha()));
        registro.setAdministrativo(administrativoService.findEntity(request.idAdministrativo()));
        registro.setExpedienteHospitalizacion(expedienteService.findEntity(request.idExpediente()));
        registro.setFechaArchivado(request.fechaArchivado());
        registro.setObservacion(request.observacion());
    }

    private RegistroArchivadoResponseDTO toResponse(RegistroArchivadoModel registro) {
        return new RegistroArchivadoResponseDTO(
                registro.getIdRegistroArchivado(),
                registro.getFichaClinica().getFolioFicha(),
                registro.getAdministrativo().getIdAdministrativo(),
                registro.getExpedienteHospitalizacion().getIdExpediente(),
                registro.getFechaArchivado(),
                registro.getObservacion()
        );
    }
}
