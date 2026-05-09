package co.icesi.exercise.repositories;

import co.icesi.exercise.model.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    @EntityGraph(attributePaths = {"permissions"})
    List<Role> findAll();

    Optional<Role> findByName(String name);
    List<Role> findByUsersId(Integer userId);
}
