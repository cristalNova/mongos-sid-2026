package co.icesi.exercise.repositories;

import co.icesi.exercise.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    List<Permission> findByRolesId(Integer roleId);
}
