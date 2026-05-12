package com.proyecto.gestionArchivo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.gestionArchivo.model.ExpedienteHospitalizacionModel;

@Repository
public interface ExpedienteHospitalizacionRepository extends JpaRepository<ExpedienteHospitalizacionModel, Long> {
}
