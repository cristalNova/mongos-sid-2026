package co.icesi.exercise.repositories;

import co.icesi.exercise.model.RoutineExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutineExerciseRepository extends JpaRepository<RoutineExercise, Integer> {
    List<RoutineExercise> findByRoutineId(Integer routineId);
    List<RoutineExercise> findByExerciseId(Integer exerciseId);
}
