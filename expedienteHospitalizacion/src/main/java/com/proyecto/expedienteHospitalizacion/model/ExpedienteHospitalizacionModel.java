package com.proyecto.expedienteHospitalizacion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Entity
@Table(name= "EXPEDIENTE_HOSPITALIZACION")
public class ExpedienteHospitalizacionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idExpediente;
    
    //codigo de la expediente
    @Column(name = "cod_expediente", nullable=false)
    private String codExpediente;
    //rut del paciente
    @Column(name = "rut_paciente", nullable=false, length = 20)
    private String rutPaciente;

    //esta digitalizado?
    @Column (name = "digitalizacion", nullable=false)
    private boolean isDigitalizacion;

    //id_Reserva
    @Column (name = "id_reserva", nullable=false)
    private Long idBooking;

    // id del registro de archivado (opcional)
    @Column(name = "id_registro_archivado")
    private Long idRegistroArchivado;

}
