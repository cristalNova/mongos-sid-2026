package co.icesi.exercise.services;

import co.icesi.exercise.model.Exercise;
import co.icesi.exercise.repositories.ExerciseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    public Exercise getExerciseById(int id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ejercicio no encontrado con id: " + id));
    }

    public List<Exercise> searchExercisesByName(String exerciseName) {
        return exerciseRepository.findByExerciseNameContainingIgnoreCase(exerciseName);
    }

    @Transactional
    public Exercise createExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    @Transactional
    public Exercise updateExercise(int id, Exercise updatedExercise) {
        Exercise existingExercise = getExerciseById(id);
        existingExercise.setExerciseName(updatedExercise.getExerciseName());
        existingExercise.setDescription(updatedExercise.getDescription());
        return exerciseRepository.save(existingExercise);
    }

    @Transactional
    public void deleteExercise(int id) {
        Exercise existingExercise = getExerciseById(id);
        exerciseRepository.delete(existingExercise);
    }
}
