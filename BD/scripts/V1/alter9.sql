ALTER TABLE registro_pago DROP COLUMN mon_cuenta;
ALTER TABLE registro_pago DROP COLUMN mon_origen;
ALTER TABLE registro_pago DROP COLUMN no_cuenta;
ALTER TABLE registro_pago DROP COLUMN cuenta_origen;
ALTER TABLE registro_pago DROP COLUMN num_id;
ALTER TABLE registro_pago DROP COLUMN nombre;
ALTER TABLE registro_pago DROP COLUMN estado;
ALTER TABLE registro_pago ADD no_detraccion varchar(255);
ALTER TABLE registro_pago ADD id_estado int4;
ALTER TABLE registro_pago RENAME COLUMN otro TO otra_informacion;
ALTER TABLE registro_pago ADD socket_documento varchar(1500);
ALTER TABLE registro_pago ADD id_concepto int4;
ALTER TABLE registro_pago ADD id_proveedor bigint;
ALTER TABLE registro_pago ADD id_cuenta_origen int4;
ALTER TABLE registro_pago ADD serie_factura varchar(255);
ALTER TABLE registro_pago ADD id_cuenta_destino int4;
ALTER TABLE registro_pago ADD desc_destino varchar(255);
ALTER TABLE registro_pago ADD id_empresa bigint;
ALTER TABLE registro_pago RENAME COLUMN fecha TO fecha_pago;
ALTER TABLE registro_pago ADD persona_entrega_fact varchar(1000);
ALTER TABLE registro_pago ADD id_acuerdo_pago int4;
ALTER TABLE registro_pago ADD fecha_entrega_fact date;
ALTER TABLE registro_pago ADD fecha_emision_fact date;
ALTER TABLE registro_pago ADD area_entrega_fact varchar(1000);
DROP INDEX registro_pagos_ui1;
ALTER TABLE proveedor DROP COLUMN cod_opr;
ALTER TABLE proveedor DROP COLUMN cod_servicio;
ALTER TABLE proveedor ADD telefono celular varchar(255);
ALTER TABLE proveedor ADD email varchar(255);
ALTER TABLE proveedor ADD persona_contacto varchar(255);
ALTER TABLE proveedor RENAME COLUMN no_cuenta TO no_cuenta_detraccion;
ALTER TABLE proveedor ADD telefono fijo varchar(255);
ALTER TABLE detraccion RENAME COLUMN id_adquiriente TO id_empresa;
ALTER TABLE registro_pago DROP COLUMN area_entrega_fact;
ALTER TABLE registro_pago DROP COLUMN fecha_emision_fact;
ALTER TABLE registro_pago DROP COLUMN fecha_entrega_fact;
ALTER TABLE registro_pago DROP COLUMN id_acuerdo_pago;
ALTER TABLE registro_pago DROP COLUMN persona_entrega_fact;
ALTER TABLE registro_pago DROP COLUMN serie_factura;
ALTER TABLE registro_pago DROP COLUMN id_concepto;
ALTER TABLE registro_pago DROP COLUMN socket_documento;
CREATE UNIQUE INDEX registro_pago_ui1 ON registro_pago (no_planilla,fecha_pago,consecutivo);
CREATE TABLE registro_factura (
	id bigserial NOT NULL,
	id_proveedor bigint,
	id_cuenta_proveedor varchar(255),
	serie_fact varchar(255),
	no_factura varchar(255),
	area_entrega_fact varchar(255),
	persona_entrega_fact varchar(255),
	id_acuerdo_pago int4,
	fecha_emision_fact date,
	fecha_entrega_fact date,
	id_concepto int4,
	monto_total numeric(10, 2),
	moneda varchar(10),
	id_registro_pago bigint,
	fecha_vencimiento date
);
ALTER TABLE registro_factura ADD CONSTRAINT registro_factura_pk PRIMARY KEY(id);
