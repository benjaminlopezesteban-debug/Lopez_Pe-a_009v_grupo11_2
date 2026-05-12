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
@Entity
@Table(name = "ADMINISTRATIVO")
public class AdministrativoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAdministrativo;

    @Column(nullable = false, unique = true, length = 13)
    private String rut;

    @Column(nullable = false, length = 100)
    private String pnombre;

    @Column(length = 100)
    private String snombre;

    @Column(nullable = false, length = 150)
    private String papellido;

    @Column(nullable = false, length = 150)
    private String sapellido;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNaci;

    @Column(name = "fecha_contrato")
    private LocalDate fechaContrato;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 150)
    private String cargo;
}
