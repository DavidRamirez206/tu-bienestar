-- HU7: nivel de las rutinas prediseñadas. Se puede ejecutar mas de una vez.
ALTER TABLE routines ADD COLUMN IF NOT EXISTS level varchar(20);
