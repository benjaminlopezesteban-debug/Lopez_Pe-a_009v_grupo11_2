package com.proyecto.gestionArchivo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.gestionArchivo.model.EstanteModel;

@Repository
public interface EstanteRepository extends JpaRepository<EstanteModel, Long> {
}
