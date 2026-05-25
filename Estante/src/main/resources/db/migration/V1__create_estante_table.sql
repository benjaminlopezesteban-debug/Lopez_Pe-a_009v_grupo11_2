CREATE TABLE estante (
    id_estante  BIGSERIAL PRIMARY KEY,
    num_estante INT NOT NULL UNIQUE,
    num_bodega  INT NOT NULL UNIQUE
);
