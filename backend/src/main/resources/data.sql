-- Users
INSERT INTO users (first_name, last_name, institutional_email, password, age) VALUES
--pass: pass123
('Juan',    'Perez',  'juan.perez@icesi.edu.co',  '$2b$10$xQh1x62WkCI94Hb772kR0eS7PSr3OsUHGekW.q/rUOl0EqEcIUpZC', 20),
--pass: pass456
('Maria',   'Lopez',  'maria.lopez@icesi.edu.co', '$2b$10$rS2CsP8TT7PmXK6le/FdReAUWZ0NLLQ0UJ2/owv8ncq44KWYThJry', 22),
--pass: trainerpass
('Trainer', 'One',    'trainer1@icesi.edu.co',    '$2b$10$v9COem6/GqyL6onYf8X4ZOBzBpMnkCZKojF8Qh9x0CeyiE/TwasPW', 30),
--pass: adminpass
('admin',   'admin',  'admin@icesi.edu.co',       '$2b$10$oQDNDmVu6O3qSO5W15BYR.fERbCH2O7ACTQZjk1u/CvTYTxjbOLA2', 30);

-- Roles
INSERT INTO roles (name) VALUES
('TRAINEE'),
('TRAINER'),
('ADMIN');

-- User Roles
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1),
(2, 1),
(3, 2),
(4, 3);



