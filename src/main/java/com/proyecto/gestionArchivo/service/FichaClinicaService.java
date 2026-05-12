package com.proyecto.gestionArchivo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.gestionArchivo.dto.FichaClinicaRequestDTO;
import com.proyecto.gestionArchivo.dto.FichaClinicaResponseDTO;
import com.proyecto.gestionArchivo.exception.ResourceNotFoundException;
import com.proyecto.gestionArchivo.model.FichaClinicaModel;
import com.proyecto.gestionArchivo.model.PacienteModel;
import com.proyecto.gestionArchivo.repository.FichaClinicaRepository;

@Service
@Transactional
public class FichaClinicaService {

    private final FichaClinicaRepository fichaClinicaRepository;
    private final PacienteService pacienteService;
    private final EstanteService estanteService;

    public FichaClinicaService(
            FichaClinicaRepository fichaClinicaRepository,
            PacienteService pacienteService,
            EstanteService estanteService
    ) {
        this.fichaClinicaRepository = fichaClinicaRepository;
        this.pacienteService = pacienteService;
        this.estanteService = estanteService;
    }

    @Transactional(readOnly = true)
    public List<FichaClinicaResponseDTO> findAll() {
        return fichaClinicaRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public FichaClinicaResponseDTO findByFolio(String folio) {
        return toResponse(findEntity(folio));
    }

    public FichaClinicaResponseDTO create(FichaClinicaRequestDTO request) {
        FichaClinicaModel ficha = new FichaClinicaModel();
        copy(request, ficha);
        return toResponse(fichaClinicaRepository.save(ficha));
    }

    public FichaClinicaResponseDTO update(String folio, FichaClinicaRequestDTO request) {
        FichaClinicaModel ficha = findEntity(folio);
        copy(request, ficha);
        ficha.setFolioFicha(folio);
        return toResponse(fichaClinicaRepository.save(ficha));
    }

    public void delete(String folio) {
        fichaClinicaRepository.delete(findEntity(folio));
    }

    public FichaClinicaModel findEntity(String folio) {
        return fichaClinicaRepository.findById(folio)
                .orElseThrow(() -> new ResourceNotFoundException("FichaClinica no encontrada con folio: " + folio));
    }

    private void copy(FichaClinicaRequestDTO request, FichaClinicaModel ficha) {
        ficha.setFolioFicha(request.folioFicha());
        ficha.setFechaCreacion(request.fechaCreacion());
        ficha.setPaciente(pacienteService.findEntity(request.idPaciente()));
        ficha.setEstante(estanteService.findEntity(request.idEstante()));
    }

    private FichaClinicaResponseDTO toResponse(FichaClinicaModel ficha) {
        PacienteModel paciente = ficha.getPaciente();
        String pacienteNombre = String.join(" ",
                paciente.getPnombre(),
                paciente.getPapellido(),
                paciente.getSapellido()).replaceAll("\\s+", " ").trim();
        return new FichaClinicaResponseDTO(
                ficha.getFolioFicha(),
                ficha.getFechaCreacion(),
                paciente.getIdPaciente(),
                pacienteNombre,
                ficha.getEstante().getIdEstante(),
                ficha.getEstante().getNumEstante()
        );
    }
}
