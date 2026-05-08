package co.icesi.exercise.repositories;

import co.icesi.exercise.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {
    List<Exercise> findByExerciseNameContainingIgnoreCase(String exerciseName);
}