INSERT INTO policies (name, description, resource, action) VALUES
-- Users
('LIST_USER',             'Listar todos los usuarios',                'USERS',          'READ'),
('CREATE_USER',           'Crear nuevos usuarios',                    'USERS',          'CREATE'),
('EDIT_USER',             'Editar usuarios existentes',               'USERS',          'UPDATE'),
('DELETE_USER',           'Eliminar usuarios',                        'USERS',          'DELETE'),
('VIEW_OWN_PROFILE',      'Ver perfil propio',                        'USERS',          'READ'),
('EDIT_OWN_PROFILE',      'Editar perfil propio',                     'USERS',          'UPDATE'),
-- Roles
('LIST_ROLE',             'Listar todos los roles',                   'ROLES',          'READ'),
('CREATE_ROLE',           'Crear nuevos roles',                       'ROLES',          'CREATE'),
('EDIT_ROLE',             'Editar roles existentes',                  'ROLES',          'UPDATE'),
('DELETE_ROLE',           'Eliminar roles',                           'ROLES',          'DELETE'),
-- Policies
('LIST_POLICY',           'Listar todas las policies',                'POLICIES',       'READ'),
('CREATE_POLICY',         'Crear nuevas policies',                    'POLICIES',       'CREATE'),
('EDIT_POLICY',           'Editar policies existentes',               'POLICIES',       'UPDATE'),
('DELETE_POLICY',         'Eliminar policies',                        'POLICIES',       'DELETE'),
-- Routines
('LIST_ROUTINE',          'Listar todas las rutinas',                 'ROUTINES',       'READ'),
('CREATE_ROUTINE',        'Crear nuevas rutinas',                     'ROUTINES',       'CREATE'),
('EDIT_ROUTINE',          'Editar rutinas existentes',                'ROUTINES',       'UPDATE'),
('DELETE_ROUTINE',        'Eliminar rutinas',                         'ROUTINES',       'DELETE'),
-- Exercises
('LIST_EXERCISE',         'Listar todos los ejercicios',              'EXERCISES',      'READ'),
('CREATE_EXERCISE',       'Crear nuevos ejercicios',                  'EXERCISES',      'CREATE'),
('EDIT_EXERCISE',         'Editar ejercicios existentes',             'EXERCISES',      'UPDATE'),
('DELETE_EXERCISE',       'Eliminar ejercicios',                      'EXERCISES',      'DELETE'),
-- Schedules
('LIST_SCHEDULE',         'Listar todos los horarios',                'SCHEDULES',      'READ'),
('CREATE_SCHEDULE',       'Crear nuevos horarios',                    'SCHEDULES',      'CREATE'),
('EDIT_SCHEDULE',         'Editar horarios existentes',               'SCHEDULES',      'UPDATE'),
('DELETE_SCHEDULE',       'Eliminar horarios',                        'SCHEDULES',      'DELETE'),
-- Progress
('LIST_PROGRESS',         'Listar todos los registros de progreso',   'PROGRESS',       'READ'),
('CREATE_PROGRESS',       'Crear registros de progreso',              'PROGRESS',       'CREATE'),
('EDIT_PROGRESS',         'Editar registros de progreso',             'PROGRESS',       'UPDATE'),
('DELETE_PROGRESS',       'Eliminar registros de progreso',           'PROGRESS',       'DELETE'),
-- Spaces
('LIST_SPACE',            'Listar todos los espacios',                'SPACES',         'READ'),
('CREATE_SPACE',          'Crear nuevos espacios',                    'SPACES',         'CREATE'),
('EDIT_SPACE',            'Editar espacios existentes',               'SPACES',         'UPDATE'),
('DELETE_SPACE',          'Eliminar espacios',                        'SPACES',         'DELETE'),
-- Activities
('LIST_ACTIVITY',         'Listar todas las actividades',             'ACTIVITIES',     'READ'),
('CREATE_ACTIVITY',       'Crear nuevas actividades',                 'ACTIVITIES',     'CREATE'),
('EDIT_ACTIVITY',         'Editar actividades existentes',            'ACTIVITIES',     'UPDATE'),
('DELETE_ACTIVITY',       'Eliminar actividades',                     'ACTIVITIES',     'DELETE'),
-- Assignments
('LIST_ASSIGNMENT',       'Listar todas las asignaciones',            'ASSIGNMENTS',    'READ'),
('CREATE_ASSIGNMENT',     'Crear nuevas asignaciones',                'ASSIGNMENTS',    'CREATE'),
('DELETE_ASSIGNMENT',     'Eliminar asignaciones',                    'ASSIGNMENTS',    'DELETE'),
-- Enrollments
('LIST_ENROLLMENT',       'Listar todas las inscripciones',           'ENROLLMENTS',    'READ'),
('CREATE_ENROLLMENT',     'Crear nuevas inscripciones',               'ENROLLMENTS',    'CREATE'),
('DELETE_ENROLLMENT',     'Eliminar inscripciones',                   'ENROLLMENTS',    'DELETE'),
-- Recommendations
('LIST_RECOMMENDATION',   'Listar todas las recomendaciones',         'RECOMMENDATIONS','READ'),
('CREATE_RECOMMENDATION', 'Crear nuevas recomendaciones',             'RECOMMENDATIONS','CREATE'),
('EDIT_RECOMMENDATION',   'Editar recomendaciones existentes',        'RECOMMENDATIONS','UPDATE'),
('DELETE_RECOMMENDATION', 'Eliminar recomendaciones',                 'RECOMMENDATIONS','DELETE'),
-- Notifications
('LIST_NOTIFICATION',     'Listar todas las notificaciones',          'NOTIFICATIONS',  'READ'),
('CREATE_NOTIFICATION',   'Crear nuevas notificaciones',              'NOTIFICATIONS',  'CREATE'),
('EDIT_NOTIFICATION',     'Editar notificaciones existentes',         'NOTIFICATIONS',  'UPDATE'),
('DELETE_NOTIFICATION',   'Eliminar notificaciones',                  'NOTIFICATIONS',  'DELETE'),
-- Messages
('LIST_MESSAGE',          'Listar todos los mensajes',                'MESSAGES',       'READ'),
('CREATE_MESSAGE',        'Enviar mensajes',                          'MESSAGES',       'CREATE'),
('EDIT_MESSAGE',          'Editar mensajes',                          'MESSAGES',       'UPDATE'),
('DELETE_MESSAGE',        'Eliminar mensajes',                        'MESSAGES',       'DELETE'),

--user-roles
('LIST_USER_ROLE',            'Listar todas las asignaciones usuario-rol',         'USER_ROLES',         'READ'),
('CREATE_USER_ROLE',          'Crear nuevas asignaciones usuario-rol',             'USER_ROLES',         'CREATE'),
('EDIT_USER_ROLE',            'Editar asignaciones usuario-rol existentes',        'USER_ROLES',         'UPDATE'),
('DELETE_USER_ROLE',          'Eliminar asignaciones usuario-rol',                 'USER_ROLES',         'DELETE'),

-- Role Policies
('LIST_ROLE_POLICY',          'Listar todas las asignaciones rol-policy',          'ROLE_POLICIES',      'READ'),
('CREATE_ROLE_POLICY',        'Crear nuevas asignaciones rol-policy',              'ROLE_POLICIES',      'CREATE'),
('EDIT_ROLE_POLICY',          'Editar asignaciones rol-policy existentes',         'ROLE_POLICIES',      'UPDATE'),
('DELETE_ROLE_POLICY',        'Eliminar asignaciones rol-policy',                  'ROLE_POLICIES',      'DELETE'),

