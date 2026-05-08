-- =============================================================
--  DATOS DE INSERCIÓN - SISTEMA DE GESTIÓN DE ENTRENAMIENTO
-- =============================================================

-- -------------------------
-- ROLES
-- -------------------------
INSERT INTO ROLE (name) VALUES ('USER'), ('TRAINER'), ('ADMIN');

-- -------------------------
-- PERMISOS
-- -------------------------
INSERT INTO PERMISSION (name) VALUES
('CREATE_ROUTINE'),
('VIEW_ROUTINE'),
('EDIT_ROUTINE'),
('DELETE_ROUTINE'),
('ASSIGN_ROUTINE'),
('VIEW_ASSIGNED_ROUTINES'),
('CREATE_EXERCISE'),
('VIEW_EXERCISE'),
('EDIT_EXERCISE'),
('DELETE_EXERCISE'),
('REGISTER_PROGRESS'),
('VIEW_OWN_PROGRESS'),
('VIEW_ASSIGNED_USERS_PROGRESS'),
('VIEW_STATISTICS'),
('GENERATE_RECOMMENDATIONS'),
('VIEW_RECOMMENDATIONS'),
('CREATE_PREDEFINED_ROUTINE'),
('VIEW_PREDEFINED_ROUTINE'),
('EDIT_PREDEFINED_ROUTINE'),
('DELETE_PREDEFINED_ROUTINE'),
('CREATE_EVENT'),
('VIEW_EVENT'),
('EDIT_EVENT'),
('DELETE_EVENT'),
('REGISTER_EVENT'),
('VIEW_USERS'),
('MANAGE_USERS'),
('ASSIGN_TRAINER'),
('VIEW_ASSIGNED_USERS'),
('VIEW_HISTORY'),
('DOWNLOAD_REPORT'),
('LOGIN'),
('LOGOUT'),
('ACCESS_ADMIN_PANEL'),
('MANAGE_PERMISSIONS'),
('MANAGE_ROLES');

-- -------------------------
-- ROLE_PERMISSION
-- -------------------------
-- Mapeo de IDs de permisos (en el orden de inserción):
--  1  CREATE_ROUTINE              19 EDIT_PREDEFINED_ROUTINE
--  2  VIEW_ROUTINE                20 DELETE_PREDEFINED_ROUTINE
--  3  EDIT_ROUTINE                21 CREATE_EVENT
--  4  DELETE_ROUTINE              22 VIEW_EVENT
--  5  ASSIGN_ROUTINE              23 EDIT_EVENT
--  6  VIEW_ASSIGNED_ROUTINES      24 DELETE_EVENT
--  7  CREATE_EXERCISE             25 REGISTER_EVENT
--  8  VIEW_EXERCISE               26 VIEW_USERS
--  9  EDIT_EXERCISE               27 MANAGE_USERS
-- 10  DELETE_EXERCISE             28 ASSIGN_TRAINER
-- 11  REGISTER_PROGRESS           29 VIEW_ASSIGNED_USERS
-- 12  VIEW_OWN_PROGRESS           30 VIEW_HISTORY
-- 13  VIEW_ASSIGNED_USERS_PROGRESS 31 DOWNLOAD_REPORT
-- 14  VIEW_STATISTICS             32 LOGIN
-- 15  GENERATE_RECOMMENDATIONS    33 LOGOUT
-- 16  VIEW_RECOMMENDATIONS        34 ACCESS_ADMIN_PANEL
-- 17  CREATE_PREDEFINED_ROUTINE   35 MANAGE_PERMISSIONS
-- 18  VIEW_PREDEFINED_ROUTINE     36 MANAGE_ROLES

-- Roles: 1=USER, 2=TRAINER, 3=ADMIN

-- 👤 USUARIO (roleId = 1)
INSERT INTO ROLE_PERMISSION (roleId, permissionId) VALUES
(1,  1),  -- CREATE_ROUTINE
(1,  2),  -- VIEW_ROUTINE
(1,  3),  -- EDIT_ROUTINE
(1,  6),  -- VIEW_ASSIGNED_ROUTINES
(1,  8),  -- VIEW_EXERCISE
(1, 11),  -- REGISTER_PROGRESS
(1, 12),  -- VIEW_OWN_PROGRESS
(1, 16),  -- VIEW_RECOMMENDATIONS
(1, 18),  -- VIEW_PREDEFINED_ROUTINE
(1, 22),  -- VIEW_EVENT
(1, 25),  -- REGISTER_EVENT
(1, 30),  -- VIEW_HISTORY
(1, 31),  -- DOWNLOAD_REPORT
(1, 32),  -- LOGIN
(1, 33);  -- LOGOUT

