CREATE TABLE expediente_hospitalizacion (
    id_expediente         BIGSERIAL    PRIMARY KEY,
    cod_expediente        VARCHAR(255) NOT NULL,
    rut_paciente          VARCHAR(20)  NOT NULL,
    digitalizacion        BOOLEAN      NOT NULL,
    id_reserva            BIGINT       NOT NULL,
    id_registro_archivado BIGINT
);
