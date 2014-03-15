CREATE TABLE registro_pagos (
	id bigserial NOT NULL,
	tipo_operacion varchar(255),
	subtipo_operacion varchar(255),
	no_planilla varchar(255),
	fecha date,
	rp varchar(255),
	cuenta_origen varchar(255),
	consecutivo int4,
	nombre varchar(255),
	num_id varchar(255),
	monto numeric(10, 2),
	monto_abonado numeric(10, 2),
	no_cuenta varchar(255),
	estado varchar(255),
	observacion varchar(255)
);
ALTER TABLE registro_pagos ADD CONSTRAINT registro_pagos_pk PRIMARY KEY(id);
CREATE UNIQUE INDEX registro_pagos_ui1 ON registro_pagos (no_planilla,consecutivo,fecha);
