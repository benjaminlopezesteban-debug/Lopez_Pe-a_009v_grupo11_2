package com.proyecto.expedienteHospitalizacion.dto.response;


import lombok.Builder;
import lombok.Data;


import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class ReservaHoraResponse {
        
    private Long idBooking;
    private LocalDate dateBooking;
    private LocalTime hourBooking;
    private String specialty;
    private Long idPaciente;
    private String professional;

    private PacienteResponse paciente;


}
