package com.proyecto.registroArchivado.model;

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
@Table(name = "REGISTRO_ARCHIVADO")
public class RegistroArchivadoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRegistroArchivado;

    // Referencias a entidades externas, guardadas como IDs/clave
    @Column(name = "folio_ficha", nullable = false, length = 50)
    private String folioFicha;

    @Column(name = "id_administrativo", nullable = false)
    private Long idAdministrativo;

    @Column(name = "id_expediente", nullable = false)
    private Long idExpediente;

    @Column(nullable = false)
    private LocalDateTime fechaArchivado;

    @Column(length = 500)
    private String observacion;
}
