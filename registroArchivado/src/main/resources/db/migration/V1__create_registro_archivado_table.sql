CREATE TABLE registro_archivado (
    id_registro_archivado BIGSERIAL    PRIMARY KEY,
    folio_ficha           VARCHAR(50)  NOT NULL,
    id_administrativo     BIGINT       NOT NULL,
    id_expediente         BIGINT       NOT NULL,
    fecha_archivado       TIMESTAMP    NOT NULL,
    observacion           VARCHAR(500)
);
