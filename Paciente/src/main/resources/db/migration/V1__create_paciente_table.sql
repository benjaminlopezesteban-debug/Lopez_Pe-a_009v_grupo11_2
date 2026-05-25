CREATE TABLE paciente (
    id_paciente  BIGSERIAL    PRIMARY KEY,
    num_rut      VARCHAR(13)  NOT NULL UNIQUE,
    pnombre      VARCHAR(100) NOT NULL,
    snombre      VARCHAR(100),
    papellido    VARCHAR(150) NOT NULL,
    sapellido    VARCHAR(150) NOT NULL,
    fecha_naci   DATE         NOT NULL,
    direccion    VARCHAR(150) NOT NULL,
    nacionalidad VARCHAR(25)  NOT NULL
);
