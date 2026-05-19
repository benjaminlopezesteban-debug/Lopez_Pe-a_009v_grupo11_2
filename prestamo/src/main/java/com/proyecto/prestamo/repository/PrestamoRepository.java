package com.proyecto.prestamo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.gestionArchivo.model.PrestamoModel;

@Repository
public interface PrestamoRepository extends JpaRepository<PrestamoModel, Long> {
}
