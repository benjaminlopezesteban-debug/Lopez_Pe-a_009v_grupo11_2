package com.proyecto.Estante.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.Estante.model.EstanteModel;

@Repository
public interface EstanteRepository extends JpaRepository<EstanteModel, Long>{

    Optional<EstanteModel> findById(Long idEstante);

    Optional<EstanteModel> findByNumEstante(int numEstante);

}