-- Routine Exercises
('LIST_ROUTINE_EXERCISE',     'Listar todas las asignaciones rutina-ejercicio',    'ROUTINE_EXERCISES',  'READ'),
('CREATE_ROUTINE_EXERCISE',   'Crear nuevas asignaciones rutina-ejercicio',        'ROUTINE_EXERCISES',  'CREATE'),
('EDIT_ROUTINE_EXERCISE',     'Editar asignaciones rutina-ejercicio existentes',   'ROUTINE_EXERCISES',  'UPDATE'),
('DELETE_ROUTINE_EXERCISE',   'Eliminar asignaciones rutina-ejercicio',            'ROUTINE_EXERCISES',  'DELETE');

-- ADMIN: todos los permisos
INSERT INTO role_policies (role_id, policy_id)
SELECT r.id, p.id
FROM roles r, policies p
WHERE r.name = 'ADMIN'
  AND p.name IN (
         'LIST_USER',            'CREATE_USER',           'EDIT_USER',             'DELETE_USER',
         'VIEW_OWN_PROFILE',     'EDIT_OWN_PROFILE',
         'LIST_ROLE',            'CREATE_ROLE',           'EDIT_ROLE',             'DELETE_ROLE',
         'LIST_POLICY',          'CREATE_POLICY',         'EDIT_POLICY',           'DELETE_POLICY',
         'LIST_ROUTINE',         'CREATE_ROUTINE',        'EDIT_ROUTINE',          'DELETE_ROUTINE',
         'LIST_EXERCISE',        'CREATE_EXERCISE',       'EDIT_EXERCISE',         'DELETE_EXERCISE',
         'LIST_SCHEDULE',        'CREATE_SCHEDULE',       'EDIT_SCHEDULE',         'DELETE_SCHEDULE',
         'LIST_PROGRESS',        'CREATE_PROGRESS',       'EDIT_PROGRESS',         'DELETE_PROGRESS',
         'LIST_SPACE',           'CREATE_SPACE',          'EDIT_SPACE',            'DELETE_SPACE',
         'LIST_ACTIVITY',        'CREATE_ACTIVITY',       'EDIT_ACTIVITY',         'DELETE_ACTIVITY',
         'LIST_ASSIGNMENT',      'CREATE_ASSIGNMENT',     'DELETE_ASSIGNMENT',
         'LIST_ENROLLMENT',      'CREATE_ENROLLMENT',     'DELETE_ENROLLMENT',
         'LIST_RECOMMENDATION',  'CREATE_RECOMMENDATION', 'EDIT_RECOMMENDATION',   'DELETE_RECOMMENDATION',
         'LIST_NOTIFICATION',    'CREATE_NOTIFICATION',   'EDIT_NOTIFICATION',     'DELETE_NOTIFICATION',
         'LIST_MESSAGE',         'CREATE_MESSAGE',        'EDIT_MESSAGE',          'DELETE_MESSAGE',
         'LIST_USER_ROLE',        'CREATE_USER_ROLE',      'EDIT_USER_ROLE',        'DELETE_USER_ROLE',
         'LIST_ROLE_POLICY',      'CREATE_ROLE_POLICY',    'EDIT_ROLE_POLICY',      'DELETE_ROLE_POLICY',
         'LIST_ROUTINE_EXERCISE', 'CREATE_ROUTINE_EXERCISE','EDIT_ROUTINE_EXERCISE', 'DELETE_ROUTINE_EXERCISE'
    );

-- TRAINEE
INSERT INTO role_policies (role_id, policy_id)
SELECT r.id, p.id FROM roles r, policies p
WHERE r.name = 'TRAINEE'
  AND p.name IN (
     'VIEW_OWN_PROFILE',      'EDIT_OWN_PROFILE',
     'LIST_ENROLLMENT',       'CREATE_ENROLLMENT',     'DELETE_ENROLLMENT',
     'CREATE_PROGRESS',       'EDIT_PROGRESS',
     'LIST_ACTIVITY',         'LIST_SCHEDULE',
     'LIST_SPACE',
    -- Rutinas (Todos)
     'LIST_ROUTINE',          'CREATE_ROUTINE',        'EDIT_ROUTINE',          'DELETE_ROUTINE',
    -- Ejercicios (Solo lectura)
     'LIST_EXERCISE',
    -- Rutina_Ejercicios (Todos)
     'LIST_ROUTINE_EXERCISE', 'CREATE_ROUTINE_EXERCISE','EDIT_ROUTINE_EXERCISE', 'DELETE_ROUTINE_EXERCISE',
    -- Recomendaciones (Solo lectura)
     'LIST_RECOMMENDATION',
    -- Notificaciones
     'LIST_NOTIFICATION',     'DELETE_NOTIFICATION',
    -- Users
      'LIST_USER'
    );

