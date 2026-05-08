package co.icesi.exercise.services;

import co.icesi.exercise.model.AppUser;
import co.icesi.exercise.model.Permission;
import co.icesi.exercise.model.Role;
import co.icesi.exercise.repositories.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomUserDetailsServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private AppUser user;
    private Role role;
    private Permission permission1;
    private Permission permission2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        permission1 = new Permission();
        permission1.setId(10);
        permission1.setName("VIEW_USERS");

        permission2 = new Permission();
        permission2.setId(11);
        permission2.setName("ROLE_LIST");

        role = new Role();
        role.setId(1);
        role.setName("ADMIN");
        role.setPermissions(List.of(permission1, permission2));

        user = new AppUser();
        user.setId(1);
        user.setFirstName("Samuel");
        user.setLastName("Navia");
        user.setEmail("samuel@icesi.edu.co");
        user.setPasswordHash("$2a$10$encodedPassword123");
        user.setRoles(List.of(role));
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetailsWithAuthorities_WhenEmailExists() {
        when(appUserRepository.findWithRolesByEmail("samuel@icesi.edu.co"))
                .thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("samuel@icesi.edu.co");

        assertNotNull(result);
        assertEquals("samuel@icesi.edu.co", result.getUsername());
        assertEquals("$2a$10$encodedPassword123", result.getPassword());

        Set<String> authorities = result.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertTrue(authorities.contains("ROLE_ADMIN"));
        assertTrue(authorities.contains("VIEW_USERS"));
        assertTrue(authorities.contains("ROLE_LIST"));

        verify(appUserRepository).findWithRolesByEmail("samuel@icesi.edu.co");
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenEmailDoesNotExist() {
        when(appUserRepository.findWithRolesByEmail("noexiste@icesi.edu.co"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("noexiste@icesi.edu.co")
        );

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(appUserRepository).findWithRolesByEmail("noexiste@icesi.edu.co");
    }
}