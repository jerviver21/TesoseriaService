
--Reporte 1 ************************************************************************************************************************************************
INSERT INTO reporte  VALUES ('totalxproveedor','MASTER, TESORERO',1, 3);
INSERT INTO archivo (nombre, nombre_archivo, nombre_jasper, id_tipo_archivo, id_reporte) VALUES ('totalxproveedor','reporteproveedor.xls','totalxproveedor.jasper',1,3);
--insert into data (id, nombre, descripcion) values (500,'TIPO_PUBLICACION','tipos de publicación');
insert into parametros_reporte (nombre, etiqueta, id_tipo, id_data, id_reporte, id) values ('proveedor', 'RUC DEL PROVEEDOR:', 2, null, 3, 5);
insert into parametros_reporte (nombre, etiqueta, id_tipo, id_data, id_reporte, id) values ('fecha_ini', 'DESDE:', 3, null, 3, 6);
insert into parametros_reporte (nombre, etiqueta, id_tipo, id_data, id_reporte, id) values ('fecha_fin', 'HASTA:', 3, null, 3, 7);
--insert into parametros_reporte (nombre, etiqueta, id_tipo, id_data, id_reporte, id) values ('tipo', 'TIPO:', 5, 500, 2, 4);
--**********************************************************************************************************************************************************
