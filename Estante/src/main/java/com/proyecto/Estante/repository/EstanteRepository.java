package com.proyecto.Estante.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.Estante.model.EstanteModel;

@Repository
public interface EstanteRepository extends JpaRepository<EstanteModel, Long>{


}
