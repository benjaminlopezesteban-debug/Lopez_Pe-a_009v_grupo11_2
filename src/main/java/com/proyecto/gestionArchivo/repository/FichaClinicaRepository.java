package com.proyecto.gestionArchivo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.gestionArchivo.model.FichaClinicaModel;

@Repository
public interface FichaClinicaRepository extends JpaRepository<FichaClinicaModel, String> {
}
