package com.proyecto.gestionArchivo.model;

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
@Table(name = "EXPEDIENTE_HOSPITALIZACION")
public class ExpedienteHospitalizacionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idExpediente;

    @Column(nullable = false, unique = true, length = 60)
    private String codExpediente;

    @Column(nullable = false, length = 13)
    private String rutPaciente;

    @Column(nullable = false)
    private Boolean digitalizacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_reserva_atencion", nullable = false)
    private ReservaAtencionModel reservaAtencion;
}
