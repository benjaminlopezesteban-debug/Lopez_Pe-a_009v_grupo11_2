package com.proyecto.gestionArchivo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.gestionArchivo.model.PacienteModel;
import com.proyecto.gestionArchivo.repository.PacienteRepository;

@Service
@Transactional
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    
    public List<PacienteModel> listAll(){
        return pacienteRepository.findAll();
    }

    public Optional<PacienteModel> findById(Long id){
        return pacienteRepository.findById(id);
    }

    public Optional<PacienteModel> findByRut(String rut){
        return pacienteRepository.findByRut(rut);
    }

    public PacienteModel save(PacienteModel paciente) {
        return pacienteRepository.save(paciente);
    }

    public void delet(Long id) {
        pacienteRepository.deleteById(id);
    }

}
