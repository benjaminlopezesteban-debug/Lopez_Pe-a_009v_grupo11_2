CREATE TABLE ficha_clinica (
    folio_ficha     BIGSERIAL PRIMARY KEY,
    id_paciente     BIGINT    NOT NULL,
    id_estante      BIGINT    NOT NULL,
    fecha_creacion  DATE      NOT NULL DEFAULT CURRENT_DATE
);