-- 🧑‍🏫 ENTRENADOR (roleId = 2) — todo lo del usuario +
INSERT INTO ROLE_PERMISSION (roleId, permissionId) VALUES
(2,  1),  -- CREATE_ROUTINE
(2,  2),  -- VIEW_ROUTINE
(2,  3),  -- EDIT_ROUTINE
(2,  5),  -- ASSIGN_ROUTINE
(2,  6),  -- VIEW_ASSIGNED_ROUTINES
(2,  8),  -- VIEW_EXERCISE
(2, 11),  -- REGISTER_PROGRESS
(2, 12),  -- VIEW_OWN_PROGRESS
(2, 13),  -- VIEW_ASSIGNED_USERS_PROGRESS
(2, 15),  -- GENERATE_RECOMMENDATIONS
(2, 16),  -- VIEW_RECOMMENDATIONS
(2, 17),  -- CREATE_PREDEFINED_ROUTINE
(2, 18),  -- VIEW_PREDEFINED_ROUTINE
(2, 22),  -- VIEW_EVENT
(2, 25),  -- REGISTER_EVENT
(2, 29),  -- VIEW_ASSIGNED_USERS
(2, 30),  -- VIEW_HISTORY
(2, 31),  -- DOWNLOAD_REPORT
(2, 32),  -- LOGIN
(2, 33);  -- LOGOUT

-- 👑 ADMINISTRADOR (roleId = 3) — TODOS los permisos
INSERT INTO ROLE_PERMISSION (roleId, permissionId) VALUES
(3,  1),(3,  2),(3,  3),(3,  4),(3,  5),(3,  6),(3,  7),(3,  8),(3,  9),(3, 10),
(3, 11),(3, 12),(3, 13),(3, 14),(3, 15),(3, 16),(3, 17),(3, 18),(3, 19),(3, 20),
(3, 21),(3, 22),(3, 23),(3, 24),(3, 25),(3, 26),(3, 27),(3, 28),(3, 29),(3, 30),
(3, 31),(3, 32),(3, 33),(3, 34),(3, 35),(3, 36);

-- -------------------------
-- USUARIOS (APP_USER)
-- -------------------------
-- IDs 1-3: ADMIN, 4-7: TRAINERS, 8-20: USERS
INSERT INTO APP_USER (firstName, lastName, email, password_hash, age, weight, height) VALUES
-- Administradores
('Hernando','Ospina Martínez','hernando.ospina@fitcampus.co','$2a$10$lCwNFFxufd4EupbnWiBiDOcLOcNY.ivnQQ4O80LCaB9Hov3w9RikS',40,82.00,1.78),
-- Entrenadores
('Leidy',      'Cardona Restrepo',   'leidy.cardona@fitcampus.co',      '$2a$10$Mm.D7CxaZlGI.IyLE3XUN.xmoPB1KMpddKUwqfAVOnvFaCoOtTWoi', 32, 62.50, 1.65),
('Mauricio',   'Giraldo Patiño',     'mauricio.giraldo@fitcampus.co',   '$2a$10$qsbrl78.TPw2wFNH86f4vuF2SZh9ZhPK1a7DrtBHE4t/aivWANwoW', 35, 78.00, 1.80),
('Tatiana',    'Salazar Ríos',       'tatiana.salazar@fitcampus.co',    '$2a$10$BtAM2urG4xL/UYFcHCMxuOPK2b/iUDgAjkr6w0qtDwFRAtDylwqQa', 28, 57.00, 1.62),
-- Usuarios
('Camilo',     'Vargas Peñaloza',    'camilo.vargas@gmail.com',         '$2a$10$klbaDTvgNIaSRrRuReyOg.jqcTnpS3KkToZcKefaFfDCJeI5VLHky', 22, 75.00, 1.75),
('Valentina',  'Torres Cárdenas',    'valentina.torres@gmail.com',      '$2a$10$JddUKgMIg0ufizA.xg9rR.f0UPwrStkWHir1E1rSIh3KyV9b089JS', 20, 58.00, 1.60),
('Sebastián',  'Morales Agudelo',    'sebastian.morales@gmail.com',     '$2a$10$k8d/aYmRQtWuHDp3uu2lYunu4TRKGCKI7/LK.NupxLMIbw..tIGIa', 25, 80.50, 1.82),
('Daniela',    'Ramírez Herrera',    'daniela.ramirez@gmail.com',       '$2a$10$oO60A00y5KYpJuJtvO.cJOZctD.1kUlTIAgWiGABNpZYyyJPCCX9y', 23, 55.00, 1.58),
('Andrés',     'López Bermúdez',     'andres.lopez@gmail.com',          '$2a$10$2BYEG8hVSrdYyWrRaPlNWeHK47LizuS13IUJxPwJf51P6HE.UPPGK', 27, 88.00, 1.85),
('Juliana',    'Castro Quintero',    'juliana.castro@gmail.com',        '$2a$10$gcSrbmD39ZdATRA.DgVid./PeVxeNKfNckld.ypb8IXNxGxV.K89S', 21, 60.20, 1.63),
('Felipe',     'Gómez Acosta',       'felipe.gomez@gmail.com',          '$2a$10$vBtn9P1.TQ6EFutNCntUbuiGvtD5mO7KTJr9pXVsU/IojIAztjyPm', 29, 92.00, 1.88),
('Mariana',    'Díaz Londoño',       'mariana.diaz@gmail.com',          '$2a$10$BqSh/DphJuKpdynhaiWD7.B6NDg.JbV4JZ/iw0AF7yRo4mTRe4K5O', 24, 63.50, 1.67),
('Esteban',    'Muñoz Villegas',     'esteban.munoz@gmail.com',         '$2a$10$0UeLabOQpIkVYOSrhHPlxefdANJkW0m5HZq/yOUh7anOrAKEyrYkK', 26, 85.00, 1.79),
('Manuela',    'Sánchez Piedrahita', 'manuela.sanchez@gmail.com',       '$2a$10$rvM76tACznxmUuW7TXyEr.aWQ8udJC9uFOjW6hV3rJFnHCLZF20fO', 22, 52.00, 1.55),
('Tomás',      'Ríos Betancur',      'tomas.rios@gmail.com',            '$2a$10$KAFEYLM3q2yFGfGZFljVCOHcGZbl3zgm86xYlVNZxVAJDtiMJDzp2', 30, 77.00, 1.77),
('Vanessa',    'Herrera Aristizábal','vanessa.herrera@gmail.com',       '$2a$10$ovuzr97Pm8TYrxGRlWGHGeNi4smlYl.oQ4KBMJb1lu0FeVMdlLYUK', 19, 54.00, 1.59),
('David',      'Mejía Correa',       'david.mejia@gmail.com',           '$2a$10$mKTKAgdRx1XnRS/6aUEY9ON3OmhrxKTzIRUXxx1/56WcNh5C.p0ai', 28, 70.00, 1.72),
('Alejandra',  'Benjumea Arango',    'alejandra.benjumea@gmail.com',    '$2a$10$l0ymGe13JNeyAffn7IyOYOygcKHoJ7pQou0/PwG0XJjEWW9uryPYe', 23, 61.00, 1.64),
('Nicolás',    'Prado Echeverri',    'nicolas.prado@gmail.com',         '$2a$10$v9KlUV4RLLjpsVdZYaKZAey4lc7v.zTshHNt7Mzsv3crmSVL.j2Wu', 31, 95.00, 1.90),
('Isabela',    'Flórez Zuluaga',     'isabela.florez@gmail.com',        '$2a$10$hdcPtWiOs0ybSX5CQxbrg.9uMXUarcVWlxu4TCjvmljnbn8u622Qa', 20, 57.50, 1.61);

