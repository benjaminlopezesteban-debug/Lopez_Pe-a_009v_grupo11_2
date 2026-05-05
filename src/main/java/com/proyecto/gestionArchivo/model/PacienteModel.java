package com.proyecto.gestionArchivo.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "PACIENTE")
public class PacienteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 13)
    @NotBlank(message = "El rut es obligatorio")
    @Size(max = 13, message = "El rut no puede superar 13 caracteres")
    private String rut;

    @Column(name = "pnombre", nullable = false, length = 100)
    @NotBlank(message = "El primer nombre es obligatorio")
    @Size(max = 100, message = "El primer nombre no puede superar 100 caracteres")
    private String pnombre;

    @Column(name = "snombre", nullable = false, length = 100)
    @NotBlank(message = "El segundo nombre es obligatorio")
    @Size(max = 100, message = "El segundo nombre no puede superar 100 caracteres")
    private String snombre;

    @Column(name = "appaterno", nullable = false, length = 150)
    @NotBlank(message = "El apellido paterno es obligatorio")
    @Size(max = 150, message = "El apellido paterno no puede superar 150 caracteres")
    private String appaterno;

    @Column(name = "apmaterno", nullable = false, length = 150)
    @NotBlank(message = "El apellido materno es obligatorio")
    @Size(max = 150, message = "El apellido materno no puede superar 150 caracteres")
    private String apmaterno;

    @Column(name = "fecha_nacimiento")
    @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
    private LocalDate bornDate;

    @Column(name = "direccion_particular", nullable = false, length = 150)
    @NotBlank(message = "La direccion es obligatoria")
    @Size(max = 150, message = "La direccion no puede superar 150 caracteres")
    private String direction;

    @Column(nullable = false, length = 25)
    @NotBlank(message = "La nacionalidad es obligatoria")
    @Size(max = 25, message = "La nacionalidad no puede superar 25 caracteres")
    private String nacionality;

}
