package co.icesi.exercise.services;

import co.icesi.exercise.model.AppUser;
import co.icesi.exercise.model.Role;
import co.icesi.exercise.repositories.AppUserRepository;
import co.icesi.exercise.repositories.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    private AppUser user;
    private Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        role = new Role();
        role.setId(1);
        role.setName("USER");

        user = new AppUser();
        user.setId(1);
        user.setEmail("test@mail.com");
        user.setRoles(new ArrayList<>());
        user.setTrainers(new ArrayList<>());
    }

    @Test
    void getAllAppUsers_ShouldReturnList() {
        when(appUserRepository.findAll()).thenReturn(List.of(user));

        List<AppUser> result = userService.getAllAppUsers();

        assertEquals(1, result.size());
        verify(appUserRepository).findAll();
    }

    @Test
    void getAppUserById_ShouldReturnUser() {
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));

        AppUser result = userService.getAppUserById(1);

        assertEquals("test@mail.com", result.getEmail());
    }

    @Test
    void getAppUserById_ShouldThrow() {
        when(appUserRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.getAppUserById(1);
        });
    }

    @Test
    void getUsersByRoleId_ShouldReturnList() {
        when(appUserRepository.findByRolesId(1)).thenReturn(List.of(user));

        List<AppUser> result = userService.getUsersByRoleId(1);

        assertEquals(1, result.size());
    }

    @Test
    void getUsersByTrainerId_ShouldReturnList() {
        when(appUserRepository.findByTrainersId(1)).thenReturn(List.of(user));

        List<AppUser> result = userService.getUsersByTrainerId(1);

        assertEquals(1, result.size());
    }

    @Test
    void createAppUser_ShouldSave() {
        when(appUserRepository.findByEmail("test@mail.com")).thenReturn(Optional.empty());
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(appUserRepository.save(any(AppUser.class))).thenReturn(user);

        AppUser result = userService.createAppUser(user, List.of(1));

        assertEquals(1, result.getRoles().size());
        verify(appUserRepository).save(user);
    }

    @Test
    void createAppUser_ShouldThrowIfEmailExists() {
        when(appUserRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> {
            userService.createAppUser(user, List.of(1));
        });
    }

    @Test
    void createAppUser_ShouldThrowIfRoleNotFound() {
        when(appUserRepository.findByEmail("test@mail.com")).thenReturn(Optional.empty());
        when(roleRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.createAppUser(user, List.of(1));
        });
    }

    @Test
    void updateAppUser_ShouldUpdate() {
        AppUser updated = new AppUser();
        updated.setEmail("new@mail.com");
        updated.setFirstName("Juan");

        user.setRoles(new ArrayList<>(List.of(role)));

        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(appUserRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser result = userService.updateAppUser(1, updated, List.of(1));

        assertEquals("new@mail.com", result.getEmail());
        assertEquals("Juan", result.getFirstName());
    }

    @Test
    void updateAppUser_ShouldThrowIfEmailExists() {
        AppUser other = new AppUser();
        other.setId(2);

        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(appUserRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(other));

        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateAppUser(1, user, null);
        });
    }

    @Test
    void assignRoleToUser_ShouldAddRole() {
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(appUserRepository.save(any(AppUser.class))).thenReturn(user);

        AppUser result = userService.assignRoleToUser(1, 1);

        assertEquals(1, result.getRoles().size());
    }

    @Test
    void assignRoleToUser_ShouldThrowIfRoleNotFound() {
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(roleRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.assignRoleToUser(1, 1);
        });
    }



    @Test
    void removeRoleFromUser_ShouldRemove_WhenUserHasMoreThanOneRole() {
        Role anotherRole = new Role();
        anotherRole.setId(2);
        anotherRole.setName("TRAINER");

        user.getRoles().add(role);
        user.getRoles().add(anotherRole);

        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(appUserRepository.save(any(AppUser.class))).thenReturn(user);

        AppUser result = userService.removeRoleFromUser(1, 1);

        assertEquals(1, result.getRoles().size());
        assertEquals(2, result.getRoles().get(0).getId());
    }

    @Test
    void assignTrainerToUser_ShouldThrowIfSameUser() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.assignTrainerToUser(1, 1);
        });
    }

    @Test
    void assignTrainerToUser_ShouldAssign() {
        AppUser trainer = new AppUser();
        trainer.setId(2);

        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(appUserRepository.findById(2)).thenReturn(Optional.of(trainer));
        when(appUserRepository.save(any(AppUser.class))).thenReturn(user);

        AppUser result = userService.assignTrainerToUser(1, 2);

        assertEquals(1, result.getTrainers().size());
    }

    @Test
    void deleteAppUser_ShouldDelete() {
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));

        userService.deleteAppUserById(1);

        verify(appUserRepository).delete(user);
    }

    @Test
    void deleteAppUser_ShouldThrow() {
        when(appUserRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.deleteAppUserById(1);
        });
    }

    @Test
    void assignRoleToUser_ShouldNotDuplicateRole() {
        user.getRoles().add(role);

        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(appUserRepository.save(any())).thenReturn(user);

        AppUser result = userService.assignRoleToUser(1, 1);

        assertEquals(1, result.getRoles().size());
    }

    @Test
    void removeRoleFromUser_ShouldKeepRoles_WhenRoleToRemoveDoesNotExist() {
        Role existingRole = new Role();
        existingRole.setId(1);
        existingRole.setName("USER");

        Role anotherRole = new Role();
        anotherRole.setId(2);
        anotherRole.setName("TRAINER");

        user.getRoles().clear();
        user.getRoles().add(existingRole);
        user.getRoles().add(anotherRole);

        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser result = userService.removeRoleFromUser(1, 999);

        assertNotNull(result);
        assertEquals(2, result.getRoles().size());
        assertTrue(result.getRoles().stream().anyMatch(r -> r.getId() == 1));
        assertTrue(result.getRoles().stream().anyMatch(r -> r.getId() == 2));

        verify(appUserRepository).findById(1);
        verify(appUserRepository).save(user);
    }

    @Test
    void removeRoleFromUser_ShouldThrowException_WhenRemovingLastRole() {
        Role existingRole = new Role();
        existingRole.setId(1);
        existingRole.setName("USER");

        user.getRoles().clear();
        user.getRoles().add(existingRole);

        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> userService.removeRoleFromUser(1, 1)
        );

        assertEquals("Usuario tiene que tener al menos un rol asignado", exception.getMessage());

        verify(appUserRepository).findById(1);
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void assignTrainerToUser_ShouldNotDuplicateTrainer() {
        AppUser trainer = new AppUser();
        trainer.setId(2);

        user.getTrainers().add(trainer);

        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(appUserRepository.findById(2)).thenReturn(Optional.of(trainer));
        when(appUserRepository.save(any())).thenReturn(user);

        AppUser result = userService.assignTrainerToUser(1, 2);

        assertEquals(1, result.getTrainers().size());
    }


    @Test
    void updateAppUser_WithRoles_ShouldUpdateRoles() {
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(appUserRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(appUserRepository.save(any())).thenReturn(user);

        AppUser updated = new AppUser();
        updated.setEmail("new@mail.com");

        AppUser result = userService.updateAppUser(1, updated, List.of(1));

        assertEquals(1, result.getRoles().size());
    }

    @Test
    void updateAppUser_SameEmailSameUser_ShouldNotThrow() {
        user.setId(1);
        user.setEmail("test@mail.com");

        user.setRoles(new ArrayList<>(List.of(role)));

        AppUser updated = new AppUser();
        updated.setEmail("test@mail.com");
        updated.setFirstName(user.getFirstName());
        updated.setLastName(user.getLastName());
        updated.setPasswordHash(user.getPasswordHash());
        updated.setAge(user.getAge());
        updated.setWeight(user.getWeight());
        updated.setHeight(user.getHeight());

        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(appUserRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser result = userService.updateAppUser(1, updated, List.of(1));

        assertEquals("test@mail.com", result.getEmail());
    }

    @Test
    void assignTrainerToUser_ShouldThrowIfTrainerNotFound() {
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(appUserRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.assignTrainerToUser(1, 2);
        });
    }

    @Test
    void createAppUser_ShouldThrow_WhenRoleIdsAreNull() {
        when(appUserRepository.findByEmail("test@mail.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                userService.createAppUser(user, null)
        );
    }



}
