package co.icesi.exercise.services;

import co.icesi.exercise.model.nosql.ExerciseDocument;
import co.icesi.exercise.model.nosql.VisualSupportDocument;
import co.icesi.exercise.repositories.nosql.ExerciseMongoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseService {

    @Autowired
    private ExerciseMongoRepository exerciseMongoRepository;

    public List<ExerciseDocument> getAllExercises() {
        return exerciseMongoRepository.findAll();
    }

    public ExerciseDocument getExerciseById(String id) {
        return exerciseMongoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ejercicio no encontrado con id: " + id));
    }

    public List<ExerciseDocument> searchExercisesByName(String name) {
        return exerciseMongoRepository.findByExerciseNameContainingIgnoreCase(name);
    }

    public ExerciseDocument createExercise(ExerciseDocument exercise) {
        return exerciseMongoRepository.save(exercise);
    }

    public ExerciseDocument updateExercise(String id, ExerciseDocument updated) {
        ExerciseDocument existing = getExerciseById(id);
        existing.setExerciseName(updated.getExerciseName());
        existing.setDescription(updated.getDescription());
        existing.setType(updated.getType());
        existing.setDifficultyType(updated.getDifficultyType());
        existing.setDuration(updated.getDuration());
        return exerciseMongoRepository.save(existing);
    }

    public void deleteExercise(String id) {
        ExerciseDocument existing = getExerciseById(id);
        exerciseMongoRepository.delete(existing);
    }

    public ExerciseDocument addVisualSupport(String exerciseId, VisualSupportDocument vs) {
        ExerciseDocument exercise = getExerciseById(exerciseId);
        exercise.getVisualSupports().add(vs);
        return exerciseMongoRepository.save(exercise);
    }

    public ExerciseDocument removeVisualSupport(String exerciseId, int index) {
        ExerciseDocument exercise = getExerciseById(exerciseId);
        exercise.getVisualSupports().remove(index);
        return exerciseMongoRepository.save(exercise);
    }
}
