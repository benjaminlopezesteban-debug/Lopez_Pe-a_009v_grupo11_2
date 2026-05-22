package com.proyecto.estante.model;

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
@Table(name = "ESTANTE")
public class EstanteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEstante;

    @Column(nullable = false, unique = true)    
    private int numEstante;

    @Column(nullable = false, unique = true)
    private int numBodega; 
}
