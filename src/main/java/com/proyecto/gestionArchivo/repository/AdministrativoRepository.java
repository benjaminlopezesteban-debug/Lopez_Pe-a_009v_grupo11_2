package com.proyecto.gestionArchivo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.gestionArchivo.model.AdministrativoModel;

@Repository
public interface AdministrativoRepository extends JpaRepository<AdministrativoModel, Long> {
    Optional<AdministrativoModel> findByRut(String rut);
    boolean existsByRut(String rut);
}