-- -------------------------
-- USER_ROLE
-- -------------------------
INSERT INTO USER_ROLE (userId, roleId) VALUES
(1,  3),  -- Hernando → ADMIN
(2,  2),  -- Leidy → TRAINER
(3,  2),  -- Mauricio → TRAINER
(4,  2),  -- Tatiana → TRAINER
(5,  1),  -- Camilo → USER
(6,  1),  -- Valentina → USER
(7,  1),  -- Sebastián → USER
(8,  1),  -- Daniela → USER
(9,  1),  -- Andrés → USER
(10, 1),  -- Juliana → USER
(11, 1),  -- Felipe → USER
(12, 1),  -- Mariana → USER
(13, 1),  -- Esteban → USER
(14, 1),  -- Manuela → USER
(15, 1),  -- Tomás → USER
(16, 1),  -- Vanessa → USER
(17, 1),  -- David → USER
(18, 1),  -- Alejandra → USER
(19, 1),  -- Nicolás → USER
(20, 1);  -- Isabela → USER

-- -------------------------
-- USER_TRAINER  (trainer → user)
-- -------------------------
INSERT INTO USER_TRAINER (userId, trainerId) VALUES
(5,  2),  -- Camilo     → Leidy
(6,  2),  -- Valentina  → Leidy
(7,  2),  -- Sebastián  → Leidy
(8,  3),  -- Daniela    → Mauricio
(9,  3),  -- Andrés     → Mauricio
(10, 3),  -- Juliana    → Mauricio
(11, 3),  -- Felipe     → Mauricio
(12, 4),  -- Mariana    → Tatiana
(13, 4),  -- Esteban    → Tatiana
(14, 4),  -- Manuela    → Tatiana
(15, 2),  -- Tomás      → Leidy
(16, 4),  -- Vanessa    → Tatiana
(17, 3),  -- David      → Mauricio
(18, 2),  -- Alejandra  → Leidy
(19, 3),  -- Nicolás    → Mauricio
(20, 4);  -- Isabela    → Tatiana

-- -------------------------
-- DIFFICULTY
-- -------------------------
INSERT INTO DIFFICULTY (difficultyName) VALUES
('Principiante'),
('Intermedio'),
('Avanzado'),
('Élite');

