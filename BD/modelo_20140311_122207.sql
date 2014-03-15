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
