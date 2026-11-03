-- Datos para probar HU3, HU8.1 y HU19: videos reales, filtros con varios valores y eventos futuros.
-- Se puede ejecutar mas de una vez sin duplicar datos.
UPDATE exercises SET type = 'Fuerza', difficulty = 'Baja', description = 'Flexiones de pecho apoyando manos y puntas de los pies.',
       video_url = 'https://www.youtube.com/watch?v=IODxDxX7oi4'
 WHERE name = 'Pushups' AND video_url = 'http://video.com/pushups';
UPDATE exercises SET type = 'Fuerza', difficulty = 'Baja', description = 'Sentadillas con el peso del cuerpo.',
       video_url = 'https://www.youtube.com/watch?v=aclHkVaku9U'
 WHERE name = 'Squats' AND video_url = 'http://video.com/squats';

INSERT INTO exercises (name, type, description, duration_min, difficulty, video_url, id_user, predefined)
SELECT v.name, v.type, v.description, v.duration_min, v.difficulty, v.video_url, u.id_user, true
  FROM (VALUES
    ('Burpees', 'Cardio', 'Salto, plancha y flexion en un solo movimiento.', 10, 'Alta', 'https://www.youtube.com/watch?v=dZgVxmf6jkA'),
    ('Jumping jacks', 'Cardio', 'Saltos abriendo brazos y piernas.', 5, 'Baja', 'https://www.youtube.com/watch?v=c4DAnQ6DtF8'),
    ('HIIT sin equipo', 'Cardio', 'Intervalos de alta intensidad con calentamiento.', 30, 'Media', 'https://www.youtube.com/watch?v=ml6cT4AZdqI'),
    ('Plancha', 'Fuerza', 'Sostener el cuerpo recto apoyado en antebrazos.', 3, 'Media', 'https://www.youtube.com/watch?v=pSHjTRCQxIw'),
    ('Zancadas', 'Fuerza', 'Paso largo bajando la cadera.', 8, 'Media', 'https://www.youtube.com/watch?v=QOVaHwm-Q6U'),
    ('Yoga para principiantes', 'Movilidad', 'Sesion suave de yoga en casa.', 20, 'Baja', 'https://www.youtube.com/watch?v=v7AYKMP6rOE'),
    ('Estiramiento de cuerpo completo', 'Movilidad', 'Estiramientos para soltar tension.', 20, 'Baja', 'https://www.youtube.com/watch?v=sTANio_2E0Q')
  ) AS v(name, type, description, duration_min, difficulty, video_url)
  JOIN users u ON u.institutional_email = 'admin@icesi.edu.co'
 WHERE NOT EXISTS (SELECT 1 FROM exercises e WHERE e.name = v.name);

UPDATE spaces SET name = 'Gimnasio principal', location = 'Edificio deportivo, primer piso' WHERE name = 'Gym Area 1';
UPDATE spaces SET name = 'Salón de yoga', location = 'Edificio deportivo, segundo piso' WHERE name = 'Yoga Studio';

INSERT INTO activities (id_space, name, description, start_date, end_date)
SELECT s.id_space, v.name, v.description, current_date + v.start_in, current_date + v.end_in
  FROM (VALUES
    ('Gimnasio principal', 'Torneo de fuerza', 'Competencia amistosa por categorías.', 7, 7),
    ('Salón de yoga', 'Yoga al amanecer', 'Sesión abierta para todos los niveles.', 3, 3),
    ('Gimnasio principal', 'Semana del bienestar', 'Charlas y clases gratuitas durante la semana.', 14, 18),
    ('Salón de yoga', 'Taller de movilidad', 'Rutinas de movilidad para quienes pasan mucho tiempo sentados.', 30, 30)
  ) AS v(space_name, name, description, start_in, end_in)
  JOIN spaces s ON s.name = v.space_name
 WHERE NOT EXISTS (SELECT 1 FROM activities a WHERE a.name = v.name);