-- -------------------------
-- TYPE
-- -------------------------
INSERT INTO TYPE (typeName) VALUES
('Fuerza'),
('Cardio'),
('Flexibilidad'),
('Resistencia'),
('Funcional'),
('HIIT'),
('Rehabilitación'),
('Mixto');

-- -------------------------
-- EXERCISE  (30 ejercicios)
-- -------------------------
INSERT INTO EXERCISE (exerciseName, description) VALUES
-- Pierna / Glúteo
('Sentadilla libre',            'Ejercicio compuesto que trabaja cuádriceps, glúteos e isquiotibiales. Barra sobre trapecios, descenso hasta 90°.'),
('Prensa de piernas',           'Máquina de empuje que aísla cuádriceps y glúteos. Pies a la anchura de caderas.'),
('Zancada alternada',           'Ejercicio unilateral de pierna con paso al frente, rodilla trasera casi toca el suelo.'),
('Hip thrust con barra',        'Empuje de cadera con barra apoyada en banco; activa glúteo mayor de forma máxima.'),
('Extensión de cuádriceps',     'Máquina de extensión; trabaja cuádriceps en aislamiento con control en la fase excéntrica.'),
('Curl femoral acostado',       'Máquina curl; trabaja isquiotibiales en aislamiento. Rodilla flexionada hasta 90°.'),
('Elevación de talones (gemelos)','Ejercicio de pie o en máquina para trabajar gastrocnemio y sóleo.'),
('Sentadilla sumo',             'Variante de sentadilla con piernas abiertas y pies rotados hacia afuera; enfatiza glúteo y aductor.'),
('Step-up con mancuernas',      'Subida al cajón con mancuernas; trabaja cuádriceps, glúteos y equilibrio.'),
('Peso muerto rumano',          'Movimiento de bisagra de cadera con barra o mancuernas; trabaja isquiotibiales y glúteo.'),
-- Pecho
('Press de banca plano',        'Ejercicio rey de pecho con barra; trabaja pectoral mayor, deltoides anterior y tríceps.'),
('Press inclinado con mancuernas','Press en banco a 45°; enfatiza la porción clavicular del pectoral.'),
('Aperturas en cable (cruce)',  'Ejercicio de aislamiento de pectoral en polea alta o baja; contracción máxima al centro.'),
-- Espalda
('Dominadas (pull-up)',         'Jalón en barra con peso corporal; trabaja dorsal ancho, bíceps y romboides.'),
('Remo con barra',              'Ejercicio compuesto de espalda; dorsal ancho, romboides y trapecio medio.'),
('Jalón al pecho en polea',     'Variante de jalón en máquina; ideal para principiantes o trabajo de volumen.'),
-- Hombro
('Press militar con barra',     'Empuje vertical sobre cabeza; trabaja deltoides, trapecio y tríceps.'),
('Elevaciones laterales',       'Aislamiento de deltoides lateral con mancuernas; codos ligeramente flexionados.'),
-- Bíceps
('Curl con barra recta',        'Flexión de codo con barra; trabaja cabeza larga y corta del bíceps braquial.'),
('Curl martillo alternado',     'Agarre neutro con mancuernas; trabaja bíceps braquial y braquiorradial.'),
-- Tríceps
('Extensión de tríceps en polea','Jalón de polea alta con cuerda o barra recta; aislamiento de tríceps.'),
('Fondos en paralelas',         'Ejercicio de peso corporal; trabaja tríceps, pectoral inferior y deltoides.'),
-- Core / Abdomen
('Plancha isométrica',          'Posición de puente ventral; activa core completo, glúteos y hombros de forma isométrica.'),
('Crunch abdominal',            'Flexión parcial de tronco en suelo; trabaja recto abdominal.'),
('Elevación de piernas colgado','Colgado en barra, elevación de piernas hasta 90°; trabaja recto abdominal inferior.'),
-- Cardio / Funcional
('Burpee',                      'Movimiento funcional explosivo de cuerpo completo: sentadilla, plancha, flexión y salto vertical.'),
('Box jump (salto al cajón)',    'Salto pliométrico al cajón; desarrolla potencia en piernas y coordinación.'),
('Cuerda de batalla (battle rope)','Ejercicio de alta intensidad con cuerdas pesadas; trabaja cardio, hombros y core.'),
('Remo en máquina (ergómetro)', 'Cardio de bajo impacto que trabaja espalda, piernas y brazos de forma simultánea.'),
('Kettlebell swing',            'Movimiento de bisagra con pesa rusa; trabaja posterior de la cadena y cardio metabólico.');

