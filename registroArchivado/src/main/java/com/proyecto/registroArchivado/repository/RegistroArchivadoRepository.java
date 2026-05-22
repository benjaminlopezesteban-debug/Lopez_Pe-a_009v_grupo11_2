package com.proyecto.registroArchivado.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.registroArchivado.model.RegistroArchivadoModel;

@Repository
public interface RegistroArchivadoRepository extends JpaRepository<RegistroArchivadoModel, Long> {
}
