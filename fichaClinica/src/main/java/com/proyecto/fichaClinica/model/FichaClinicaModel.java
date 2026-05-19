package com.proyecto.fichaClinica.FichaClinicaModel;

import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "FICHA_CLINICA")
public class FichaClinicaModel {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long folioFicha;

    @Column(name = "id_paciente", nullable = false)
    private Long idPaciente; 

    @Column(name = "id_estante", nullable = false)
    private Long idEstante; 

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDate fechaCreacion;

}
