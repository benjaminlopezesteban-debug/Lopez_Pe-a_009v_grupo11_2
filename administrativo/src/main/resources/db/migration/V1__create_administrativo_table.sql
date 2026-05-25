CREATE TABLE administrativo (
    id_administrativo BIGSERIAL PRIMARY KEY,
    rut               VARCHAR(13)  NOT NULL UNIQUE,
    pnombre           VARCHAR(100) NOT NULL,
    snombre           VARCHAR(100) NOT NULL,
    appaterno         VARCHAR(150) NOT NULL,
    apmaterno         VARCHAR(150) NOT NULL,
    fecha_nacimiento  DATE,
    fecha_contrato    DATE,
    mail              VARCHAR(150) NOT NULL UNIQUE,
    cargo             VARCHAR(150) NOT NULL
);
