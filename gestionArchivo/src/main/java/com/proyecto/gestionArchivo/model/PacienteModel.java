package com.proyecto.gestionArchivo.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity //--
@Table(name = "PACIENTE")
public class PacienteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 13) //Ojo unique estandar para elementos que no pueden repetirse
    private String rut;

    @Column(name = "pnombre", nullable = false, length = 100)
    private String pnombre;

    @Column(name = "snombre", nullable = false, length = 100)
    private String snombre;

    @Column(name = "appaterno", nullable = false, length = 150)
    private String appaterno;

    @Column(name = "apmaterno", nullable = false, length = 150)
    private String apmaterno;

    @Column(name = "fecha_nacimiento")
    private LocalDate bornDate;

    @Column(name = "direccion_particular", nullable = false, length = 150)
    private String direction;

    @Column(nullable = false, length = 25)
    private String nacionality;

    //@Column(name = "last_hospitalization", nullable = false)
    //private LocalDate lastHospitalization;

}
