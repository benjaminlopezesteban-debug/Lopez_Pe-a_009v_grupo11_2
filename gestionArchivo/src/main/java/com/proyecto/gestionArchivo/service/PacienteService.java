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

    private final PacienteRepository pacienteRepository;                    //Esta inyeccion es obligatoria para utilizar jpa
                                                                            //asique debo declarar el uso de objeto repositorio
    public PacienteService(PacienteRepository pacienteRepository) {         //e inyectarlo en el constructor de paciente service.
        this.pacienteRepository = pacienteRepository;                       //Ocupara una sola vez y no volvera a solicitar un nuevo objeto por metodo
    }

    
    public List<PacienteModel> listAll(){
        return pacienteRepository.findAll();
    }

    public Optional<PacienteModel> findById(Long id){
        return pacienteRepository.findById(id);
    }

    public Optional<PacienteModel> findByRut(String rut){ //el optional se usa solo para metodos de busqueda, dado que entrega una respuesta en caso de no
        return pacienteRepository.findByRut(rut); //encontrar un valor
    }

    public PacienteModel save(PacienteModel paciente) {
        return pacienteRepository.save(paciente);
    }

    public void delet(Long id) {
        pacienteRepository.deleteById(id);
    }

}