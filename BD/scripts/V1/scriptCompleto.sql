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
ALTER TABLE proveedor ADD email varchar(255);
ALTER TABLE proveedor ADD persona_contacto varchar(255);
ALTER TABLE proveedor RENAME COLUMN no_cuenta TO no_cuenta_detraccion;
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
ALTER TABLE registro_factura ADD CONSTRAINT reg_pag_fact_fk FOREIGN KEY (id_registro_pago) REFERENCES registro_pago(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE registro_factura ADD CONSTRAINT concept_reg_fact_fk FOREIGN KEY (id_concepto) REFERENCES concepto_pago(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE registro_factura ADD id_empresa bigint;
ALTER TABLE registro_factura ADD id_estado int4;
ALTER TABLE registro_factura ADD CONSTRAINT estado_fact_fk FOREIGN KEY (id_estado) REFERENCES estado_pago(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE registro_factura ADD CONSTRAINT acuerdo_fact_fk FOREIGN KEY (id_acuerdo_pago) REFERENCES acuerdo_pago(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE registro_factura ADD CONSTRAINT empresa_fact_fk FOREIGN KEY (id_empresa) REFERENCES empresa(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE empresa ADD no_licencia varchar(500);
ALTER TABLE registro_factura ADD CONSTRAINT proveedor_fact_fk FOREIGN KEY (id_proveedor) REFERENCES proveedor(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE cuentas_proveedor ADD id_proveedor bigint NOT NULL;
ALTER TABLE cuentas_empresa ADD id_empresa bigint;
ALTER TABLE cuentas_empresa ADD FK_empresa_id bigserial NOT NULL;
ALTER TABLE cuentas_empresa ADD CONSTRAINT empresa_cuenta_fk FOREIGN KEY (FK_empresa_id) REFERENCES empresa(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE cuentas_empresa DROP CONSTRAINT empresa_cuenta_fk;
ALTER TABLE cuentas_empresa ADD CONSTRAINT empresa_cuenta_fk FOREIGN KEY (id_empresa) REFERENCES empresa(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE cuentas_empresa DROP COLUMN FK_empresa_id;
ALTER TABLE cuentas_proveedor ADD CONSTRAINT cuenta_proveedor_fk FOREIGN KEY (id_proveedor) REFERENCES proveedor(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE registro_pago ADD CONSTRAINT cuenta_ori_fk FOREIGN KEY (id_cuenta_origen) REFERENCES cuentas_empresa(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE registro_pago ADD CONSTRAINT cuenta_destino_fk FOREIGN KEY (id_cuenta_destino) REFERENCES cuentas_proveedor(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE registro_pago ADD CONSTRAINT proveedor_pago_fk FOREIGN KEY (id_proveedor) REFERENCES proveedor(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE registro_pago ADD CONSTRAINT empresa_pago_fk FOREIGN KEY (id_empresa) REFERENCES empresa(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE registro_pago ADD CONSTRAINT estado_pago_fk FOREIGN KEY (id_estado) REFERENCES estado_pago(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE proveedor ADD telefono_celular varchar(255);
ALTER TABLE proveedor ADD telefono_fijo varchar(255);
ALTER TABLE registro_factura DROP COLUMN id_cuenta_proveedor;
ALTER TABLE registro_factura ADD id_cuenta_proveedor int4;
ALTER TABLE registro_factura ADD CONSTRAINT cuenta_fact_fk FOREIGN KEY (id_cuenta_proveedor) REFERENCES cuentas_proveedor(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE registro_pago ADD sede_empresa varchar(1000);
ALTER TABLE registro_pago ADD nombre varchar(1000);
ALTER TABLE registro_pago ADD mon_org varchar(10);
ALTER TABLE registro_pago ADD mon_des varchar(10);
CREATE TABLE notificacion (
	id bigserial NOT NULL,
	criterio varchar(255),
	mensaje varchar(1500),
	fecha date DEFAULT now(),
	archivo varchar(255),
	licencia varchar(255),
	campo varchar(255),
	tipo varchar(255)
);
ALTER TABLE notificacion ADD CONSTRAINT notificacion_pk PRIMARY KEY(id);
ALTER TABLE proveedor ADD informacion_actualizada boolean DEFAULT false;
ALTER TABLE registro_factura ADD url_soporte varchar(1500);
ALTER TABLE registro_factura ADD num_factura bigint;
ALTER TABLE registro_factura RENAME COLUMN no_factura TO serie_no_factura;
ALTER TABLE registro_factura DROP COLUMN serie_fact;
ALTER TABLE registro_factura ADD serie_factura int4;
ALTER TABLE registro_pago DROP COLUMN no_detraccion;
ALTER TABLE registro_pago ADD id_detraccion bigint;
ALTER TABLE registro_pago ADD CONSTRAINT detracion_pago_fk FOREIGN KEY (id_detraccion) REFERENCES detraccion(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE empresa ADD telefono_celular varchar(500);
ALTER TABLE empresa ADD persona_contacto varchar(500);
ALTER TABLE empresa ADD telefono_fijo varchar(500);
ALTER TABLE empresa ADD email varchar(500);
ALTER TABLE detraccion ADD fecha_emision date;
ALTER TABLE detraccion ADD base_imponible numeric(10, 2);
ALTER TABLE detraccion ADD porcentaje int4;
ALTER TABLE detraccion ADD monto_total numeric(10, 2);
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
INSERT INTO estado_pago VALUES (0, 'Anulado');
INSERT INTO estado_pago VALUES (1, 'Pendiente');
INSERT INTO estado_pago VALUES (2, 'Procesada');
INSERT INTO estado_pago VALUES (3, 'Rechazada');
INSERT INTO acuerdo_pago VALUES (0, '7 días - Fecha Emisión', 7);
INSERT INTO acuerdo_pago VALUES (1, '15 días - Fecha Emisión', 15);
INSERT INTO acuerdo_pago VALUES (2, '30 días - Fecha Emisión', 30);
INSERT INTO acuerdo_pago VALUES (3, '45 días - Fecha Emisión', 45);
INSERT INTO acuerdo_pago VALUES (4, '7 días - Fecha Entrega', 7);
INSERT INTO acuerdo_pago VALUES (5, '15 días - Fecha Entrega', 15);
INSERT INTO acuerdo_pago VALUES (6, '30 días - Fecha Entrega', 30);
INSERT INTO acuerdo_pago VALUES (7, '45 días - Fecha Entrega', 45);


INSERT INTO concepto_pago VALUES (0, 'TRANSPORTE');
INSERT INTO concepto_pago VALUES (1, 'SERVICIOS');

INSERT INTO parametro (nombre, valor, tipo) VALUES ('nombre_bucket','tesoreria_soportes','AWS');
INSERT INTO parametro (nombre, valor, tipo) VALUES ('url_imagenes','http://s3.amazonaws.com','AWS');

COPY service (id, cod, descripcion) FROM stdin;
113	009	ARENA Y PIEDRA
114	019	ARRENDAMIENTO DE BIENES
115	022	OTROS SERVICIOS EMPRESARIALES
116	037	DEMÁS SERVICIOS GRAVADOS CON EL IGV
117	027	TRANSPORTE DE CARGA
118	020	REPARACIÓN DE BIENES MUEBLES
119	030	CONTRATOS DE CONSTRUCCIÓN
120	008	MADERA
121	021	MOVIMIENTO DE CARGA
122	026	SERVICIO DE TRANSPORTE DE PERSONAS
124	020	MANT. Y REPARACION BIENES MUEBLES
125	037	DEMAS SERVICIOS GRABADOS
126	026	TRANSPORTE DE PERSONAS
127	019	ALQUILER BIENES MUEBLES
\.


--
-- Name: service_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('service_id_seq', 127, true);

delete from reporte where id = 1;
delete from reporte where id = 3;


INSERT INTO licencia (no_licencia, fecha_inicio, fecha_fin, owner) VALUES ('00001', now(), '2015-12-31', 'owner');
update users set id_licencia = 1 where usr='gaby';
update empresa set no_licencia = '00001';


delete from concepto_pago;
INSERT INTO concepto_pago VALUES (1, 'LOGISTICA');
INSERT INTO concepto_pago VALUES (2, 'SERVICIOS');
INSERT INTO concepto_pago VALUES (3, 'SUBCONTRATOS');
INSERT INTO concepto_pago VALUES (4, 'IMPORTACIONES');
INSERT INTO concepto_pago VALUES (5, 'EQUIPOS');
INSERT INTO concepto_pago VALUES (6, 'RRHH');
INSERT INTO concepto_pago VALUES (7, 'OOMM');






