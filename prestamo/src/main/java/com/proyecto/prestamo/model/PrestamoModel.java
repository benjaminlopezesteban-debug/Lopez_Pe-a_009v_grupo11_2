package com.proyecto.prestamo.model;

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
@Table(name = "PRESTAMO")
public class PrestamoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPrestamo;

    // referencias externas como IDs/clave
    @Column(name = "id_administrativo", nullable = false)
    private Long idAdministrativo;

    @Column(name = "folio_ficha", nullable = false, length = 50)
    private String folioFicha;

    @Column(nullable = false)
    private LocalDate fechaPrestamo;

    private LocalDate fechaDevolucion;

    @Column(nullable = false, length = 40)
    private String estado;
}
