package com.proyecto.gestionArchivo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.gestionArchivo.dto.AdministrativoRequestDTO;
import com.proyecto.gestionArchivo.dto.AdministrativoResponseDTO;
import com.proyecto.gestionArchivo.exception.ResourceNotFoundException;
import com.proyecto.gestionArchivo.model.AdministrativoModel;
import com.proyecto.gestionArchivo.repository.AdministrativoRepository;

@Service
@Transactional
public class AdministrativoService {

    private final AdministrativoRepository administrativoRepository;

    public AdministrativoService(AdministrativoRepository administrativoRepository) {
        this.administrativoRepository = administrativoRepository;
    }

    @Transactional(readOnly = true)
    public List<AdministrativoResponseDTO> findAll() {
        return administrativoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AdministrativoResponseDTO findById(Long id) {
        return toResponse(findEntity(id));
    }

    public AdministrativoResponseDTO create(AdministrativoRequestDTO request) {
        AdministrativoModel administrativo = new AdministrativoModel();
        copy(request, administrativo);
        return toResponse(administrativoRepository.save(administrativo));
    }

    public AdministrativoResponseDTO update(Long id, AdministrativoRequestDTO request) {
        AdministrativoModel administrativo = findEntity(id);
        copy(request, administrativo);
        return toResponse(administrativoRepository.save(administrativo));
    }

    public void delete(Long id) {
        administrativoRepository.delete(findEntity(id));
    }

    public AdministrativoModel findEntity(Long id) {
        return administrativoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrativo no encontrado con id: " + id));
    }

    private void copy(AdministrativoRequestDTO request, AdministrativoModel administrativo) {
        administrativo.setRut(request.rut());
        administrativo.setPnombre(request.pnombre());
        administrativo.setSnombre(request.snombre());
        administrativo.setPapellido(request.papellido());
        administrativo.setSapellido(request.sapellido());
        administrativo.setFechaNaci(request.fechaNaci());
        administrativo.setFechaContrato(request.fechaContrato());
        administrativo.setEmail(request.email());
        administrativo.setCargo(request.cargo());
    }

    private AdministrativoResponseDTO toResponse(AdministrativoModel administrativo) {
        String nombreCompleto = String.join(" ",
                administrativo.getPnombre(),
                administrativo.getSnombre() == null ? "" : administrativo.getSnombre(),
                administrativo.getPapellido(),
                administrativo.getSapellido()).replaceAll("\\s+", " ").trim();
        return new AdministrativoResponseDTO(
                administrativo.getIdAdministrativo(),
                administrativo.getRut(),
                nombreCompleto,
                administrativo.getEmail(),
                administrativo.getCargo()
        );
    }
}
