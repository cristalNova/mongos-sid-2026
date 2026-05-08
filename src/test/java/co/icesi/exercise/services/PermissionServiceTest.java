package co.icesi.exercise.services;

import co.icesi.exercise.model.Permission;
import co.icesi.exercise.repositories.PermissionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionService permissionService;

    private Permission permission;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        permission = new Permission();
        permission.setId(1);
        permission.setName("READ");

    }
    @Test
    void getAllPermissions_ShouldReturnList() {
        when(permissionRepository.findAll()).thenReturn(List.of(permission));

        List<Permission> result = permissionService.getAllPermissions();

        assertEquals(1, result.size());
        verify(permissionRepository).findAll();
    }
    @Test
    void getPermissionById_ShouldReturnPermission() {
        when(permissionRepository.findById(1)).thenReturn(Optional.of(permission));

        Permission result = permissionService.getPermissionById(1);

        assertEquals("READ", result.getName());
    }

    @Test
    void getPermissionById_ShouldThrowException() {
        when(permissionRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            permissionService.getPermissionById(1);
        });
    }

    @Test
    void getPermissionsByRoleId_ShouldReturnList() {
        when(permissionRepository.findByRolesId(1)).thenReturn(List.of(permission));

        List<Permission> result = permissionService.getPermissionsByRoleId(1);

        assertEquals(1, result.size());
        verify(permissionRepository).findByRolesId(1);
    }

    @Test
    void createPermission_ShouldSave() {
        when(permissionRepository.save(permission)).thenReturn(permission);

        Permission result = permissionService.createPermission(permission);

        assertNotNull(result);
        verify(permissionRepository).save(permission);
    }

    @Test
    void updatePermission_ShouldUpdate() {
        Permission updated = new Permission();
        updated.setName("WRITE");

        when(permissionRepository.findById(1)).thenReturn(Optional.of(permission));
        when(permissionRepository.save(any(Permission.class))).thenReturn(permission);

        Permission result = permissionService.updatePermission(1, updated);

        assertEquals("WRITE", result.getName());
        verify(permissionRepository).save(permission);
    }

    @Test
    void updatePermission_ShouldThrowException() {
        Permission updated = new Permission();

        when(permissionRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            permissionService.updatePermission(1, updated);
        });
    }

    @Test
    void deletePermission_ShouldDelete() {
        when(permissionRepository.findById(1)).thenReturn(Optional.of(permission));

        permissionService.deletePermission(1);

        verify(permissionRepository).delete(permission);
    }

    @Test
    void deletePermission_ShouldThrowException() {
        when(permissionRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            permissionService.deletePermission(1);
        });
    }



}
