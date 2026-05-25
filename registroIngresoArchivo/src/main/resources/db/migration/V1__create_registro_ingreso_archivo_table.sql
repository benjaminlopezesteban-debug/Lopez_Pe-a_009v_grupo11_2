CREATE TABLE registro_ingreso_archivo (
    id_registro_ingreso_archivo BIGSERIAL    PRIMARY KEY,
    id_expediente               BIGINT       NOT NULL,
    id_administrativo           BIGINT       NOT NULL,
    fecha_ingreso               TIMESTAMP    NOT NULL,
    observacion                 VARCHAR(500)
);
