-- Datos de referencia para las pruebas automatizadas.
-- Hibernate ejecuta este archivo al crear el esquema de H2 (ddl-auto=create-drop).
-- Los roles son datos de referencia que la aplicacion necesita para registrar usuarios.
INSERT INTO roles (name) VALUES ('TRAINEE');
INSERT INTO roles (name) VALUES ('TRAINER');
INSERT INTO roles (name) VALUES ('ADMIN');
