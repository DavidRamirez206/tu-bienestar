-- HU9.1 (GMB-63): desactivar entrenadores sin borrarlos.
-- Un usuario inactivo no puede iniciar sesion, pero conserva sus asignaciones e historial.
--
-- No hace falta correr este archivo a mano: al arrancar, la aplicacion crea la columna si falta
-- (ver UsersActiveColumnInitializer). Queda aqui como registro del cambio de esquema.
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT TRUE;
