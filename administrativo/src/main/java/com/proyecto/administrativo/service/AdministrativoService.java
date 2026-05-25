package com.proyecto.administrativo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.administrativo.exceptions.NotFoundException;
import com.proyecto.administrativo.model.AdministrativoModel;
import com.proyecto.administrativo.repository.AdministrativoRepository;

@Service
@Transactional
public class AdministrativoService {

    private final AdministrativoRepository administrativoRepository;

    public AdministrativoService(AdministrativoRepository administrativoRepository) {
        this.administrativoRepository = administrativoRepository;
    }

    @Transactional(readOnly = true)
    public List<AdministrativoModel> listAll() {
        return administrativoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public AdministrativoModel findById(Long id) {
        return administrativoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el administrativo con id: " + id));
    }

    @Transactional(readOnly = true)
    public AdministrativoModel findByRut(String rut) {
        return administrativoRepository.findByRut(rut)
                .orElseThrow(() -> new NotFoundException("No existe el administrativo con rut: " + rut));
    }

    @Transactional(readOnly = true)
    public List<AdministrativoModel> findByCharge(String charge) {
        return administrativoRepository.findByCharge(charge);
    }

    public AdministrativoModel save(AdministrativoModel administrativo) {
        return administrativoRepository.save(administrativo);
    }

    public void delete(Long id) {
        findById(id);
        administrativoRepository.deleteById(id);
    }
}
