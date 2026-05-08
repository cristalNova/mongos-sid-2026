package co.icesi.exercise.services;

import co.icesi.exercise.model.AppUser;
import co.icesi.exercise.model.Role;
import co.icesi.exercise.repositories.AppUserRepository;
import co.icesi.exercise.repositories.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void createAppUser_ShouldPersistUserInDatabase() {
        Role role = new Role();
        role.setName("USER");
        role.setPermissions(new ArrayList<>());
        role = roleRepository.save(role);

        AppUser user = new AppUser();
        user.setFirstName("Samuel");
        user.setLastName("Navia");
        user.setEmail("samuel.integration@test.com");
        user.setPasswordHash("hash123");
        user.setAge(22);
        user.setWeight(70.5);
        user.setHeight(1.75);
        user.setRoles(new ArrayList<>());
        user.setTrainers(new ArrayList<>());

        AppUser saved = userService.createAppUser(user, List.of(role.getId()));

        assertTrue(saved.getId() > 0);

        Optional<AppUser> persisted = appUserRepository.findById(saved.getId());
        assertTrue(persisted.isPresent());
        assertEquals("samuel.integration@test.com", persisted.get().getEmail());
        assertEquals(1, persisted.get().getRoles().size());
        assertEquals("USER", persisted.get().getRoles().get(0).getName());
    }

    @Test
    void assignTrainerToUser_ShouldPersistTrainerRelationInDatabase() {
        Role role = new Role();
        role.setName("USER");
        role.setPermissions(new ArrayList<>());
        role = roleRepository.save(role);

        AppUser user = new AppUser();
        user.setFirstName("User");
        user.setLastName("Test");
        user.setEmail("user@test.com");
        user.setPasswordHash("hashUser");
        user.setAge(20);
        user.setWeight(65.0);
        user.setHeight(1.70);
        user.setRoles(new ArrayList<>());
        user.setTrainers(new ArrayList<>());

        AppUser trainer = new AppUser();
        trainer.setFirstName("Trainer");
        trainer.setLastName("Test");
        trainer.setEmail("trainer@test.com");
        trainer.setPasswordHash("hashTrainer");
        trainer.setAge(30);
        trainer.setWeight(75.0);
        trainer.setHeight(1.80);
        trainer.setRoles(new ArrayList<>());
        trainer.setTrainers(new ArrayList<>());

        AppUser savedUser = userService.createAppUser(user, List.of(role.getId()));
        AppUser savedTrainer = userService.createAppUser(trainer, List.of(role.getId()));

        userService.assignTrainerToUser(savedUser.getId(), savedTrainer.getId());

        AppUser persisted = appUserRepository.findById(savedUser.getId()).orElseThrow();

        assertEquals(1, persisted.getTrainers().size());
        assertEquals(savedTrainer.getId(), persisted.getTrainers().get(0).getId());
    }
}