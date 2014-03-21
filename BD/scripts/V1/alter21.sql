ALTER TABLE detraccion ADD aprobada_x_sunat boolean NOT NULL DEFAULT false;
ALTER TABLE detraccion ADD archivo_comprobante varchar(255);
DROP TABLE servicios;
CREATE TABLE service (
	id serial NOT NULL,
	cod varchar(3),
	descripcion varchar(255)
);
ALTER TABLE service ADD CONSTRAINT service_pk PRIMARY KEY(id);
CREATE UNIQUE INDEX service_ui1 ON service (cod);
DROP INDEX service_ui1;
CREATE UNIQUE INDEX service_ui2 ON service (descripcion);
