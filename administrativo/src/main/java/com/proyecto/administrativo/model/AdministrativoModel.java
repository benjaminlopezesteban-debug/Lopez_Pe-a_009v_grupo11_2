package com.proyecto.administrativo.model;

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
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idAdministrativo;

    @Column(nullable = false, unique = true, length=13)
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

    @Column(name = "fecha_contrato")
    private LocalDate contracDate;

    @Column(name = "mail", nullable = false, unique = true, length = 150)
    private String mail;

    @Column(name = "cargo", nullable = false, length = 150)
    private String charge;


}
