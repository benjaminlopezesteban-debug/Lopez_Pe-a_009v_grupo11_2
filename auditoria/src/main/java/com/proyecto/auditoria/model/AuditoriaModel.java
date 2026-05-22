package com.proyecto.auditoria.model;

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
@Table(name = "AUDITORIA")
public class AuditoriaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAuditoria;

    // referencias externas como IDs/clave
    @Column(name = "id_administrativo", nullable = false)
    private Long idAdministrativo;

    @Column(name = "folio_ficha", nullable = false, length = 50)
    private String folioFicha;

    @Column(nullable = false)
    private LocalDateTime fechaAuditoria;

    @Column(nullable = false, length = 80)
    private String accion;

    @Column(length = 500)
    private String detalle;
}
