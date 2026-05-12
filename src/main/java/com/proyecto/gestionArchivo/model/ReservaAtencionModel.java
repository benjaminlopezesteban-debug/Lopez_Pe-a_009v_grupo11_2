package com.proyecto.gestionArchivo.model;

import java.time.LocalDate;
import java.time.LocalTime;

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
@Table(name = "RESERVA_ATENCION")
public class ReservaAtencionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservaAtencion;

    @Column(nullable = false)
    private LocalDate fechaReservada;

    @Column(nullable = false)
    private LocalTime horaReservada;

    @Column(nullable = false, length = 120)
    private String especialidad;

    @Column(nullable = false, length = 150)
    private String profesional;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_paciente", nullable = false)
    private PacienteModel paciente;
}
