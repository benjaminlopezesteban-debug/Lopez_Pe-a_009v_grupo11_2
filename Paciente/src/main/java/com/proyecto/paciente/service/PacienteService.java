package com.proyecto.paciente.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.paciente.dto.request.PacienteRequestDTO;
import com.proyecto.paciente.dto.response.PacienteFeignResponse;
import com.proyecto.paciente.dto.response.PacienteResponseDTO;
import com.proyecto.paciente.exceptions.BadRequestException;
import com.proyecto.paciente.exceptions.NotFoundException;
import com.proyecto.paciente.model.PacienteModel;
import com.proyecto.paciente.repository.PacienteRepository;

@Service
@Transactional
public class PacienteService {

    private static final Logger log = LoggerFactory.getLogger(PacienteService.class);

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional(readOnly = true)
    public List<PacienteResponseDTO> findAll() {
        log.info("event=find_all_pacientes");
        return pacienteRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PacienteResponseDTO findById(Long id) {
        log.info("event=find_paciente id={}", id);
        return toResponse(findEntity(id));
    }

    @Transactional(readOnly = true)
    public PacienteFeignResponse findByRut(String rut) {
        log.info("event=find_paciente_by_rut rut={}", rut);
        PacienteModel p = pacienteRepository.findByNumRut(rut);
        if (p == null) throw new NotFoundException("Paciente no encontrado con rut: " + rut);
        return toFeign(p);
    }

    @Transactional(readOnly = true)
    public List<PacienteFeignResponse> searchByNombre(String pnombre, String snombre, String appaterno) {
        log.info("event=search_paciente pnombre={} appaterno={}", pnombre, appaterno);
        return pacienteRepository
                .findByPnombreIgnoreCaseContainingAndPapellidoIgnoreCaseContaining(
                        pnombre == null ? "" : pnombre,
                        appaterno == null ? "" : appaterno)
                .stream().map(this::toFeign).toList();
    }

    public PacienteResponseDTO create(PacienteRequestDTO request) {
        log.info("event=create_paciente rut={}", request.numRut());
        if (pacienteRepository.existsByNumRut(request.numRut())) {
            throw new BadRequestException("Ya existe un paciente con rut: " + request.numRut());
        }
        PacienteModel paciente = new PacienteModel();
        copy(request, paciente);
        return toResponse(pacienteRepository.save(paciente));
    }

    public PacienteResponseDTO update(Long id, PacienteRequestDTO request) {
        log.info("event=update_paciente id={}", id);
        PacienteModel paciente = findEntity(id);
        copy(request, paciente);
        return toResponse(pacienteRepository.save(paciente));
    }

    public void delete(Long id) {
        log.info("event=delete_paciente id={}", id);
        pacienteRepository.delete(findEntity(id));
    }

    private PacienteModel findEntity(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Paciente no encontrado con id: " + id));
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
                paciente.getNacionalidad());
    }

    private PacienteFeignResponse toFeign(PacienteModel p) {
        return new PacienteFeignResponse(
                p.getIdPaciente(), p.getNumRut(), p.getPnombre(), p.getSnombre(),
                p.getPapellido(), p.getSapellido(), p.getFechaNaci(), p.getDireccion(), p.getNacionalidad());
    }

    private String nombreCompleto(String pnombre, String snombre, String papellido, String sapellido) {
        return String.join(" ",
                pnombre == null ? "" : pnombre,
                snombre == null ? "" : snombre,
                papellido == null ? "" : papellido,
                sapellido == null ? "" : sapellido).replaceAll("\\s+", " ").trim();
    }
}
