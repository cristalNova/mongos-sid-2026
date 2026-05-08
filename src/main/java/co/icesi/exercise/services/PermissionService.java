package co.icesi.exercise.services;

import co.icesi.exercise.model.Permission;
import co.icesi.exercise.repositories.PermissionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public Permission getPermissionById(int id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permiso no encontrado con id: " + id));
    }

    public List<Permission> getPermissionsByRoleId(int roleId) {
        return permissionRepository.findByRolesId(roleId);
    }

    @Transactional
    public Permission createPermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Transactional
    public Permission updatePermission(int id, Permission updatedPermission) {
        Permission existingPermission = getPermissionById(id);
        existingPermission.setName(updatedPermission.getName());
        return permissionRepository.save(existingPermission);
    }

    @Transactional
    public void deletePermission(int id) {
        Permission existingPermission = getPermissionById(id);
        permissionRepository.delete(existingPermission);
    }
}
