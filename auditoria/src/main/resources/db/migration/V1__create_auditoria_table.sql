CREATE TABLE auditoria (
    id_auditoria      BIGSERIAL    PRIMARY KEY,
    id_administrativo BIGINT       NOT NULL,
    folio_ficha       VARCHAR(50)  NOT NULL,
    fecha_auditoria   TIMESTAMP    NOT NULL,
    accion            VARCHAR(80)  NOT NULL,
    detalle           VARCHAR(500)
);