-- TRAINER
INSERT INTO role_policies (role_id, policy_id)
SELECT r.id, p.id FROM roles r, policies p
WHERE r.name = 'TRAINER'
  AND p.name IN (
     'VIEW_OWN_PROFILE',      'EDIT_OWN_PROFILE',
     'LIST_USER',             'LIST_ROLE',             'LIST_POLICY',
     'LIST_ACTIVITY',
     'LIST_SCHEDULE',
     'LIST_SPACE',
     'LIST_PROGRESS',
     'LIST_ASSIGNMENT',       'CREATE_ASSIGNMENT',
     'LIST_ENROLLMENT',       'CREATE_ENROLLMENT',     'DELETE_ENROLLMENT',
     'LIST_NOTIFICATION',     'DELETE_NOTIFICATION',
     'LIST_MESSAGE',          'CREATE_MESSAGE',
-- Rutinas (Todos)
     'LIST_ROUTINE',          'CREATE_ROUTINE',        'EDIT_ROUTINE',          'DELETE_ROUTINE',
-- Ejercicios (Solo lectura)
     'LIST_EXERCISE',
-- Rutina_Ejercicios (Todos)
     'LIST_ROUTINE_EXERCISE', 'CREATE_ROUTINE_EXERCISE','EDIT_ROUTINE_EXERCISE', 'DELETE_ROUTINE_EXERCISE',
-- Recomendaciones (Todos)
     'LIST_RECOMMENDATION',   'CREATE_RECOMMENDATION', 'EDIT_RECOMMENDATION',   'DELETE_RECOMMENDATION',
    -- Users
     'LIST_USER'
    );

-- Spaces
INSERT INTO spaces ( name, capacity, location) VALUES
( 'Gym Area 1', 50, 'First Floor'),
( 'Yoga Studio', 20, 'Second Floor');

-- Activities
INSERT INTO activities ( id_space, name, description, start_date, end_date) VALUES
( 1, 'Bodybuilding 101', 'Intro to weights', '2023-01-01', '2023-12-31'),
( 2, 'Morning Yoga', 'Relaxing yoga session', '2023-01-01', '2023-12-31');

-- Exercises
INSERT INTO exercises (name, type, description, duration_min, difficulty, video_url, id_user, predefined) VALUES
( 'Pushups', 'Strength', 'Standard pushups', 5, 'Beginner', 'http://video.com/pushups', 3, true),
('Squats', 'Strength', 'Standard squats', 5, 'Beginner', 'http://video.com/squats', 3, true);

-- Routines
INSERT INTO routines (id_user, name, description, creation_date, predesigned) VALUES
(1, 'Juan Morning Routine', 'Light morning exercises', '2023-10-01', false);

-- Routine Exercises (Assuming Routine ID 1)
INSERT INTO routine_exercises (id_routine, id_exercise, sets, target_reps, exercise_order) VALUES
(1, 1, 3, 15, 1),
(1, 2, 3, 20, 2);

-- Assignments
INSERT INTO assignments (id_user, id_trainer, assignment_date) VALUES
(1, 3, '2023-10-05');

-- Enrollments
INSERT INTO enrollments (id_user, id_activity, enrollment_date) VALUES
(1, 1, '2023-10-02');

-- Messages
INSERT INTO messages (sender_id, receiver_id, content, sent_at) VALUES
(1, 3, 'Hello trainer, I finished my routine.', CURRENT_TIMESTAMP);

-- Notifications
INSERT INTO notifications (id_user_target, id_user_source, type, message, reference_id, reference_type, date_sent, is_read) VALUES
(3, 1, 'MESSAGE', 'You have a new message from Juan', 1, 'MESSAGE', CURRENT_TIMESTAMP, false);

-- Progress
INSERT INTO progress (id_user, id_exercise, id_routine, date_logged, reps, duration_min, effort_level, set_number, weight_kg) VALUES
(1, 1, 1, CURRENT_TIMESTAMP, 15, 5, 3, 1, 0);

-- Recommendations
INSERT INTO recommendations (id_trainer, id_user, description, date_created) VALUES
(3, 1, 'Focus on cardiovascular exercises and light weights for the first month.', '2023-10-05');

-- Schedules
INSERT INTO schedules (id_activity, day_of_week, start_time, end_time) VALUES
(1, 'MONDAY', '08:00:00', '10:00:00'),
(2, 'WEDNESDAY', '07:00:00', '08:30:00');
