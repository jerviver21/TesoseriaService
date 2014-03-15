ALTER TABLE registro_pagos RENAME TO registro_pago;
ALTER TABLE adquiriente RENAME TO empresa;
ALTER TABLE detracciones RENAME TO detraccion;
CREATE TABLE estado_pago (
	id int4 NOT NULL,
	nombre varchar(255) NOT NULL
);
ALTER TABLE estado_pago ADD CONSTRAINT estado_pago_pk PRIMARY KEY(id);
CREATE TABLE acuerdo_pago (
	id int4 NOT NULL,
	nombre varchar(255) NOT NULL,
	num_dias int4
);
ALTER TABLE acuerdo_pago ADD CONSTRAINT acuerdo_pago_pk PRIMARY KEY(id);
CREATE TABLE concepto_pago (
	id int4 NOT NULL,
	nombre varchar(255) NOT NULL
);
ALTER TABLE concepto_pago ADD CONSTRAINT concepto_pago_pk PRIMARY KEY(id);
CREATE TABLE cuentas_proveedor (
	id serial NOT NULL,
	no_cuenta varchar(255) NOT NULL,
	moneda varchar(255) NOT NULL
);
ALTER TABLE cuentas_proveedor ADD CONSTRAINT cuentas_proveedor_pk PRIMARY KEY(id);
CREATE TABLE cuentas_empresa (
	id serial NOT NULL,
	no_cuenta varchar(255) NOT NULL,
	moneda varchar(255) NOT NULL
);
ALTER TABLE cuentas_empresa ADD CONSTRAINT cuentas_empresa_pk PRIMARY KEY(id);
