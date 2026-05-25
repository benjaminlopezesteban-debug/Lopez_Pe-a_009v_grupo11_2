package com.proyecto.registroArchivado.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.registroArchivado.client.AdministrativoClient;
import com.proyecto.registroArchivado.client.ExpedienteHospitalizacionClient;
import com.proyecto.registroArchivado.client.FichaClinicaClient;
import com.proyecto.registroArchivado.dto.request.RegistroArchivadoRequestDTO;
import com.proyecto.registroArchivado.dto.response.RegistroArchivadoResponseDTO;
import com.proyecto.registroArchivado.exceptions.NotFoundException;
import com.proyecto.registroArchivado.exceptions.RemoteServiceException;
import com.proyecto.registroArchivado.model.RegistroArchivadoModel;
import com.proyecto.registroArchivado.repository.RegistroArchivadoRepository;

import feign.FeignException;

@Service
@Transactional
public class RegistroArchivadoService {

    private final RegistroArchivadoRepository registroArchivadoRepository;
    private final ExpedienteHospitalizacionClient expedienteClient;
    private final FichaClinicaClient fichaClinicaClient;
    private final AdministrativoClient administrativoClient;

    public RegistroArchivadoService(RegistroArchivadoRepository registroArchivadoRepository,
                                    ExpedienteHospitalizacionClient expedienteClient,
                                    FichaClinicaClient fichaClinicaClient,
                                    AdministrativoClient administrativoClient) {
        this.registroArchivadoRepository = registroArchivadoRepository;
        this.expedienteClient = expedienteClient;
        this.fichaClinicaClient = fichaClinicaClient;
        this.administrativoClient = administrativoClient;
    }

    public List<RegistroArchivadoResponseDTO> findAll() {
        return registroArchivadoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public RegistroArchivadoResponseDTO findById(Long id) {
        return toResponse(findEntity(id));
    }

    // FLUJO: validar expediente + administrativo + ficha → persistir → notificar expediente (PATCH)
    public RegistroArchivadoResponseDTO create(RegistroArchivadoRequestDTO request) {
        validarExpediente(request.idExpediente());
        validarAdministrativo(request.idAdministrativo());
        validarFichaClinica(request.folioFicha());

        RegistroArchivadoModel registro = new RegistroArchivadoModel();
        copy(request, registro);
        RegistroArchivadoModel guardado = registroArchivadoRepository.save(registro);

        // Notificar al MS Expediente para cerrar la relación bidireccional
        try {
            expedienteClient.asignarRegistroArchivado(request.idExpediente(), guardado.getIdRegistroArchivado());
        } catch (FeignException e) {
            System.err.println("[WARN] No se pudo notificar el expediente " + request.idExpediente()
                    + " con el registro archivado creado. Causa: " + e.getMessage());
        }

        return toResponse(guardado);
    }

    public RegistroArchivadoResponseDTO update(Long id, RegistroArchivadoRequestDTO request) {
        RegistroArchivadoModel registro = findEntity(id);
        validarExpediente(request.idExpediente());
        validarAdministrativo(request.idAdministrativo());
        validarFichaClinica(request.folioFicha());
        copy(request, registro);
        return toResponse(registroArchivadoRepository.save(registro));
    }

    public void delete(Long id) {
        registroArchivadoRepository.delete(findEntity(id));
    }

     //PRIVADOS — Feign calls 

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

    private void validarFichaClinica(String folioFichaStr) {
        try {
            Long folioFicha = Long.parseLong(folioFichaStr);
            fichaClinicaClient.obtenerPorFolio(folioFicha);
        } catch (NumberFormatException e) {
            throw new NotFoundException("Folio de ficha clínica inválido: " + folioFichaStr);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("No existe ficha clínica con folio: " + folioFichaStr);
        } catch (FeignException e) {
            throw new RemoteServiceException("Error al comunicarse con el microservicio de ficha clínica");
        }
    }

    private RegistroArchivadoModel findEntity(Long id) {
        return registroArchivadoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("RegistroArchivado no encontrado con id: " + id));
    }

    
    private void copy(RegistroArchivadoRequestDTO request, RegistroArchivadoModel registro) {
        registro.setFolioFicha(request.folioFicha());
        registro.setIdAdministrativo(request.idAdministrativo());
        registro.setIdExpediente(request.idExpediente());
        registro.setFechaArchivado(request.fechaArchivado());
        registro.setObservacion(request.observacion());
    }

    private RegistroArchivadoResponseDTO toResponse(RegistroArchivadoModel registro) {
        return new RegistroArchivadoResponseDTO(
                registro.getIdRegistroArchivado(),
                registro.getFolioFicha(),
                registro.getIdAdministrativo(),
                registro.getIdExpediente(),
                registro.getFechaArchivado(),
                registro.getObservacion()
        );
    }
}
