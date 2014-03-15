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
