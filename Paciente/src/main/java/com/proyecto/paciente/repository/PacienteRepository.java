package com.proyecto.paciente.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.paciente.model.PacienteModel;

@Repository
public interface PacienteRepository extends JpaRepository<PacienteModel, Long> {
    boolean existsByNumRut(String numRut);
    PacienteModel findByNumRut(String numRut);
    List<PacienteModel> findByPnombreIgnoreCaseContainingAndPapellidoIgnoreCaseContaining(String pnombre, String papellido);
}
