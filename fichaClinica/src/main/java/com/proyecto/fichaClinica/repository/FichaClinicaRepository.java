package com.proyecto.fichaClinica.repository;

import com.proyecto.fichaClinica.FichaClinicaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FichaClinicaRepository extends JpaRepository<FichaClinicaModel, Long> {

    // Busca todas las fichas de un paciente por su id
    List<FichaClinicaModel> findByIdPaciente(Long idPaciente);

    // Busca una ficha por el rut del paciente (se filtrará en service usando el MS Paciente)
    Optional<FichaClinicaModel> findByFolioFicha(Long folioFicha);

    // Busca todas las fichas asociadas a un estante
    List<FichaClinicaModel> findByIdEstante(Long idEstante);
}
