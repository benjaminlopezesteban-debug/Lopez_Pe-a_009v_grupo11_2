package com.proyecto.estante.repository;

import com.proyecto.estante.model.EstanteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstanteRepository extends JpaRepository<EstanteModel, Long> {

    // Busca un estante por su número de estante
    Optional<EstanteModel> findByNumEstante(int numEstante);

    // Busca un estante por su número de bodega
    Optional<EstanteModel> findByNumBodega(int numBodega);

    // Verifica si ya existe un estante con ese número
    boolean existsByNumEstante(int numEstante);

    // Verifica si ya existe una bodega con ese número
    boolean existsByNumBodega(int numBodega);
}
