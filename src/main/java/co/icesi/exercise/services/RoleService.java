package co.icesi.exercise.services;

import co.icesi.exercise.model.Permission;
import co.icesi.exercise.model.Role;
import co.icesi.exercise.repositories.PermissionRepository;
import co.icesi.exercise.repositories.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(int id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con id: " + id));
    }

    public List<Role> getRolesByUserId(int userId) {
        return roleRepository.findByUsersId(userId);
    }

    @Transactional
    public Role createRole(Role role, List<Integer> permissionIds) {
        role.setPermissions(getPermissionsFromIds(permissionIds));
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(int id, Role updatedRole, List<Integer> permissionIds) {
        Role existingRole = getRoleById(id);
        existingRole.setName(updatedRole.getName());

        if (permissionIds != null) {
            existingRole.setPermissions(getPermissionsFromIds(permissionIds));
        }

        return roleRepository.save(existingRole);
    }

    @Transactional
    public Role assignPermissionToRole(int roleId, int permissionId) {
        Role role = getRoleById(roleId);
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new EntityNotFoundException("Permiso no encontrado con id: " + permissionId));

        if (role.getPermissions().stream().noneMatch(p -> p.getId() == permissionId)) {
            role.getPermissions().add(permission);
        }

        return roleRepository.save(role);
    }

    @Transactional
    public Role removePermissionFromRole(int roleId, int permissionId) {
        Role role = getRoleById(roleId);
        role.getPermissions().removeIf(permission -> permission.getId() == permissionId);
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(int id) {
        Role existingRole = getRoleById(id);
        roleRepository.delete(existingRole);
    }

    private List<Permission> getPermissionsFromIds(List<Integer> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Permission> permissions = new ArrayList<>();
        for (Integer permissionId : permissionIds) {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new EntityNotFoundException("Permiso no encontrado con id: " + permissionId));
            permissions.add(permission);
        }
        return permissions;
    }
}
