package com.proyecto.registroIngresoArchivo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "REGISTRO_INGRESO_ARCHIVO")
public class RegistroIngresoArchivoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRegistroIngresoArchivo;

    // Referencias externas como IDs
    @Column(name = "id_expediente", nullable = false)
    private Long idExpediente;

    @Column(name = "id_administrativo", nullable = false)
    private Long idAdministrativo;

    @Column(nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(length = 500)
    private String observacion;
}
