
--Reporte 1 ************************************************************************************************************************************************
INSERT INTO reporte  VALUES ('totalmes','MASTER, TESORERO',1, 2);
INSERT INTO archivo (nombre, nombre_archivo, nombre_jasper, id_tipo_archivo, id_reporte) VALUES ('clasificadosxdia','reportetotal.xls','totalmes.jasper',1,2);
--insert into data (id, nombre, descripcion) values (500,'TIPO_PUBLICACION','tipos de publicación');
insert into parametros_reporte (nombre, etiqueta, id_tipo, id_data, id_reporte, id) values ('fecha_ini', 'DESDE:', 3, null, 2, 3);
insert into parametros_reporte (nombre, etiqueta, id_tipo, id_data, id_reporte, id) values ('fecha_fin', 'HASTA:', 3, null, 2, 4);
--insert into parametros_reporte (nombre, etiqueta, id_tipo, id_data, id_reporte, id) values ('tipo', 'TIPO:', 5, 500, 2, 4);
--**********************************************************************************************************************************************************
