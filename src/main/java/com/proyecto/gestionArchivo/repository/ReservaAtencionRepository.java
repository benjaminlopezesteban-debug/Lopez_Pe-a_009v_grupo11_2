package com.proyecto.gestionArchivo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.gestionArchivo.model.ReservaAtencionModel;

@Repository
public interface ReservaAtencionRepository extends JpaRepository<ReservaAtencionModel, Long> {
}
