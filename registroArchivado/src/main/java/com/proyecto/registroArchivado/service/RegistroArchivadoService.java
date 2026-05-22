package com.proyecto.registroArchivado.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.registroArchivado.dto.RegistroArchivadoRequestDTO;
import com.proyecto.registroArchivado.dto.RegistroArchivadoResponseDTO;
import com.proyecto.registroArchivado.exception.ResourceNotFoundException;
import com.proyecto.registroArchivado.model.RegistroArchivadoModel;
import com.proyecto.registroArchivado.repository.RegistroArchivadoRepository;

@Service
@Transactional
public class RegistroArchivadoService {

    private final RegistroArchivadoRepository registroArchivadoRepository;
    public RegistroArchivadoService(RegistroArchivadoRepository registroArchivadoRepository) {
        this.registroArchivadoRepository = registroArchivadoRepository;
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