-- -------------------------
-- VISUAL_SUPPORT
-- -------------------------
INSERT INTO VISUAL_SUPPORT (supportType, url, exerciseId) VALUES
('video', 'https://cdn.fitcampus.co/videos/sentadilla-libre.mp4',      1),
('foto',  'https://cdn.fitcampus.co/img/sentadilla-libre.jpg',         1),
('video', 'https://cdn.fitcampus.co/videos/prensa-piernas.mp4',        2),
('foto',  'https://cdn.fitcampus.co/img/hip-thrust.jpg',               4),
('video', 'https://cdn.fitcampus.co/videos/hip-thrust.mp4',            4),
('foto',  'https://cdn.fitcampus.co/img/curl-femoral.jpg',             6),
('video', 'https://cdn.fitcampus.co/videos/peso-muerto-rumano.mp4',   10),
('foto',  'https://cdn.fitcampus.co/img/press-banca.jpg',             11),
('video', 'https://cdn.fitcampus.co/videos/press-banca.mp4',          11),
('foto',  'https://cdn.fitcampus.co/img/dominadas.jpg',               14),
('video', 'https://cdn.fitcampus.co/videos/dominadas.mp4',            14),
('video', 'https://cdn.fitcampus.co/videos/press-militar.mp4',        17),
('foto',  'https://cdn.fitcampus.co/img/curl-barra.jpg',              19),
('video', 'https://cdn.fitcampus.co/videos/plancha.mp4',              23),
('foto',  'https://cdn.fitcampus.co/img/burpee.jpg',                  26),
('video', 'https://cdn.fitcampus.co/videos/burpee.mp4',               26),
('video', 'https://cdn.fitcampus.co/videos/box-jump.mp4',             27),
('foto',  'https://cdn.fitcampus.co/img/kettlebell-swing.jpg',        30),
('video', 'https://cdn.fitcampus.co/videos/kettlebell-swing.mp4',     30),
('foto',  'https://cdn.fitcampus.co/img/remo-barra.jpg',              15);

-- -------------------------
-- ROUTINE  (20 rutinas)
-- -------------------------
-- ownerId: entrenadores (2,3,4) o usuarios (5-20)
-- visibility: TRUE=pública, FALSE=privada
INSERT INTO ROUTINE (routineName, visibility, difficultyId, typeId, ownerId) VALUES
-- Rutinas de entrenadores (predefinidas / asignables)
('Rutina de pierna - Hipertrofia',      TRUE,  2, 1, 2),   -- id 1
('Rutina de glúteo - Forma y tono',     TRUE,  2, 1, 2),   -- id 2
('Rutina de brazo completo',            TRUE,  2, 1, 3),   -- id 3
('Rutina femoral e isquiotibial',       TRUE,  3, 1, 3),   -- id 4
('Rutina de bíceps - Volumen',          TRUE,  2, 1, 4),   -- id 5
('Rutina de pecho avanzado',            TRUE,  3, 1, 3),   -- id 6
('Cardio HIIT - Quema de grasa',        TRUE,  3, 6, 2),   -- id 7
('Rutina funcional cuerpo completo',    TRUE,  2, 5, 4),   -- id 8
('Rutina de hombro y trapecio',         TRUE,  2, 1, 3),   -- id 9
('Rutina de core y abdomen',            TRUE,  1, 5, 2),   -- id 10
('Rutina de espalda - Fuerza',          TRUE,  3, 1, 4),   -- id 11
('Rutina de resistencia cardio',        TRUE,  2, 4, 2),   -- id 12
('Rutina de movilidad y flexibilidad',  TRUE,  1, 3, 4),   -- id 13
-- Rutinas propias de usuarios (privadas)
('Mi rutina de pierna - Camilo',        FALSE, 2, 1, 5),   -- id 14
('Mi rutina mixta - Valentina',         FALSE, 1, 8, 6),   -- id 15
('Entrenamiento fuerza - Sebastián',    FALSE, 3, 1, 7),   -- id 16
('Rutina HIIT - Daniela',               FALSE, 2, 6, 8),   -- id 17
('Glúteo y femoral - Juliana',          FALSE, 2, 1, 10),  -- id 18
('Full body - Felipe',                  FALSE, 3, 5, 11),  -- id 19
('Rutina élite potencia - Nicolás',     FALSE, 4, 1, 19);  -- id 20

