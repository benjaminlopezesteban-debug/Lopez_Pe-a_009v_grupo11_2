package com.proyecto.gestionArchivo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.gestionArchivo.dto.PacienteRequestDTO;
import com.proyecto.gestionArchivo.dto.PacienteResponseDTO;
import com.proyecto.gestionArchivo.exception.ResourceNotFoundException;
import com.proyecto.gestionArchivo.model.PacienteModel;
import com.proyecto.gestionArchivo.repository.PacienteRepository;

@Service
@Transactional
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional(readOnly = true)
    public List<PacienteResponseDTO> findAll() {
        return pacienteRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PacienteResponseDTO findById(Long id) {
        return toResponse(findEntity(id));
    }

    public PacienteResponseDTO create(PacienteRequestDTO request) {
        PacienteModel paciente = new PacienteModel();
        copy(request, paciente);
        return toResponse(pacienteRepository.save(paciente));
    }

    public PacienteResponseDTO update(Long id, PacienteRequestDTO request) {
        PacienteModel paciente = findEntity(id);
        copy(request, paciente);
        return toResponse(pacienteRepository.save(paciente));
    }

    public void delete(Long id) {
        PacienteModel paciente = findEntity(id);
        pacienteRepository.delete(paciente);
    }

    public PacienteModel findEntity(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + id));
    }

    private void copy(PacienteRequestDTO request, PacienteModel paciente) {
        paciente.setNumRut(request.numRut());
        paciente.setPnombre(request.pnombre());
        paciente.setSnombre(request.snombre());
        paciente.setPapellido(request.papellido());
        paciente.setSapellido(request.sapellido());
        paciente.setFechaNaci(request.fechaNaci());
        paciente.setDireccion(request.direccion());
        paciente.setNacionalidad(request.nacionalidad());
    }

    private PacienteResponseDTO toResponse(PacienteModel paciente) {
        return new PacienteResponseDTO(
                paciente.getIdPaciente(),
                paciente.getNumRut(),
                nombreCompleto(paciente.getPnombre(), paciente.getSnombre(), paciente.getPapellido(), paciente.getSapellido()),
                paciente.getFechaNaci(),
                paciente.getDireccion(),
                paciente.getNacionalidad()
        );
    }

    private String nombreCompleto(String pnombre, String snombre, String papellido, String sapellido) {
        return String.join(" ",
                pnombre == null ? "" : pnombre,
                snombre == null ? "" : snombre,
                papellido == null ? "" : papellido,
                sapellido == null ? "" : sapellido
        ).replaceAll("\\s+", " ").trim();
    }
}
