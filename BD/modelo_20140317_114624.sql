ALTER TABLE registro_factura ADD num_factura bigint;
ALTER TABLE registro_factura RENAME COLUMN no_factura TO serie_no_factura;
