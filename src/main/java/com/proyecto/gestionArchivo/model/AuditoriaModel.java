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
@Table(name = "AUDITORIA")
public class AuditoriaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAuditoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_administrativo", nullable = false)
    private AdministrativoModel administrativo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "folio_ficha", nullable = false)
    private FichaClinicaModel fichaClinica;

    @Column(nullable = false)
    private LocalDateTime fechaAuditoria;

    @Column(nullable = false, length = 80)
    private String accion;

    @Column(length = 500)
    private String detalle;
}
