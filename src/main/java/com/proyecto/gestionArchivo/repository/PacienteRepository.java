package com.proyecto.gestionArchivo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.gestionArchivo.model.PacienteModel;

@Repository
public interface PacienteRepository extends JpaRepository<PacienteModel, Long> {
    

    Optional<PacienteModel> findByRut(String rut);

    boolean existsByRut(String rut);

    Optional<PacienteModel> findByPnombreAndAppaternoAndApmaterno(String pnombre, String appaterno, String apmaterno);

}
