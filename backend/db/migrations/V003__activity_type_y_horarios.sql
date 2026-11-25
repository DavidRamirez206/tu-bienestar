-- HU8.2: tipo de evento (CLASE, TORNEO o GIMNASIO) y horarios de prueba.
-- Se puede ejecutar mas de una vez sin duplicar datos.
-- La columna type la crea la aplicacion al arrancar si falta (ver MissingColumnsInitializer);
-- los horarios de prueba de abajo si hay que cargarlos a mano.
ALTER TABLE activities ADD COLUMN IF NOT EXISTS type varchar(20);

UPDATE activities SET type = 'GIMNASIO' WHERE type IS NULL AND name IN ('Bodybuilding 101', 'Semana del bienestar');
UPDATE activities SET type = 'TORNEO'   WHERE type IS NULL AND name = 'Torneo de fuerza';
UPDATE activities SET type = 'CLASE'    WHERE type IS NULL;

INSERT INTO schedules (id_activity, day_of_week, start_time, end_time)
SELECT a.id_activity, v.day_of_week, v.start_time::time, v.end_time::time
  FROM (VALUES
    ('Semana del bienestar', 'MONDAY',    '06:00', '21:00'),
    ('Semana del bienestar', 'TUESDAY',   '06:00', '21:00'),
    ('Semana del bienestar', 'WEDNESDAY', '06:00', '21:00'),
    ('Semana del bienestar', 'THURSDAY',  '06:00', '21:00'),
    ('Semana del bienestar', 'FRIDAY',    '06:00', '19:00'),
    ('Yoga al amanecer',     'TUESDAY',   '06:30', '07:30'),
    ('Yoga al amanecer',     'THURSDAY',  '06:30', '07:30'),
    ('Taller de movilidad',  'WEDNESDAY', '12:00', '13:00'),
    ('Torneo de fuerza',     'SATURDAY',  '09:00', '13:00')
  ) AS v(activity_name, day_of_week, start_time, end_time)
  JOIN activities a ON a.name = v.activity_name
 WHERE NOT EXISTS (
       SELECT 1 FROM schedules s
        WHERE s.id_activity = a.id_activity
          AND s.day_of_week = v.day_of_week
          AND s.start_time = v.start_time::time);
