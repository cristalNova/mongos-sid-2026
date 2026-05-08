package co.icesi.exercise.services;

import co.icesi.exercise.model.Permission;
import co.icesi.exercise.model.Role;
import co.icesi.exercise.repositories.PermissionRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private RoleService roleService;

    private Role role;
    private Permission permission;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        permission = new Permission();
        permission.setId(1);
        permission.setName("READ");

        role = new Role();
        role.setId(1);
        role.setName("ADMIN");
        role.setPermissions(new ArrayList<>());
    }

    @Test
    void getAllRoles_ShouldReturnList() {
        when(roleRepository.findAll()).thenReturn(List.of(role));

        List<Role> result = roleService.getAllRoles();

        assertEquals(1, result.size());
        verify(roleRepository).findAll();
    }

    @Test
    void getRoleById_ShouldReturnRole() {
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));

        Role result = roleService.getRoleById(1);

        assertEquals("ADMIN", result.getName());
    }

    @Test
    void getRoleById_ShouldThrowException() {
        when(roleRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            roleService.getRoleById(1);
        });
    }

    @Test
    void getRolesByUserId_ShouldReturnList() {
        when(roleRepository.findByUsersId(1)).thenReturn(List.of(role));

        List<Role> result = roleService.getRolesByUserId(1);

        assertEquals(1, result.size());
        verify(roleRepository).findByUsersId(1);
    }

    @Test
    void createRole_ShouldSaveWithPermissions() {
        when(permissionRepository.findById(1)).thenReturn(Optional.of(permission));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role result = roleService.createRole(role, List.of(1));

        assertEquals(1, result.getPermissions().size());
        verify(roleRepository).save(role);
    }

    @Test
    void createRole_ShouldThrowIfPermissionNotFound() {
        when(permissionRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            roleService.createRole(role, List.of(1));
        });
    }

    @Test
    void updateRole_ShouldUpdateWithoutPermissions() {
        Role updated = new Role();
        updated.setName("USER");

        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role result = roleService.updateRole(1, updated, null);

        assertEquals("USER", result.getName());
        verify(roleRepository).save(role);
    }

    @Test
    void updateRole_ShouldUpdatePermissions() {
        Permission newPermission = new Permission();
        newPermission.setId(2);

        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(2)).thenReturn(Optional.of(newPermission));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role updated = new Role();
        updated.setName("ADMIN");

        Role result = roleService.updateRole(1, updated, List.of(2));

        assertEquals(1, result.getPermissions().size());
        verify(roleRepository).save(role);
    }

    @Test
    void updateRole_ShouldThrowIfPermissionNotFound() {
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            roleService.updateRole(1, new Role(), List.of(2));
        });
    }
    
    @Test
    void assignPermissionToRole_ShouldAddPermission() {
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(1)).thenReturn(Optional.of(permission));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role result = roleService.assignPermissionToRole(1, 1);

        assertEquals(1, result.getPermissions().size());
        verify(roleRepository).save(role);
    }

    @Test
    void assignPermissionToRole_ShouldNotDuplicate() {
        role.getPermissions().add(permission);

        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(1)).thenReturn(Optional.of(permission));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role result = roleService.assignPermissionToRole(1, 1);

        assertEquals(1, result.getPermissions().size());
    }

    @Test
    void assignPermissionToRole_ShouldThrowIfPermissionNotFound() {
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            roleService.assignPermissionToRole(1, 1);
        });
    }

    @Test
    void removePermissionFromRole_ShouldRemove() {
        role.getPermissions().add(permission);

        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role result = roleService.removePermissionFromRole(1, 1);

        assertEquals(0, result.getPermissions().size());
        verify(roleRepository).save(role);
    }

    @Test
    void deleteRole_ShouldDelete() {
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));

        roleService.deleteRole(1);

        verify(roleRepository).delete(role);
    }

    @Test
    void deleteRole_ShouldThrowException() {
        when(roleRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            roleService.deleteRole(1);
        });
    }
    @Test
    void createRole_WithEmptyPermissions_ShouldReturnEmptyList() {
        when(roleRepository.save(any())).thenReturn(role);

        Role result = roleService.createRole(role, new ArrayList<>());

        assertEquals(0, result.getPermissions().size());
    }

    @Test
    void createRole_WithNullPermissions_ShouldReturnEmptyList() {
        when(roleRepository.save(any())).thenReturn(role);

        Role result = roleService.createRole(role, null);

        assertEquals(0, result.getPermissions().size());
    }

    @Test
    void removePermissionFromRole_WhenPermissionNotExists_ShouldDoNothing() {
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(roleRepository.save(any())).thenReturn(role);

        Role result = roleService.removePermissionFromRole(1, 99);

        assertEquals(0, result.getPermissions().size());
    }

    @Test
    void updateRole_WithEmptyPermissions_ShouldClearPermissions() {
        role.getPermissions().add(permission);

        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(roleRepository.save(any())).thenReturn(role);

        Role updated = new Role();
        updated.setName("ADMIN");

        Role result = roleService.updateRole(1, updated, new ArrayList<>());

        assertEquals(0, result.getPermissions().size());
    }

    @Test
    void assignPermissionToRole_WhenEmptyList_ShouldAdd() {
        role.setPermissions(new ArrayList<>());

        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(1)).thenReturn(Optional.of(permission));
        when(roleRepository.save(any())).thenReturn(role);

        Role result = roleService.assignPermissionToRole(1, 1);

        assertEquals(1, result.getPermissions().size());
    }
}
