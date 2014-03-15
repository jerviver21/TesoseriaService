CREATE TABLE registro_pagos (
	id bigserial NOT NULL,
	tipo_operacion varchar(255),
	subtipo_operacion varchar(255),
	no_planilla varchar(255),
	fecha date,
	rp varchar(255),
	cuenta_origen varchar(255),
	consecutivo int4,
	nombre varchar(255),
	num_id varchar(255),
	monto numeric(10, 2),
	monto_abonado numeric(10, 2),
	no_cuenta varchar(255),
	estado varchar(255),
	observacion varchar(255),
	mon_origen varchar(255),
	otro varchar(255),
	mon_cuenta varchar(255)
);
ALTER TABLE registro_pagos ADD CONSTRAINT registro_pagos_pk PRIMARY KEY(id);
CREATE UNIQUE INDEX registro_pagos_ui1 ON registro_pagos (no_planilla,consecutivo,fecha);
CREATE TABLE proveedor (
	id bigserial NOT NULL,
	razon_social varchar(500),
	ruc varchar(255),
	cod_servicio varchar(10),
	cod_opr varchar(10),
	no_cuenta varchar(25)
);
ALTER TABLE proveedor ADD CONSTRAINT proveedor_pk PRIMARY KEY(id);
CREATE UNIQUE INDEX provedor_ui1 ON proveedor (ruc);
CREATE TABLE detracciones (
	id bigserial NOT NULL,
	id_proveedor bigint,
	cod_servicio varchar(10),
	importe numeric(10, 2),
	cod_operacion varchar(10),
	periodo_tributario varchar(6),
	id_adquiriente bigint,
	fecha_carga date DEFAULT now(),
	comprobante varchar(255),
	no_factura varchar(255),
	fecha_pago date
);
ALTER TABLE detracciones ADD CONSTRAINT detracciones_pk PRIMARY KEY(id);
CREATE TABLE adquiriente (
	id bigserial NOT NULL,
	ruc varchar(50),
	nombre varchar(500)
);
ALTER TABLE adquiriente ADD CONSTRAINT adquiriente_pk PRIMARY KEY(id);
CREATE UNIQUE INDEX adquiriente_ui1 ON adquiriente (ruc);
ALTER TABLE detracciones ADD CONSTRAINT proveedor_detre_fk FOREIGN KEY (id_proveedor) REFERENCES proveedor(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE detracciones ADD CONSTRAINT adq_fk FOREIGN KEY (id_adquiriente) REFERENCES adquiriente(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
