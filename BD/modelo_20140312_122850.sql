ALTER TABLE proveedor DROP COLUMN telefono fijo;
ALTER TABLE proveedor DROP COLUMN telefono celular;
ALTER TABLE proveedor ADD telefono_celular varchar(255);
ALTER TABLE proveedor ADD telefono_fijo varchar(255);
ALTER TABLE registro_factura DROP CONSTRAINT cuenta_fact_fk;
ALTER TABLE registro_factura DROP COLUMN id_cuenta_proveedor;
ALTER TABLE registro_factura ADD id_cuenta_proveedor int4;
ALTER TABLE registro_factura ADD CONSTRAINT cuenta_fact_fk FOREIGN KEY (id_cuenta_proveedor) REFERENCES cuentas_proveedor(id) ON DELETE NO ACTION ON UPDATE NO ACTION;
