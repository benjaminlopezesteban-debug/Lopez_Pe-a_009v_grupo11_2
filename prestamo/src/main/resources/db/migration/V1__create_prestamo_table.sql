CREATE TABLE prestamo (
    id_prestamo       BIGSERIAL   PRIMARY KEY,
    id_administrativo BIGINT      NOT NULL,
    folio_ficha       VARCHAR(50) NOT NULL,
    fecha_prestamo    DATE        NOT NULL,
    fecha_devolucion  DATE,
    estado            VARCHAR(40) NOT NULL
);
