package com.proyecto.gestionArchivo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ESTANTE")
public class EstanteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEstante;

    @Column(nullable = false, unique = true)
    private Integer numEstante;

    @Column(nullable = false)
    private Integer numBodega;

    @Column(length = 120)
    private String ubicacion;
}
