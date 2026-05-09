package co.icesi.exercise.config;

import co.icesi.exercise.model.AppUser;
import co.icesi.exercise.model.Permission;
import co.icesi.exercise.model.Recommendation;
import co.icesi.exercise.model.Role;
import co.icesi.exercise.model.nosql.*;
import co.icesi.exercise.repositories.AppUserRepository;
import co.icesi.exercise.repositories.PermissionRepository;
import co.icesi.exercise.repositories.RecommendationRepository;
import co.icesi.exercise.repositories.RoleRepository;
import co.icesi.exercise.repositories.nosql.EventMongoRepository;
import co.icesi.exercise.repositories.nosql.ExerciseMongoRepository;
import co.icesi.exercise.repositories.nosql.ProgressRecordMongoRepository;
import co.icesi.exercise.repositories.nosql.RoutineMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired private RoleRepository roleRepository;
    @Autowired private PermissionRepository permissionRepository;
    @Autowired private AppUserRepository appUserRepository;
    @Autowired private RecommendationRepository recommendationRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired private ExerciseMongoRepository exerciseMongoRepository;
    @Autowired private RoutineMongoRepository routineMongoRepository;
    @Autowired private ProgressRecordMongoRepository progressRecordMongoRepository;
    @Autowired private EventMongoRepository eventMongoRepository;

    // References shared between seedSql() and seedMongo()
    private AppUser adminUser;
    private AppUser trainer1, trainer2, trainer3;
    private AppUser user1, user2, user3, user4, user5, user6, user7, user8;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedSql();
        seedMongo();
    }

    // ── PostgreSQL ────────────────────────────────────────────────────────────

    private void seedSql() {
        // Re-seed if the full test dataset is not present (fewer than 10 users expected)
        if (appUserRepository.count() >= 10) return;
        // Clean existing partial data to avoid duplicates
        if (roleRepository.count() > 0) {
            recommendationRepository.deleteAll();
            appUserRepository.deleteAll();
            permissionRepository.deleteAll();
            roleRepository.deleteAll();
        }

        // Permisos
        Permission viewUsers     = perm("VIEW_USERS");
        Permission manageUsers   = perm("MANAGE_USERS");
        Permission manageRoles   = perm("MANAGE_ROLES");
        Permission managePerms   = perm("MANAGE_PERMISSIONS");
        Permission assignTrainer = perm("ASSIGN_TRAINER");

        // Roles
        Role userRole    = role("USER",    List.of());
        Role trainerRole = role("TRAINER", List.of(viewUsers));
        Role adminRole   = role("ADMIN",   List.of(viewUsers, manageUsers, manageRoles, managePerms, assignTrainer));

        // Admin
        adminUser = user("Admin",     "FitCampus",   "admin@fitcampus.co",            "admin123", 30, 70.0, 1.75, List.of(adminRole));

        // Entrenadores
        trainer1 = user("Leidy",    "Cardona Restrepo",  "leidy.cardona@fitcampus.co",    "trainer1", 28, 62.0, 1.65, List.of(trainerRole));
        trainer2 = user("Mauricio", "Giraldo Patiño",    "mauricio.giraldo@fitcampus.co", "trainer2", 34, 80.0, 1.80, List.of(trainerRole));
        trainer3 = user("Tatiana",  "Salazar Ríos",      "tatiana.salazar@fitcampus.co",  "trainer3", 31, 58.0, 1.62, List.of(trainerRole));

        // Usuarios regulares
        user1 = user("Camilo",     "Vargas Peñaloza",    "camilo.vargas@gmail.com",      "user1",  22, 75.0, 1.78, List.of(userRole));
        user2 = user("Valentina",  "Torres Cárdenas",    "valentina.torres@gmail.com",   "user2",  20, 58.0, 1.63, List.of(userRole));
        user3 = user("Sebastián",  "Morales Agudelo",    "sebastian.morales@gmail.com",  "user3",  23, 82.0, 1.82, List.of(userRole));
        user4 = user("Daniela",    "Ramírez Herrera",    "daniela.ramirez@gmail.com",    "user4",  21, 55.0, 1.60, List.of(userRole));
        user5 = user("Andrés",     "López Bermúdez",     "andres.lopez@gmail.com",       "user5",  24, 90.0, 1.85, List.of(userRole));
        user6 = user("Juliana",    "Castro Quintero",    "juliana.castro@gmail.com",     "user6",  19, 52.0, 1.58, List.of(userRole));
        user7 = user("Felipe",     "Gómez Acosta",       "felipe.gomez@gmail.com",       "user7",  25, 78.0, 1.76, List.of(userRole));
        user8 = user("Mariana",    "Díaz Londoño",       "mariana.diaz@gmail.com",       "user8",  22, 60.0, 1.67, List.of(userRole));

        // Asignaciones de entrenador → estudiantes (USER_TRAINER)
        // Trainer 1 — Leidy: camilo, valentina, sebastián
        assignTrainer(user1, trainer1);
        assignTrainer(user2, trainer1);
        assignTrainer(user3, trainer1);
        // Trainer 2 — Mauricio: daniela, andrés, juliana
        assignTrainer(user4, trainer2);
        assignTrainer(user5, trainer2);
        assignTrainer(user6, trainer2);
        // Trainer 3 — Tatiana: felipe, mariana
        assignTrainer(user7, trainer3);
        assignTrainer(user8, trainer3);

        // Recomendaciones (RECOMMENDATION)
        recommendation(trainer1, user1, "Camilo, aumenta el peso en sentadilla gradualmente. Tu técnica mejoró mucho esta semana.", daysAgo(5));
        recommendation(trainer1, user2, "Valentina, recuerda mantener la espalda recta en el peso muerto. Intenta con menos peso y más series.", daysAgo(3));
        recommendation(trainer1, user3, "Sebastián, excelente progreso en cardio. Te recomiendo agregar sesiones de movilidad los viernes.", daysAgo(7));
        recommendation(trainer2, user4, "Daniela, tus repeticiones son sólidas. Es momento de introducir peso libre en press de banca.", daysAgo(4));
        recommendation(trainer2, user5, "Andrés, reduce la intensidad del HIIT a 3 veces por semana para evitar sobreentrenamiento.", daysAgo(2));
        recommendation(trainer3, user7, "Felipe, continúa con el programa de fuerza superior. Considera yoga para mejorar la movilidad.", daysAgo(6));
        recommendation(trainer3, user8, "Mariana, tu constancia es admirable. Para la próxima semana agrega trabajo de core.", daysAgo(1));
    }

    // ── MongoDB ───────────────────────────────────────────────────────────────

    private void seedMongo() {
        // Re-seed if the full exercise catalog is not present (15 exercises expected)
        if (exerciseMongoRepository.count() >= 15) return;

        // ── Ejercicios (15) ──────────────────────────────────────────────────

        // Cardio
        ExerciseDocument trote = exercise("Trote en cinta",
                "Cardio aeróbico de baja intensidad, ideal para calentamiento y resistencia.", "cardio", "FÁCIL", 40.0,
                List.of(vs("video", "https://www.youtube.com/watch?v=_kGESn8ArrU")));

        ExerciseDocument bicicleta = exercise("Bicicleta estática",
                "Cardio de bajo impacto articular. Mantén cadencia constante entre 70–90 rpm.", "cardio", "FÁCIL", 35.0,
                List.of());

        ExerciseDocument cuerda = exercise("Saltar cuerda",
                "Ejercicio cardiovascular de alta coordinación. 1 minuto equivale a 10 min de trote.", "cardio", "MEDIO", 20.0,
                List.of(vs("video", "https://www.youtube.com/watch?v=FJmRQ5iTXKE")));

        ExerciseDocument hiit = exercise("HIIT 20/10",
                "Entrenamiento de intervalos de alta intensidad: 20 seg trabajo / 10 seg descanso, 8 rondas.", "cardio", "DIFÍCIL", 25.0,
                List.of(vs("video", "https://www.youtube.com/watch?v=ml6cT4AZdqI")));

        ExerciseDocument natacion = exercise("Natación estilo libre",
                "Cardio de cuerpo completo con bajo impacto. Ideal para recuperación activa.", "cardio", "MEDIO", 45.0,
                List.of());

        // Fuerza
        ExerciseDocument sentadilla = exercise("Sentadilla",
                "Ejercicio compuesto para piernas y glúteos. Baja hasta 90° manteniendo rodillas sobre pies.", "fuerza", "MEDIO", 30.0,
                List.of(vs("video", "https://www.youtube.com/watch?v=U3HlEF_E9fo")));

        ExerciseDocument plancha = exercise("Plancha isométrica",
                "Isométrico de core y estabilidad. Mantén cadera nivelada y abdomen contraído.", "fuerza", "FÁCIL", 20.0,
                List.of(vs("video", "https://www.youtube.com/watch?v=pSHjTRCQxIw")));

        ExerciseDocument burpee = exercise("Burpee",
                "Ejercicio funcional de cuerpo completo: sentadilla + plancha + salto. Alta demanda cardiovascular.", "fuerza", "DIFÍCIL", 25.0,
                List.of(vs("video", "https://www.youtube.com/watch?v=dZgVxmf6jkA")));

        ExerciseDocument flexion = exercise("Flexión de pecho",
                "Empuje de pecho, hombros y tríceps. Codos a 45° del torso, baja hasta rozar el suelo.", "fuerza", "MEDIO", 20.0,
                List.of(vs("video", "https://www.youtube.com/watch?v=IODxDxX7oi4")));

        ExerciseDocument pressBanca = exercise("Press de banca",
                "Ejercicio de pecho con barra. Baja controlado hasta el esternón, extiende sin bloquear codos.", "fuerza", "MEDIO", 30.0,
                List.of(vs("video", "https://www.youtube.com/watch?v=rT7DgCr-3pg")));

        ExerciseDocument pesomuerTo = exercise("Peso muerto",
                "Ejercicio de cadena posterior (glúteos, isquiotibiales, espalda baja). Espalda recta en todo momento.", "fuerza", "DIFÍCIL", 35.0,
                List.of(vs("video", "https://www.youtube.com/watch?v=op9kVnSso6Q")));

        ExerciseDocument dominadas = exercise("Dominadas",
                "Jalón con peso corporal. Inicia desde extensión completa, sube hasta barbilla sobre barra.", "fuerza", "DIFÍCIL", 25.0,
                List.of());

        ExerciseDocument curlBiceps = exercise("Curl de bíceps con mancuernas",
                "Aislamiento de bíceps. Mantén codos pegados al torso durante todo el movimiento.", "fuerza", "FÁCIL", 20.0,
                List.of());

        // Movilidad
        ExerciseDocument estiramiento = exercise("Estiramiento global",
                "Secuencia de estiramientos para cadena posterior, cuádriceps, hombros y espalda. 30 seg por posición.", "movilidad", "FÁCIL", 25.0,
                List.of());

        ExerciseDocument yogaFlow = exercise("Yoga flow básico",
                "Secuencia de posturas enlazadas: perro boca abajo, cobra, guerrero I y II, árbol.", "movilidad", "FÁCIL", 40.0,
                List.of(vs("video", "https://www.youtube.com/watch?v=v7AYKMP6rOE")));

        // ── Rutinas (6) ──────────────────────────────────────────────────────

        // Rutina pública del trainer 1 (Leidy)
        RoutineDocument fullBodyPrincipiante = routine(
                "Full Body Principiante", true, "mixto", "FÁCIL",
                trainer1, List.of(sentadilla, plancha, flexion, trote));

        // Rutina pública del trainer 1 (Leidy)
        routine("Movilidad y Recuperación", true, "movilidad", "FÁCIL",
                trainer1, List.of(estiramiento, yogaFlow, plancha));

        // Rutina pública del trainer 2 (Mauricio)
        RoutineDocument cardioIntensivo = routine(
                "Cardio Intensivo", true, "cardio", "DIFÍCIL",
                trainer2, List.of(trote, cuerda, hiit, burpee));

        // Rutina pública del trainer 3 (Tatiana)
        RoutineDocument fuerzaSuperior = routine(
                "Fuerza Superior", true, "fuerza", "MEDIO",
                trainer3, List.of(pressBanca, dominadas, flexion, curlBiceps));

        // Rutina privada de usuario (Camilo)
        RoutineDocument rutinaPersonalCamilo = routine(
                "Mi rutina de piernas", false, "fuerza", "MEDIO",
                user1, List.of(sentadilla, pesomuerTo));

        // Rutina privada de usuario (Valentina) — adoptada de trainer
        routine("Full Body adaptado", false, "mixto", "FÁCIL",
                user2, List.of(sentadilla, plancha, flexion, estiramiento));

        // ── Registros de progreso (18) ───────────────────────────────────────
        // Camilo — 6 registros en las últimas 6 semanas
        progress(user1, fullBodyPrincipiante, "Sentadilla",       daysAgo(42), "08:00", 4, 12, 60.0,  "Buena técnica, sin dolor");
        progress(user1, fullBodyPrincipiante, "Plancha isométrica", daysAgo(35), "08:00", 3, 1,  0.0,  "Aguanté 45 seg por serie");
        progress(user1, rutinaPersonalCamilo, "Sentadilla",       daysAgo(28), "07:30", 4, 15, 65.0,  "Aumenté peso");
        progress(user1, rutinaPersonalCamilo, "Peso muerto",      daysAgo(21), "07:30", 3, 10, 80.0,  "Espalda recta, bien");
        progress(user1, fullBodyPrincipiante, "Sentadilla",       daysAgo(14), "08:00", 5, 12, 70.0,  "Nuevo PR");
        progress(user1, rutinaPersonalCamilo, "Sentadilla",       daysAgo(7),  "08:00", 5, 15, 70.0,  "Muy bien, técnica sólida");

        // Valentina — 4 registros
        progress(user2, fullBodyPrincipiante, "Flexión de pecho", daysAgo(30), "09:00", 3, 8,  0.0,  "Primera sesión, bien");
        progress(user2, fullBodyPrincipiante, "Trote en cinta",   daysAgo(23), "09:00", 1, 1,  0.0,  "30 min continuos");
        progress(user2, fullBodyPrincipiante, "Plancha isométrica", daysAgo(15), "09:00", 4, 1,  0.0,  "50 seg cada serie");
        progress(user2, fullBodyPrincipiante, "Flexión de pecho", daysAgo(8),  "09:00", 4, 10, 0.0,  "Mejora notable en reps");

        // Sebastián — 4 registros
        progress(user3, cardioIntensivo, "HIIT 20/10",       daysAgo(25), "07:00", 8, 1,  0.0,  "Muy intenso, terminé bien");
        progress(user3, cardioIntensivo, "Saltar cuerda",    daysAgo(18), "07:00", 5, 1,  0.0,  "5 min continuos");
        progress(user3, cardioIntensivo, "Burpee",           daysAgo(11), "07:00", 4, 15, 0.0,  "Pace constante");
        progress(user3, cardioIntensivo, "Trote en cinta",   daysAgo(4),  "07:00", 1, 1,  0.0,  "40 min, fc 140 bpm");

        // Daniela — 2 registros
        progress(user4, fuerzaSuperior, "Press de banca",   daysAgo(20), "10:00", 3, 8,  30.0, "Primera vez con barra");
        progress(user4, fuerzaSuperior, "Curl de bíceps con mancuernas", daysAgo(10), "10:00", 3, 12, 8.0, "Rango completo");

        // Andrés — 2 registros
        progress(user5, cardioIntensivo, "HIIT 20/10",      daysAgo(12), "06:30", 8, 1,  0.0,  "Superé las 8 rondas");
        progress(user5, fuerzaSuperior,  "Dominadas",       daysAgo(5),  "06:30", 4, 6,  0.0,  "Con banda elástica");

        // ── Eventos (4) ──────────────────────────────────────────────────────

        EventDocument yoga = event("Clase de Yoga Matutina",
                "Sesión de yoga para todos los niveles. Trae tu tapete y ropa cómoda.",
                daysFromNow(5), "Gimnasio Principal", "Bloque A - Piso 1", 25);
        subscribe(yoga, user1, false);
        subscribe(yoga, user2, false);
        subscribe(yoga, user6, false);
        eventMongoRepository.save(yoga);

        EventDocument crossfit = event("Taller de CrossFit",
                "Introducción al CrossFit: movimientos básicos, respiración y seguridad.",
                daysFromNow(10), "Zona Funcional", "Bloque C - Piso 2", 20);
        subscribe(crossfit, user3, false);
        subscribe(crossfit, user5, false);
        eventMongoRepository.save(crossfit);

        EventDocument torneo = event("Torneo de Natación Intercampus",
                "Competencia amistosa entre estudiantes. Categorías: libre y espalda.",
                daysFromNow(20), "Piscina Semiolímpica", "Edificio Deportivo", 50);
        subscribe(torneo, user7, false);
        subscribe(torneo, user8, false);
        eventMongoRepository.save(torneo);

        EventDocument movilidadEvent = event("Sesión de Movilidad y Estiramientos",
                "Aprende rutinas de movilidad articular para prevenir lesiones. Cupo limitado.",
                daysFromNow(3), "Salón Bienestar", "Bloque B - Piso 3", 15);
        subscribe(movilidadEvent, user2, false);
        subscribe(movilidadEvent, user4, false);
        subscribe(movilidadEvent, user6, false);
        subscribe(movilidadEvent, user8, false);
        eventMongoRepository.save(movilidadEvent);
    }

    // ── Helpers SQL ───────────────────────────────────────────────────────────

    private Permission perm(String name) {
        Permission p = new Permission();
        p.setName(name);
        return permissionRepository.save(p);
    }

    private Role role(String name, List<Permission> permissions) {
        Role r = new Role();
        r.setName(name);
        r.setPermissions(new ArrayList<>(permissions));
        return roleRepository.save(r);
    }

    private AppUser user(String firstName, String lastName, String email, String password,
                         int age, double weight, double height, List<Role> roles) {
        AppUser u = new AppUser();
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(password));
        u.setAge(age);
        u.setWeight(weight);
        u.setHeight(height);
        u.setRoles(new ArrayList<>(roles));
        u.setTrainers(new ArrayList<>());
        return appUserRepository.save(u);
    }

    private void assignTrainer(AppUser student, AppUser trainer) {
        student.getTrainers().add(trainer);
        appUserRepository.save(student);
    }

    private void recommendation(AppUser sender, AppUser receiver, String message, Date date) {
        Recommendation rec = new Recommendation();
        rec.setSender(sender);
        rec.setReceiver(receiver);
        rec.setMessage(message);
        rec.setDate(new java.sql.Date(date.getTime()));
        recommendationRepository.save(rec);
    }

    // ── Helpers MongoDB ───────────────────────────────────────────────────────

    private ExerciseDocument exercise(String name, String desc, String type,
                                      String difficulty, Double duration,
                                      List<VisualSupportDocument> supports) {
        ExerciseDocument e = new ExerciseDocument();
        e.setExerciseName(name);
        e.setDescription(desc);
        e.setType(type);
        e.setDifficultyType(difficulty);
        e.setDuration(duration);
        e.setVisualSupports(new ArrayList<>(supports));
        return exerciseMongoRepository.save(e);
    }

    private VisualSupportDocument vs(String type, String url) {
        VisualSupportDocument v = new VisualSupportDocument();
        v.setSupportType(type);
        v.setUrl(url);
        return v;
    }

    private RoutineDocument routine(String name, boolean visible, String type,
                                    String difficulty, AppUser owner,
                                    List<ExerciseDocument> exercises) {
        RoutineDocument r = new RoutineDocument();
        r.setRoutineName(name);
        r.setVisibility(visible);
        r.setType(type);
        r.setDifficultyType(difficulty);
        r.setOwnerId(owner.getId());
        r.setOwnerFirstName(owner.getFirstName());
        r.setOwnerLastName(owner.getLastName());
        r.setCreatedAt(new Date());
        r.setUpdatedAt(new Date());
        List<RoutineExerciseDocument> list = new ArrayList<>();
        for (ExerciseDocument ex : exercises) list.add(reDoc(ex));
        r.setExercises(list);
        return routineMongoRepository.save(r);
    }

    private RoutineExerciseDocument reDoc(ExerciseDocument ex) {
        RoutineExerciseDocument re = new RoutineExerciseDocument();
        re.setExerciseId(ex.getId());
        re.setExerciseName(ex.getExerciseName());
        re.setType(ex.getType());
        re.setDifficultyType(ex.getDifficultyType());
        re.setDuration(ex.getDuration());
        return re;
    }

    private void progress(AppUser user, RoutineDocument routine, String exerciseName,
                          Date date, String time, int series, int reps, double weight, String notes) {
        ProgressRecordDocument p = new ProgressRecordDocument();
        p.setUserId(user.getId());
        p.setUserFirstName(user.getFirstName());
        p.setUserLastName(user.getLastName());
        p.setRoutineId(routine.getId());
        p.setRoutineName(routine.getRoutineName());
        p.setExerciseName(exerciseName);
        p.setDate(date);
        p.setTime(time);
        p.setSeries(series);
        p.setRepetitions(reps);
        p.setWeight(weight > 0 ? weight : null);
        p.setProgressNotes(notes);
        progressRecordMongoRepository.save(p);
    }

    private EventDocument event(String name, String description, Date date,
                                String spaceName, String location, int capacity) {
        EventDocument e = new EventDocument();
        e.setName(name);
        e.setDescription(description);
        e.setDate(date);
        PhysicalSpaceDocument space = new PhysicalSpaceDocument();
        space.setName(spaceName);
        space.setLocation(location);
        space.setCapacity(capacity);
        e.setPhysicalSpace(space);
        e.setSubscriptions(new ArrayList<>());
        return e; // caller saves after adding subscriptions
    }

    private void subscribe(EventDocument event, AppUser user, boolean attendance) {
        SubscriptionDocument sub = new SubscriptionDocument();
        sub.setUserId(user.getId());
        sub.setUserFirstName(user.getFirstName());
        sub.setUserLastName(user.getLastName());
        sub.setAttendance(attendance);
        event.getSubscriptions().add(sub);
    }

    // ── Utilidades de fecha ───────────────────────────────────────────────────

    private Date daysAgo(int days) {
        return Date.from(LocalDate.now().minusDays(days).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date daysFromNow(int days) {
        return Date.from(LocalDate.now().plusDays(days).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
