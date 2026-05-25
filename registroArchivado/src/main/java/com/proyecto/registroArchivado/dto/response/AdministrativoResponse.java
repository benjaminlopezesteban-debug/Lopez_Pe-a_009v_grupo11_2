package com.proyecto.registroArchivado.dto.response;

import java.time.LocalDate;

public record AdministrativoResponse(
        Long idAdministrativo,
        String rut,
        String pnombre,
        String appaterno,
        String mail,
        String charge
) {}
