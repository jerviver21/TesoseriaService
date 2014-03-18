ALTER TABLE registro_pago DROP COLUMN no_detraccion;
ALTER TABLE registro_pago ADD id_detraccion bigint;
ALTER TABLE registro_pago ADD CONSTRAINT detracion_pago_fk FOREIGN KEY (id_detraccion) REFERENCES detraccion(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
