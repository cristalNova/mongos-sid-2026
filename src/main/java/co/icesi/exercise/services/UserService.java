package co.icesi.exercise.services;

import co.icesi.exercise.model.AppUser;
import co.icesi.exercise.model.Role;
import co.icesi.exercise.repositories.AppUserRepository;
import co.icesi.exercise.repositories.RoleRepository;
import jakarta.persistence.AssociationOverrides;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private RoleRepository roleRepository;

    public List<AppUser> getAllAppUsers() {
        return appUserRepository.findAll();
    }

    public AppUser getAppUserById(int id) {
        return appUserRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
    }

    public AppUser getAppUserWithRoles(int id) {
        return appUserRepository.findWithRolesById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
    }

    public AppUser getAppUserWithTrainers(int id) {
        return appUserRepository.findWithTrainersById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
    }

    public AppUser getUserByEmail(String email) {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con email: " + email));
    }

    public List<AppUser> getUsersByRoleId(int roleId) {
        return appUserRepository.findByRolesId(roleId);
    }

    public List<AppUser> getUsersByTrainerId(int trainerId) {
        return appUserRepository.findByTrainersId(trainerId);
    }

    @Transactional
    public AppUser createAppUser(AppUser appUser, List<Integer> roleIds) {
        validateEmailAvailability(appUser.getEmail(), null);
        appUser.setRoles(getRolesFromIds(roleIds));
        if(appUser.getRoles().isEmpty()){
            throw new IllegalArgumentException("El usuario debe tener al menos un rol");
        }
        if (appUser.getTrainers() == null) {
            appUser.setTrainers(new ArrayList<>());
        }

        return appUserRepository.save(appUser);
    }

    @Transactional
    public AppUser updateAppUser(int id, AppUser updatedUser, List<Integer> roleIds) {

        AppUser existingUser = getAppUserById(id);
        validateEmailAvailability(updatedUser.getEmail(), id);

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setAge(updatedUser.getAge());
        existingUser.setWeight(updatedUser.getWeight());
        existingUser.setHeight(updatedUser.getHeight());

        if (updatedUser.getPasswordHash() != null &&
                !updatedUser.getPasswordHash().isEmpty()) {
            existingUser.setPasswordHash(updatedUser.getPasswordHash());
        }

        if (roleIds != null && !roleIds.isEmpty()) {
            existingUser.setRoles(getRolesFromIds(roleIds));
        }

        return appUserRepository.save(existingUser);
    }

    @Transactional
    public AppUser assignRoleToUser(int userId, int roleId) {
        AppUser user = getAppUserById(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con id: " + roleId));

        if (user.getRoles().stream().noneMatch(r -> r.getId() == roleId)) {
            user.getRoles().add(role);
        }

        return appUserRepository.save(user);
    }

    @Transactional
    public AppUser removeRoleFromUser(int userId, int roleId) {
        AppUser user = getAppUserById(userId);
        if(user.getRoles().size() <= 1){
            throw new IllegalStateException("Usuario tiene que tener al menos un rol asignado");
        }
        user.getRoles().removeIf(role -> role.getId() == roleId);
        return appUserRepository.save(user);
    }

    @Transactional
    public AppUser assignTrainerToUser(int userId, int trainerId) {
        if (userId == trainerId) {
            throw new IllegalArgumentException("Un usuario no puede asignarse a sí mismo como entrenador");
        }

        AppUser user = getAppUserById(userId);
        AppUser trainer = getAppUserById(trainerId);

        if (user.getTrainers().stream().noneMatch(t -> t.getId() == trainerId)) {
            user.getTrainers().add(trainer);
        }

        return appUserRepository.save(user);
    }

    @Transactional
    public AppUser removeTrainerFromUser(int userId, int trainerId) {
        AppUser user = getAppUserWithTrainers(userId);
        user.getTrainers().removeIf(t -> t.getId() == trainerId);
        return appUserRepository.save(user);
    }

    @Transactional
    public void deleteAppUserById(int id) {
        AppUser existingUser = getAppUserById(id);
        appUserRepository.delete(existingUser);
    }

    private List<Role> getRolesFromIds(List<Integer> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Role> roles = new ArrayList<>();
        for (Integer roleId : roleIds) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con id: " + roleId));
            roles.add(role);
        }
        return roles;
    }

    private void validateEmailAvailability(String email, Integer currentUserId) {
        appUserRepository.findByEmail(email).ifPresent(existingUser -> {
            if (currentUserId == null || existingUser.getId() != currentUserId) {
                throw new IllegalArgumentException("Ya existe un usuario con el correo: " + email);
            }
        });
    }
}
