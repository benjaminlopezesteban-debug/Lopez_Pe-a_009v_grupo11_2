package com.proyecto.expedienteHospitalizacion.repository;

import com.proyecto.expedienteHospitalizacion.model.ExpedienteHospitalizacionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpedienteHospitalizacionRepository extends JpaRepository<ExpedienteHospitalizacionModel, Long> {

    // Busca expediente por código único
    Optional<ExpedienteHospitalizacionModel> findByCodExpediente(String codExpediente);

    // Lista todos los expedientes de un paciente por su rut
    List<ExpedienteHospitalizacionModel> findByRutPaciente(String rutPaciente);

    // Busca el expediente asociado a una reserva
    Optional<ExpedienteHospitalizacionModel> findByIdBooking(Long idBooking);

    // Lista expedientes según estado de digitalización
    List<ExpedienteHospitalizacionModel> findByDigitalizacion(boolean digitalizacion);

    // Verifica si ya existe un expediente con ese código
    boolean existsByCodExpediente(String codExpediente);

    // Verifica si ya existe un expediente para esa reserva
    boolean existsByIdBooking(Long idBooking);
}
