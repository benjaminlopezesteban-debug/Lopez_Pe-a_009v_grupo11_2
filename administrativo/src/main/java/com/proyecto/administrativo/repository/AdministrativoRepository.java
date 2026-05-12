package com.proyecto.administrativo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.administrativo.model.AdministrativoModel;

@Repository
public interface AdministrativoRepository extends JpaRepository<AdministrativoModel, Long>{

    Optional<AdministrativoModel> findByRut(String rut);
    
    Optional<AdministrativoModel> findByNameLastname(String pnombre, String appaterno);

    List<AdministrativoModel> findByCharge(String charge);
}