-- -------------------------
-- ROUTINE_EXERCISE  (≥40 asociaciones)
-- -------------------------
INSERT INTO ROUTINE_EXERCISE (routineId, exerciseId) VALUES
-- Rutina 1: Pierna Hipertrofia
(1,  1), -- Sentadilla libre
(1,  2), -- Prensa de piernas
(1,  3), -- Zancada alternada
(1,  5), -- Extensión de cuádriceps
(1,  7), -- Elevación de talones
-- Rutina 2: Glúteo
(2,  4), -- Hip thrust
(2,  8), -- Sentadilla sumo
(2,  9), -- Step-up
(2, 10), -- Peso muerto rumano
(2,  3), -- Zancada
-- Rutina 3: Brazo completo
(3, 19), -- Curl barra
(3, 20), -- Curl martillo
(3, 21), -- Extensión tríceps
(3, 22), -- Fondos
(3, 18), -- Elevaciones laterales
-- Rutina 4: Femoral
(4,  6), -- Curl femoral
(4, 10), -- Peso muerto rumano
(4,  4), -- Hip thrust
(4,  1), -- Sentadilla
(4,  2), -- Prensa
-- Rutina 5: Bíceps
(5, 19), -- Curl barra
(5, 20), -- Curl martillo
(5, 14), -- Dominadas
(5, 16), -- Jalón al pecho
-- Rutina 6: Pecho avanzado
(6, 11), -- Press banca
(6, 12), -- Press inclinado
(6, 13), -- Aperturas cable
(6, 22), -- Fondos
-- Rutina 7: HIIT
(7, 26), -- Burpee
(7, 27), -- Box jump
(7, 28), -- Battle rope
(7, 30), -- Kettlebell swing
-- Rutina 8: Funcional
(8, 26), -- Burpee
(8, 30), -- Kettlebell swing
(8, 23), -- Plancha
(8,  9), -- Step-up
(8, 27), -- Box jump
-- Rutina 9: Hombro
(9, 17), -- Press militar
(9, 18), -- Elevaciones laterales
(9, 15), -- Remo barra
-- Rutina 10: Core
(10, 23), -- Plancha
(10, 24), -- Crunch
(10, 25), -- Elevación piernas
-- Rutina 11: Espalda
(11, 14), -- Dominadas
(11, 15), -- Remo barra
(11, 16), -- Jalón al pecho
-- Rutina 14: Pierna Camilo
(14,  1),
(14,  2),
(14,  7),
-- Rutina 16: Fuerza Sebastián
(16, 11),
(16, 15),
(16, 17),
-- Rutina 17: HIIT Daniela
(17, 26),
(17, 28),
(17, 23),
-- Rutina 18: Glúteo Juliana
(18,  4),
(18,  8),
(18, 10),
-- Rutina 19: Full body Felipe
(19,  1),
(19, 11),
(19, 14),
(19, 23),
-- Rutina 20: Élite Nicolás
(20,  1),
(20, 11),
(20, 14),
(20, 17),
(20, 26);

-- -------------------------
-- PROGRESS_RECORD
-- -------------------------
-- routineExercise IDs: ver el orden de inserción en ROUTINE_EXERCISE
-- Los primeros IDs: 1=RE(1,1), 2=RE(1,2), 3=RE(1,3)...
INSERT INTO PROGRESS_RECORD (routineExercise, date, time, progressNotes, series, repetitions, weight, equipment_used) VALUES
-- Camilo - Rutina pierna (RE ids 1-5)
(1, '2025-03-01', '07:15:00', 'Buen control en la bajada, sin dolor de rodilla.',         4, 10, 80.00, 'Barra olímpica'),
(2, '2025-03-01', '07:35:00', 'Peso aumentado 5kg respecto a la semana pasada.',          4, 12, 120.00,'Máquina prensa'),
(3, '2025-03-03', '08:00:00', 'Coordinación mejorada; se nota fatiga en el tramo final.', 3, 12, 20.00, 'Mancuernas'),
(4, '2025-03-05', '07:30:00', 'Primer intento con barra, técnica en progreso.',           3, 15, 60.00, 'Barra + banco'),
(5, '2025-03-07', '08:00:00', 'Sesión de activación; gemelos trabajados al fallo.',       4, 20, 50.00, 'Máquina gemelos'),
-- Valentina - Rutina glúteo (RE ids 6-10)
(6,  '2025-03-02', '09:00:00', 'Extensión completa de cadera, sin molestia lumbar.',      4, 15, 40.00, 'Barra olímpica + banco'),
(7,  '2025-03-02', '09:20:00', 'Buen rango de movimiento; pies bien rotados.',            4, 12, 30.00, 'Barra olímpica'),
(8,  '2025-03-04', '09:15:00', 'Cajón a 50 cm; equilibrio estable.',                     3, 12, 10.00, 'Mancuernas'),
(9,  '2025-03-06', '09:00:00', 'Peso muerto con barra, espalda neutra todo el recorrido.',4, 10, 40.00, 'Barra olímpica'),
(10, '2025-03-08', '09:30:00', 'Leve fatiga en el glúteo al final del set.',              3, 12, 20.00, 'Mancuernas'),
-- Sebastián - Brazo completo (RE ids 11-15)
(11, '2025-03-01', '18:00:00', 'Curl concentrado al final por congestión extra.',         4, 10, 20.00, 'Barra olímpica'),
(12, '2025-03-01', '18:15:00', 'Agarre neutro; sin dolor en muñeca.',                    4, 12, 16.00, 'Mancuernas'),
(13, '2025-03-03', '18:10:00', 'Codo fijo; buen aislamiento de tríceps.',                4, 15,  0.00, 'Polea alta + cuerda'),
(14, '2025-03-05', '18:00:00', 'Primer día; solo 8 reps por fatiga acumulada.',           3,  8,  0.00, 'Paralelas'),
(15, '2025-03-07', '18:30:00', 'Hombro sin molestia; peso controlado.',                  3, 15,  8.00, 'Mancuernas'),
-- Andrés - Femoral (RE ids 16-20)
(16, '2025-03-02', '06:30:00', 'Peso aumentado 2.5 kg; isquio aguantó bien.',            4, 12, 55.00, 'Máquina curl femoral'),
(17, '2025-03-04', '06:45:00', 'Barra a la cadera; excelente activación posterior.',     4, 10, 60.00, 'Barra olímpica'),
(18, '2025-03-06', '06:30:00', 'Hip thrust + banda elástica; activación glútea máxima.', 4, 15, 50.00, 'Barra + banda'),
(19, '2025-03-08', '07:00:00', 'Sentadilla pesada; rodilla en línea con pie.',           5, 10, 90.00, 'Barra olímpica'),
(20, '2025-03-10', '06:30:00', 'Prensa a una pierna alternando.',                        4, 12,100.00, 'Máquina prensa'),
-- Felipe - Pecho (RE ids 26-29)
(26, '2025-03-03', '17:00:00', 'Nuevo PR personal: 100 kg en banca.',                   5, 5, 100.00, 'Barra olímpica + banco'),
(27, '2025-03-03', '17:20:00', 'Buen trabajo; hombro sin molestias.',                   4, 10,  28.00, 'Mancuernas'),
(28, '2025-03-05', '17:00:00', 'Cable en polea baja; contracción pectoral excelente.',  3, 15,  15.00, 'Polea'),
(29, '2025-03-07', '17:30:00', 'Fondos lastrados; mejora notable en estabilidad.',      4,  8,  10.00, 'Paralelas + cinturón'),
-- Juliana - Glúteo y femoral
(56, '2025-03-02', '10:00:00', 'Hip thrust con banda; activación perfecta.',             4, 15, 30.00, 'Barra + banda elástica'),
(57, '2025-03-04', '10:20:00', 'Sentadilla sumo; excelente apertura de cadera.',        4, 12, 40.00, 'Barra olímpica'),
(58, '2025-03-06', '10:00:00', 'Peso muerto rumano; espalda recta todo el recorrido.',  4, 10, 35.00, 'Barra olímpica');

