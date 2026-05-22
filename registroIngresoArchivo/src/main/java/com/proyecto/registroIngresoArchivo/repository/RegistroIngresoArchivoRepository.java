package com.proyecto.registroIngresoArchivo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.registroIngresoArchivo.model.RegistroIngresoArchivoModel;

@Repository
public interface RegistroIngresoArchivoRepository extends JpaRepository<RegistroIngresoArchivoModel, Long> {
}
