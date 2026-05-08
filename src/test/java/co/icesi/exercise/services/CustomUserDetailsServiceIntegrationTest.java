package co.icesi.exercise.services;

import co.icesi.exercise.model.AppUser;
import co.icesi.exercise.model.Permission;
import co.icesi.exercise.model.Role;
import co.icesi.exercise.repositories.PermissionRepository;
import co.icesi.exercise.repositories.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
public class CustomUserDetailsServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_ShouldReadPersistedAuthoritiesFromDatabase() {
        Permission permission = new Permission();
        permission.setName("VIEW_USERS");
        permission = permissionRepository.save(permission);

        Role role = new Role();
        role.setName("ADMIN");
        role.setPermissions(new ArrayList<>(List.of(permission)));
        role = roleRepository.save(role);

        AppUser user = new AppUser();
        user.setFirstName("Samuel");
        user.setLastName("Security");
        user.setEmail("security@test.com");
        user.setPasswordHash("hashSeguridad");
        user.setAge(23);
        user.setWeight(70.0);
        user.setHeight(1.74);
        user.setRoles(new ArrayList<>());
        user.setTrainers(new ArrayList<>());

        userService.createAppUser(user, List.of(role.getId()));

        UserDetails result = customUserDetailsService.loadUserByUsername("security@test.com");

        assertNotNull(result);
        assertEquals("security@test.com", result.getUsername());

        Set<String> authorities = result.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertTrue(authorities.contains("ROLE_ADMIN"));
        assertTrue(authorities.contains("VIEW_USERS"));
    }
}