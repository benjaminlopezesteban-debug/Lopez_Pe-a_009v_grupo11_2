package com.proyecto.auditoria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.auditoria.model.AuditoriaModel;

@Repository
public interface AuditoriaRepository extends JpaRepository<AuditoriaModel, Long> {
}
