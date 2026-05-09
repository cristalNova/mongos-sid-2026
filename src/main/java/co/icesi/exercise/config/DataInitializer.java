package co.icesi.exercise.config;

import co.icesi.exercise.model.AppUser;
import co.icesi.exercise.model.Permission;
import co.icesi.exercise.model.Role;
import co.icesi.exercise.model.nosql.*;
import co.icesi.exercise.repositories.AppUserRepository;
import co.icesi.exercise.repositories.PermissionRepository;
import co.icesi.exercise.repositories.RoleRepository;
import co.icesi.exercise.repositories.nosql.EventMongoRepository;
import co.icesi.exercise.repositories.nosql.ExerciseMongoRepository;
import co.icesi.exercise.repositories.nosql.RoutineMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired private RoleRepository roleRepository;
    @Autowired private PermissionRepository permissionRepository;
    @Autowired private AppUserRepository appUserRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired private ExerciseMongoRepository exerciseMongoRepository;
    @Autowired private RoutineMongoRepository routineMongoRepository;
    @Autowired private EventMongoRepository eventMongoRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedSql();
        seedMongo();
    }

    // ── SQL (PostgreSQL) ──────────────────────────────────────────────────────

    private void seedSql() {
        if (roleRepository.count() > 0) return;

        Permission viewUsers     = perm("VIEW_USERS");
        Permission manageUsers   = perm("MANAGE_USERS");
        Permission manageRoles   = perm("MANAGE_ROLES");
        Permission managePerms   = perm("MANAGE_PERMISSIONS");
        Permission assignTrainer = perm("ASSIGN_TRAINER");

        role("USER",    List.of());
        role("TRAINER", List.of(viewUsers));
        Role adminRole = role("ADMIN", List.of(viewUsers, manageUsers, manageRoles, managePerms, assignTrainer));

        AppUser admin = new AppUser();
        admin.setFirstName("Admin");
        admin.setLastName("FitCampus");
        admin.setEmail("admin@fitcampus.co");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setAge(30);
        admin.setWeight(70.0);
        admin.setHeight(1.75);
        admin.setRoles(List.of(adminRole));
        admin.setTrainers(new ArrayList<>());
        appUserRepository.save(admin);
    }

    // ── MongoDB ───────────────────────────────────────────────────────────────

    private void seedMongo() {
        if (exerciseMongoRepository.count() > 0) return;

        // Ejercicios
        ExerciseDocument sentadilla = exercise(
                "Sentadilla", "Ejercicio compuesto para piernas y glúteos", "fuerza", "MEDIO", 30.0,
                List.of(vs("video", "https://www.youtube.com/watch?v=U3HlEF_E9fo")));

        ExerciseDocument plancha = exercise(
                "Plancha", "Isométrico de core y estabilidad", "fuerza", "FÁCIL", 20.0,
                List.of(vs("video", "https://www.youtube.com/watch?v=pSHjTRCQxIw")));

        ExerciseDocument burpee = exercise(
                "Burpee", "Ejercicio funcional de cuerpo completo", "cardio", "DIFÍCIL", 25.0,
                List.of(vs("video", "https://www.youtube.com/watch?v=dZgVxmf6jkA")));

        ExerciseDocument trote = exercise(
                "Trote en cinta", "Cardio aeróbico de baja intensidad", "cardio", "FÁCIL", 40.0,
                List.of());

        ExerciseDocument flexion = exercise(
                "Flexión de pecho", "Empuje de pecho, hombros y tríceps", "fuerza", "MEDIO", 20.0,
                List.of(vs("video", "https://www.youtube.com/watch?v=IODxDxX7oi4")));

        // Rutina pública de ejemplo
        RoutineDocument rutina = new RoutineDocument();
        rutina.setRoutineName("Full Body Principiante");
        rutina.setVisibility(true);
        rutina.setType("mixto");
        rutina.setDifficultyType("FÁCIL");
        rutina.setOwnerId(1);
        rutina.setOwnerFirstName("Admin");
        rutina.setOwnerLastName("FitCampus");
        rutina.setCreatedAt(new Date());
        rutina.setUpdatedAt(new Date());

        List<RoutineExerciseDocument> ejercicios = new ArrayList<>();
        ejercicios.add(reDoc(sentadilla));
        ejercicios.add(reDoc(plancha));
        ejercicios.add(reDoc(flexion));
        rutina.setExercises(ejercicios);
        routineMongoRepository.save(rutina);

        // Evento de ejemplo
        EventDocument evento = new EventDocument();
        evento.setName("Clase de yoga matutina");
        evento.setDescription("Sesión de yoga para todos los niveles. Trae tu tapete.");
        evento.setDate(new Date());

        PhysicalSpaceDocument espacio = new PhysicalSpaceDocument();
        espacio.setName("Gimnasio Principal");
        espacio.setLocation("Edificio A - Piso 1");
        espacio.setCapacity(20);
        evento.setPhysicalSpace(espacio);
        evento.setSubscriptions(new ArrayList<>());
        eventMongoRepository.save(evento);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

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

    private RoutineExerciseDocument reDoc(ExerciseDocument ex) {
        RoutineExerciseDocument re = new RoutineExerciseDocument();
        re.setExerciseId(ex.getId());
        re.setExerciseName(ex.getExerciseName());
        re.setType(ex.getType());
        re.setDifficultyType(ex.getDifficultyType());
        re.setDuration(ex.getDuration());
        return re;
    }
}
