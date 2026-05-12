package com.proyecto.gestionArchivo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "REGISTRO_INGRESO_ARCHIVO")
public class RegistroIngresoArchivoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRegistroIngresoArchivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_expediente", nullable = false)
    private ExpedienteHospitalizacionModel expedienteHospitalizacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_administrativo", nullable = false)
    private AdministrativoModel administrativo;

    @Column(nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(length = 500)
    private String observacion;
}
