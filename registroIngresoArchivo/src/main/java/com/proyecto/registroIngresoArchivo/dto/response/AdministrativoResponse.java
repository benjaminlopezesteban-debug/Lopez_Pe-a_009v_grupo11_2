package com.proyecto.registroIngresoArchivo.dto.response;

public record AdministrativoResponse(
        Long idAdministrativo,
        String rut,
        String pnombre,
        String appaterno,
        String mail,
        String charge
) {}
