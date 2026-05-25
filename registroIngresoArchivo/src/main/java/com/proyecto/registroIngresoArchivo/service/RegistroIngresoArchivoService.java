package com.proyecto.registroIngresoArchivo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.registroIngresoArchivo.client.AdministrativoClient;
import com.proyecto.registroIngresoArchivo.client.ExpedienteHospitalizacionClient;
import com.proyecto.registroIngresoArchivo.dto.request.RegistroIngresoArchivoRequestDTO;
import com.proyecto.registroIngresoArchivo.dto.response.RegistroIngresoArchivoResponseDTO;
import com.proyecto.registroIngresoArchivo.exceptions.NotFoundException;
import com.proyecto.registroIngresoArchivo.exceptions.RemoteServiceException;
import com.proyecto.registroIngresoArchivo.model.RegistroIngresoArchivoModel;
import com.proyecto.registroIngresoArchivo.repository.RegistroIngresoArchivoRepository;

import feign.FeignException;

@Service
@Transactional
public class RegistroIngresoArchivoService {

    private final RegistroIngresoArchivoRepository registroIngresoArchivoRepository;
    private final ExpedienteHospitalizacionClient expedienteClient;
    private final AdministrativoClient administrativoClient;

    public RegistroIngresoArchivoService(RegistroIngresoArchivoRepository registroIngresoArchivoRepository,
                                          ExpedienteHospitalizacionClient expedienteClient,
                                          AdministrativoClient administrativoClient) {
        this.registroIngresoArchivoRepository = registroIngresoArchivoRepository;
        this.expedienteClient = expedienteClient;
        this.administrativoClient = administrativoClient;
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
        validarExpediente(request.idExpediente());
        validarAdministrativo(request.idAdministrativo());
        RegistroIngresoArchivoModel registro = new RegistroIngresoArchivoModel();
        copy(request, registro);
        return toResponse(registroIngresoArchivoRepository.save(registro));
    }

    public RegistroIngresoArchivoResponseDTO update(Long id, RegistroIngresoArchivoRequestDTO request) {
        RegistroIngresoArchivoModel registro = findEntity(id);
        validarExpediente(request.idExpediente());
        validarAdministrativo(request.idAdministrativo());
        copy(request, registro);
        return toResponse(registroIngresoArchivoRepository.save(registro));
    }

    public void delete(Long id) {
        registroIngresoArchivoRepository.delete(findEntity(id));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // HELPERS PRIVADOS — Feign calls con manejo de excepciones uniforme
    // ─────────────────────────────────────────────────────────────────────────────

    private void validarExpediente(Long idExpediente) {
        try {
            expedienteClient.obtenerPorId(idExpediente);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("No existe el expediente con id: " + idExpediente);
        } catch (FeignException e) {
            throw new RemoteServiceException("Error al comunicarse con el microservicio de expedientes");
        }
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

    private RegistroIngresoArchivoModel findEntity(Long id) {
        return registroIngresoArchivoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("RegistroIngresoArchivo no encontrado con id: " + id));
    }

    private void copy(RegistroIngresoArchivoRequestDTO request, RegistroIngresoArchivoModel registro) {
        registro.setIdExpediente(request.idExpediente());
        registro.setIdAdministrativo(request.idAdministrativo());
        registro.setFechaIngreso(request.fechaIngreso());
        registro.setObservacion(request.observacion());
    }

    private RegistroIngresoArchivoResponseDTO toResponse(RegistroIngresoArchivoModel registro) {
        return new RegistroIngresoArchivoResponseDTO(
                registro.getIdRegistroIngresoArchivo(),
                registro.getIdExpediente(),
                registro.getIdAdministrativo(),
                registro.getFechaIngreso(),
                registro.getObservacion()
        );
    }
}
