package com.proyecto.Estante.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.Estante.model.EstanteModel;
import com.proyecto.Estante.repository.EstanteRepository;

@Service
@Transactional
public class EstanteService {

    private final EstanteRepository estanteRepository;

    public EstanteService(EstanteRepository estanteRepository){
        this.estanteRepository = estanteRepository; 
    }

    public List<EstanteModel> listAll(){
        return estanteRepository.findAll();
    }
    

}
