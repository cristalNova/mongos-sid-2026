package co.icesi.exercise.repositories;

import co.icesi.exercise.model.AppUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
    Optional<AppUser> findByEmail(String email);
    List<AppUser> findByRolesId(Integer roleId);
    List<AppUser> findByTrainersId(Integer trainerId);
    @EntityGraph(attributePaths = {"roles"})
    Optional<AppUser> findWithRolesByEmail(String email);
}
