package co.icesi.exercise.config;

import co.icesi.exercise.model.AppUser;
import co.icesi.exercise.model.Permission;
import co.icesi.exercise.model.Role;
import co.icesi.exercise.repositories.AppUserRepository;
import co.icesi.exercise.repositories.PermissionRepository;
import co.icesi.exercise.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired private RoleRepository roleRepository;
    @Autowired private PermissionRepository permissionRepository;
    @Autowired private AppUserRepository appUserRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (roleRepository.count() > 0) return;

        // Permissions
        Permission viewUsers        = perm("VIEW_USERS");
        Permission manageUsers      = perm("MANAGE_USERS");
        Permission manageRoles      = perm("MANAGE_ROLES");
        Permission managePerms      = perm("MANAGE_PERMISSIONS");
        Permission assignTrainer    = perm("ASSIGN_TRAINER");

        // Roles
        Role userRole    = role("USER",    List.of());
        Role trainerRole = role("TRAINER", List.of(viewUsers));
        Role adminRole   = role("ADMIN",   List.of(viewUsers, manageUsers, manageRoles, managePerms, assignTrainer));

        // Admin user
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
}
