package com.proyecto.gestionArchivo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.gestionArchivo.dto.RegistroIngresoArchivoRequestDTO;
import com.proyecto.gestionArchivo.dto.RegistroIngresoArchivoResponseDTO;
import com.proyecto.gestionArchivo.exception.ResourceNotFoundException;
import com.proyecto.gestionArchivo.model.RegistroIngresoArchivoModel;
import com.proyecto.gestionArchivo.repository.RegistroIngresoArchivoRepository;

@Service
@Transactional
public class RegistroIngresoArchivoService {

    private final RegistroIngresoArchivoRepository registroIngresoArchivoRepository;
    private final ExpedienteHospitalizacionService expedienteService;
    private final AdministrativoService administrativoService;

    public RegistroIngresoArchivoService(
            RegistroIngresoArchivoRepository registroIngresoArchivoRepository,
            ExpedienteHospitalizacionService expedienteService,
            AdministrativoService administrativoService
    ) {
        this.registroIngresoArchivoRepository = registroIngresoArchivoRepository;
        this.expedienteService = expedienteService;
        this.administrativoService = administrativoService;
    }

    @Transactional(readOnly = true)
    public List<RegistroIngresoArchivoResponseDTO> findAll() {
        return registroIngresoArchivoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public RegistroIngresoArchivoResponseDTO findById(Long id) {
        return toResponse(findEntity(id));
    }

    public RegistroIngresoArchivoResponseDTO create(RegistroIngresoArchivoRequestDTO request) {
        RegistroIngresoArchivoModel registro = new RegistroIngresoArchivoModel();
        copy(request, registro);
        return toResponse(registroIngresoArchivoRepository.save(registro));
    }

    public RegistroIngresoArchivoResponseDTO update(Long id, RegistroIngresoArchivoRequestDTO request) {
        RegistroIngresoArchivoModel registro = findEntity(id);
        copy(request, registro);
        return toResponse(registroIngresoArchivoRepository.save(registro));
    }

    public void delete(Long id) {
        registroIngresoArchivoRepository.delete(findEntity(id));
    }

    private RegistroIngresoArchivoModel findEntity(Long id) {
        return registroIngresoArchivoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RegistroIngresoArchivo no encontrado con id: " + id));
    }

    private void copy(RegistroIngresoArchivoRequestDTO request, RegistroIngresoArchivoModel registro) {
        registro.setExpedienteHospitalizacion(expedienteService.findEntity(request.idExpediente()));
        registro.setAdministrativo(administrativoService.findEntity(request.idAdministrativo()));
        registro.setFechaIngreso(request.fechaIngreso());
        registro.setObservacion(request.observacion());
    }

    private RegistroIngresoArchivoResponseDTO toResponse(RegistroIngresoArchivoModel registro) {
        return new RegistroIngresoArchivoResponseDTO(
                registro.getIdRegistroIngresoArchivo(),
                registro.getExpedienteHospitalizacion().getIdExpediente(),
                registro.getAdministrativo().getIdAdministrativo(),
                registro.getFechaIngreso(),
                registro.getObservacion()
        );
    }
}