-- -------------------------
-- RECOMMENDATIONS
-- -------------------------
INSERT INTO RECOMMENDATION (sender, receiver, message, date) VALUES
(2, 5,  'Camilo, aumenta el peso en sentadilla la próxima semana al menos 5 kg. Tu técnica es sólida.',                                    '2025-03-08'),
(2, 6,  'Valentina, incluye banda de resistencia en el hip thrust para mayor activación glútea.',                                          '2025-03-08'),
(2, 7,  'Sebastián, descansa 72 h entre sesiones de brazo para optimizar la recuperación muscular.',                                       '2025-03-09'),
(3, 8,  'Daniela, en el HIIT reduce el intervalo de descanso de 45 s a 30 s para mayor intensidad cardiovascular.',                       '2025-03-07'),
(3, 9,  'Andrés, es momento de periodizar: 4 semanas de volumen y 2 de intensidad. Te envío plan.',                                       '2025-03-09'),
(3, 10, 'Juliana, añade 2 series de abductores tras la rutina de glúteo para trabajo complementario.',                                    '2025-03-10'),
(3, 11, 'Felipe, tu press de banca ya superó los 100 kg. Es momento de trabajar técnica de pausa para mayor potencia.',                   '2025-03-10'),
(4, 12, 'Mariana, enfócate en la fase excéntrica del curl femoral: 3 segundos bajando para mayor estímulo.',                              '2025-03-07'),
(4, 13, 'Esteban, tu core es el punto débil. Agrega plancha lateral 3×45 s cada día de entrenamiento.',                                  '2025-03-08'),
(4, 14, 'Manuela, tu postura en sentadilla es excelente. Siguiente reto: sentadilla frontal para mayor activación de cuádriceps.',        '2025-03-09'),
(2, 15, 'Tomás, el remo con barra te dará base de espalda sólida. Mantén el pecho orgulloso durante todo el movimiento.',                '2025-03-10'),
(4, 16, 'Vanessa, hidratación clave: mínimo 2 litros durante el entrenamiento de resistencia. Nota diferencia en el rendimiento.',        '2025-03-09'),
(3, 17, 'David, tus elevaciones laterales muestran compensación de trapecio. Reduce 2 kg y concéntrate en el deltoides medio.',           '2025-03-08'),
(2, 18, 'Alejandra, excelente progresión en dominadas. Próxima sesión añade lastre de 5 kg para seguir estimulando.',                    '2025-03-10'),
(3, 19, 'Nicolás, eres el más avanzado del grupo. Vamos a integrar cadenas en sentadilla para sobrecarga acomodada.',                    '2025-03-11');

