package com.proyecto.gestionArchivo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.gestionArchivo.dto.ExpedienteHospitalizacionRequestDTO;
import com.proyecto.gestionArchivo.dto.ExpedienteHospitalizacionResponseDTO;
import com.proyecto.gestionArchivo.exception.ResourceNotFoundException;
import com.proyecto.gestionArchivo.model.ExpedienteHospitalizacionModel;
import com.proyecto.gestionArchivo.repository.ExpedienteHospitalizacionRepository;

@Service
@Transactional
public class ExpedienteHospitalizacionService {

    private final ExpedienteHospitalizacionRepository expedienteRepository;
    private final ReservaAtencionService reservaAtencionService;

    public ExpedienteHospitalizacionService(
            ExpedienteHospitalizacionRepository expedienteRepository,
            ReservaAtencionService reservaAtencionService
    ) {
        this.expedienteRepository = expedienteRepository;
        this.reservaAtencionService = reservaAtencionService;
    }

    @Transactional(readOnly = true)
    public List<ExpedienteHospitalizacionResponseDTO> findAll() {
        return expedienteRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ExpedienteHospitalizacionResponseDTO findById(Long id) {
        return toResponse(findEntity(id));
    }

    public ExpedienteHospitalizacionResponseDTO create(ExpedienteHospitalizacionRequestDTO request) {
        ExpedienteHospitalizacionModel expediente = new ExpedienteHospitalizacionModel();
        copy(request, expediente);
        return toResponse(expedienteRepository.save(expediente));
    }

    public ExpedienteHospitalizacionResponseDTO update(Long id, ExpedienteHospitalizacionRequestDTO request) {
        ExpedienteHospitalizacionModel expediente = findEntity(id);
        copy(request, expediente);
        return toResponse(expedienteRepository.save(expediente));
    }

    public void delete(Long id) {
        expedienteRepository.delete(findEntity(id));
    }

    public ExpedienteHospitalizacionModel findEntity(Long id) {
        return expedienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExpedienteHospitalizacion no encontrado con id: " + id));
    }

    private void copy(ExpedienteHospitalizacionRequestDTO request, ExpedienteHospitalizacionModel expediente) {
        expediente.setCodExpediente(request.codExpediente());
        expediente.setRutPaciente(request.rutPaciente());
        expediente.setDigitalizacion(request.digitalizacion());
        expediente.setReservaAtencion(reservaAtencionService.findEntity(request.idReservaAtencion()));
    }

    private ExpedienteHospitalizacionResponseDTO toResponse(ExpedienteHospitalizacionModel expediente) {
        return new ExpedienteHospitalizacionResponseDTO(
                expediente.getIdExpediente(),
                expediente.getCodExpediente(),
                expediente.getRutPaciente(),
                expediente.getDigitalizacion(),
                expediente.getReservaAtencion().getIdReservaAtencion()
        );
    }
}
