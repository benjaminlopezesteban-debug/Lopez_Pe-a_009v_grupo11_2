package com.proyecto.auditoria.dto.response;

import java.time.LocalDate;

public record AdministrativoResponse(
        Long idAdministrativo,
        String rut,
        String pnombre,
        String snombre,
        String appaterno,
        String apmaterno,
        LocalDate bornDate,
        LocalDate contracDate,
        String mail,
        String charge
) {}
