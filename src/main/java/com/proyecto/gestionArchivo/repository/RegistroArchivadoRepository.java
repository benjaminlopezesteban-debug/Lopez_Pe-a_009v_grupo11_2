package com.proyecto.gestionArchivo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.gestionArchivo.model.RegistroArchivadoModel;

@Repository
public interface RegistroArchivadoRepository extends JpaRepository<RegistroArchivadoModel, Long> {
}
