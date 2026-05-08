package co.icesi.exercise.services;

import co.icesi.exercise.model.Permission;
import co.icesi.exercise.model.Role;
import co.icesi.exercise.repositories.PermissionRepository;
import co.icesi.exercise.repositories.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
public class RoleServiceIntegrationTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Test
    void createRole_ShouldPersistRoleWithPermissions() {
        Permission permission = new Permission();
        permission.setName("VIEW_USERS");
        permission = permissionRepository.save(permission);

        Role role = new Role();
        role.setName("COORDINATOR");
        role.setPermissions(new ArrayList<>());

        Role saved = roleService.createRole(role, List.of(permission.getId()));

        Role persisted = roleRepository.findById(saved.getId()).orElseThrow();

        assertEquals("COORDINATOR", persisted.getName());
        assertEquals(1, persisted.getPermissions().size());
        assertEquals("VIEW_USERS", persisted.getPermissions().get(0).getName());
    }

    @Test
    void assignPermissionToRole_ShouldPersistRelation() {
        Permission permission = new Permission();
        permission.setName("MANAGE_USERS");
        permission = permissionRepository.save(permission);

        Role role = new Role();
        role.setName("SUPERVISOR");
        role.setPermissions(new ArrayList<>());
        role = roleRepository.save(role);

        roleService.assignPermissionToRole(role.getId(), permission.getId());

        Role persisted = roleRepository.findById(role.getId()).orElseThrow();
        assertEquals(1, persisted.getPermissions().size());
        assertEquals("MANAGE_USERS", persisted.getPermissions().get(0).getName());
    }
}