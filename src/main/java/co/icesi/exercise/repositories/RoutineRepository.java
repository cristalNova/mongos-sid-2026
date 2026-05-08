package co.icesi.exercise.repositories;

import co.icesi.exercise.model.Routine;
import co.icesi.exercise.model.RoutineExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Integer> {
    List<Routine> findByOwnerId(Integer ownerId);
    List<Routine> findByVisibilityTrue();
}
