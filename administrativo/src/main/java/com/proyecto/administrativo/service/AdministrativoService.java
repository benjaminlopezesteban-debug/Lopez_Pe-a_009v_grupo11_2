package com.proyecto.administrativo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.administrativo.model.AdministrativoModel;
import com.proyecto.administrativo.repository.AdministrativoRepository;

@Service
@Transactional
public class AdministrativoService {

    private final AdministrativoRepository administrativoRepository;

    public AdministrativoService(AdministrativoRepository administrativoRepository){
        this.administrativoRepository = administrativoRepository;
    }

    public List<AdministrativoModel> listAll(){
        return administrativoRepository.findAll();
    }

    public Optional<AdministrativoModel> findById(Long id){
        return administrativoRepository.findById(id);
    }

    public Optional<AdministrativoModel> findByRut(String rut){
        return administrativoRepository.findByRut(rut);
    }

    public AdministrativoModel save(AdministrativoModel administrativo){
        return administrativoRepository.save(administrativo);
    }

    public void delet(Long id){
        administrativoRepository.deleteById(id);
    }

    public List<AdministrativoModel> findByCharge(String charge){
        return administrativoRepository.findByCharge(charge);
    }
}

