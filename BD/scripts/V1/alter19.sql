CREATE TABLE servicios (
	codigo varchar(3) NOT NULL,
	nombre varchar(1500) NOT NULL
);
ALTER TABLE servicios ADD CONSTRAINT servicios_pk PRIMARY KEY(codigo);
ALTER TABLE detraccion ADD nombre_servicio varchar(1500);
ALTER TABLE servicios DROP CONSTRAINT servicios_pk;
ALTER TABLE servicios ADD id bigserial NOT NULL;
ALTER TABLE servicios ADD CONSTRAINT servicios1_pk PRIMARY KEY(id);
CREATE UNIQUE INDEX servicios_ui1 ON servicios (nombre);
