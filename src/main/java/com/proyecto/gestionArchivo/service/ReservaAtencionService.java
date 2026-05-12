package com.proyecto.gestionArchivo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.gestionArchivo.dto.ReservaAtencionRequestDTO;
import com.proyecto.gestionArchivo.dto.ReservaAtencionResponseDTO;
import com.proyecto.gestionArchivo.exception.ResourceNotFoundException;
import com.proyecto.gestionArchivo.model.ReservaAtencionModel;
import com.proyecto.gestionArchivo.repository.ReservaAtencionRepository;

@Service
@Transactional
public class ReservaAtencionService {

    private final ReservaAtencionRepository reservaAtencionRepository;
    private final PacienteService pacienteService;

    public ReservaAtencionService(ReservaAtencionRepository reservaAtencionRepository, PacienteService pacienteService) {
        this.reservaAtencionRepository = reservaAtencionRepository;
        this.pacienteService = pacienteService;
    }

    @Transactional(readOnly = true)
    public List<ReservaAtencionResponseDTO> findAll() {
        return reservaAtencionRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ReservaAtencionResponseDTO findById(Long id) {
        return toResponse(findEntity(id));
    }

    public ReservaAtencionResponseDTO create(ReservaAtencionRequestDTO request) {
        ReservaAtencionModel reserva = new ReservaAtencionModel();
        copy(request, reserva);
        return toResponse(reservaAtencionRepository.save(reserva));
    }

    public ReservaAtencionResponseDTO update(Long id, ReservaAtencionRequestDTO request) {
        ReservaAtencionModel reserva = findEntity(id);
        copy(request, reserva);
        return toResponse(reservaAtencionRepository.save(reserva));
    }

    public void delete(Long id) {
        reservaAtencionRepository.delete(findEntity(id));
    }

    public ReservaAtencionModel findEntity(Long id) {
        return reservaAtencionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReservaAtencion no encontrada con id: " + id));
    }

    private void copy(ReservaAtencionRequestDTO request, ReservaAtencionModel reserva) {
        reserva.setFechaReservada(request.fechaReservada());
        reserva.setHoraReservada(request.horaReservada());
        reserva.setEspecialidad(request.especialidad());
        reserva.setProfesional(request.profesional());
        reserva.setPaciente(pacienteService.findEntity(request.idPaciente()));
    }

    private ReservaAtencionResponseDTO toResponse(ReservaAtencionModel reserva) {
        return new ReservaAtencionResponseDTO(
                reserva.getIdReservaAtencion(),
                reserva.getFechaReservada(),
                reserva.getHoraReservada(),
                reserva.getEspecialidad(),
                reserva.getProfesional(),
                reserva.getPaciente().getIdPaciente()
        );
    }
}