-- -------------------------
-- PHYSICAL_SPACE
-- -------------------------
INSERT INTO PHYSICAL_SPACE (name, location, capacity) VALUES
('101 I',             'Edificio I, piso 1', 30),
('201 I',             'Edificio I, piso 2', 30),
('202 I',             'Edificio I, piso 2', 30),
('203 I',             'Edificio I, piso 2', 30),
('204 I',             'Edificio I, piso 2', 30),
('205 I',             'Edificio I, piso 2', 30),
('GIMNASIO',          'Bloque deportivo, planta baja', 80),
('CANCHA DE FÚTBOL',  'Zona deportiva exterior', 22),
('COLISEO 1',         'Bloque deportivo, ala norte', 200),
('COLISEO 2',         'Bloque deportivo, ala sur',  200),
('PISCINA',           'Zona acuática, exterior',    50),
('101 G',             'Edificio G, piso 1', 25),
('102 G',             'Edificio G, piso 1', 25),
('103 G',             'Edificio G, piso 1', 25),
('104 G',             'Edificio G, piso 1', 25),
('105 G',             'Edificio G, piso 1', 25),
('106 G',             'Edificio G, piso 2', 25),
('107 G',             'Edificio G, piso 2', 25),
('108 G',             'Edificio G, piso 2', 25),
('109 G',             'Edificio G, piso 2', 25);

-- -------------------------
-- EVENT
-- -------------------------
INSERT INTO EVENT (name, date, physicalSpace, description) VALUES
('Triatlón universitario',           '2025-04-12', 11, 'Competencia de natación, ciclismo y carrera. Abierto a estudiantes y egresados.'),
('Torneo interclases de fútbol',     '2025-04-20', 8,  'Campeonato interno entre programas académicos. Grupos de 11 jugadores.'),
('Maratón 5K Campus',                '2025-05-03', 8,  'Carrera popular de 5 kilómetros dentro del campus universitario.'),
('Olimpiadas internas - Atletismo',  '2025-05-17', 9,  'Pruebas de velocidad, salto y lanzamiento en el Coliseo 1.'),
('Festival de natación',             '2025-05-24', 11, 'Competencia de estilos: libre, espalda, pecho y mariposa.'),
('Campeonato de CrossFit',           '2025-06-07', 7,  'WOD especial con movimientos funcionales y levantamiento olímpico.'),
('Clínica de flexibilidad y yoga',   '2025-06-14', 1,  'Taller de 3 horas sobre movilidad articular y técnica de respiración.'),
('Seminario de nutrición deportiva', '2025-06-21', 12, 'Charla con nutricionista sobre macros, suplementación y periodización nutricional.'),
('Torneo de voleibol mixto',         '2025-07-05', 10, 'Campeonato de voleibol en el Coliseo 2. Equipos de 6 jugadores mixtos.'),
('Reto de plancha - Core Challenge', '2025-07-12', 7,  'Competencia de plancha isométrica; el que más aguante gana.'),
('Taller de entrenamiento funcional','2025-07-19', 7,  'Workshop de 4 horas sobre kettlebell, TRX y movimientos balísticos.'),
('Liga de baloncesto campus',        '2025-08-02', 9,  'Torneo de baloncesto 3x3 y 5x5 en el Coliseo 1.'),
('Jornada de wellness y meditación', '2025-08-09', 13, 'Actividades de bienestar: meditación guiada, respiración y estiramientos.'),
('Desafío de fuerza máxima',         '2025-08-23', 7,  'Competencia de sentadilla, press de banca y peso muerto. Clasificaciones por peso.'),
('Carrera nocturna universitaria',   '2025-09-06', 8,  'Carrera 10K nocturna por los alrededores del campus. Salida a las 7:00 PM.');

-- -------------------------
-- SUBSCRIPTION
-- -------------------------
INSERT INTO SUBSCRIPTION (userId, eventId, attendance) VALUES
(5,  1, TRUE),   -- Camilo → Triatlón
(6,  1, FALSE),  -- Valentina → Triatlón
(7,  2, TRUE),   -- Sebastián → Fútbol
(8,  3, TRUE),   -- Daniela → Maratón 5K
(9,  4, TRUE),   -- Andrés → Atletismo
(10, 5, TRUE),   -- Juliana → Natación
(11, 6, TRUE),   -- Felipe → CrossFit
(12, 7, TRUE),   -- Mariana → Yoga
(13, 6, FALSE),  -- Esteban → CrossFit
(14, 7, TRUE),   -- Manuela → Yoga
(15, 2, TRUE),   -- Tomás → Fútbol
(16, 7, TRUE),   -- Vanessa → Yoga
(17, 4, TRUE),   -- David → Atletismo
(18, 5, TRUE),   -- Alejandra → Natación
(19, 14,TRUE),   -- Nicolás → Desafío fuerza
(20, 7, FALSE),  -- Isabela → Yoga
(5,  6, TRUE),   -- Camilo → CrossFit
(9,  14,TRUE),   -- Andrés → Desafío fuerza
(11, 14,TRUE),   -- Felipe → Desafío fuerza
(7,  15,TRUE);   -- Sebastián → Carrera nocturna
