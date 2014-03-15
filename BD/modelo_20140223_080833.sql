CREATE TABLE proveedor (
	id bigserial NOT NULL,
	razon_social varchar(500),
	ruc varchar(255),
	valor numeric(10, 2),
	cod_servicio varchar(10),
	cod_opr varchar(10)
);
ALTER TABLE proveedor ADD CONSTRAINT proveedor_pk PRIMARY KEY(id);
CREATE UNIQUE INDEX provedor_ui1 ON proveedor (ruc);
CREATE TABLE detracciones (
	id bigserial NOT NULL,
	id_proveedor bigint,
	cod_servicio varchar(10),
	importe numeric(10, 2),
	cod_operacion varchar(10),
	periodo_tributario varchar(6)
);
ALTER TABLE detracciones ADD CONSTRAINT detracciones_pk PRIMARY KEY(id);
ALTER TABLE proveedor DROP COLUMN valor;
ALTER TABLE proveedor ADD no_cuenta varchar(25);
ALTER TABLE detracciones ADD CONSTRAINT proveedor_detre_fk FOREIGN KEY (id_proveedor) REFERENCES proveedor(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
CREATE TABLE adquiriente (
	id bigserial NOT NULL,
	ruc varchar(50),
	nombre varchar(500)
);
ALTER TABLE adquiriente ADD CONSTRAINT adquiriente_pk PRIMARY KEY(id);
CREATE UNIQUE INDEX adquiriente_ui1 ON adquiriente (ruc);
ALTER TABLE detracciones ADD id_adquiriente bigint;
ALTER TABLE detracciones ADD CONSTRAINT adq_fk FOREIGN KEY (id_adquiriente) REFERENCES adquiriente(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
