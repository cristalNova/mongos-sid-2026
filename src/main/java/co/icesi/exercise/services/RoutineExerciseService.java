package co.icesi.exercise.services;

import co.icesi.exercise.model.Exercise;
import co.icesi.exercise.model.Routine;
import co.icesi.exercise.model.RoutineExercise;
import co.icesi.exercise.repositories.ExerciseRepository;
import co.icesi.exercise.repositories.RoutineExerciseRepository;
import co.icesi.exercise.repositories.RoutineRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoutineExerciseService {

    @Autowired
    private RoutineExerciseRepository routineExerciseRepository;
    @Autowired
    private RoutineRepository routineRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;

    public List<RoutineExercise> getAllRoutineExercises() {
        return routineExerciseRepository.findAll();
    }

    public RoutineExercise getRoutineExerciseById(int id) {
        return routineExerciseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Relación rutina-ejercicio no encontrada con id: " + id));
    }

    public List<RoutineExercise> getRoutineExercisesByRoutineId(int routineId) {
        return routineExerciseRepository.findByRoutineId(routineId);
    }

    public List<RoutineExercise> getRoutineExercisesByExerciseId(int exerciseId) {
        return routineExerciseRepository.findByExerciseId(exerciseId);
    }

    @Transactional
    public RoutineExercise createRoutineExercise(int routineId, int exerciseId) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new EntityNotFoundException("Rutina no encontrada con id: " + routineId));
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new EntityNotFoundException("Ejercicio no encontrado con id: " + exerciseId));

        RoutineExercise routineExercise = new RoutineExercise();
        routineExercise.setRoutine(routine);
        routineExercise.setExercise(exercise);
        return routineExerciseRepository.save(routineExercise);
    }

    @Transactional
    public RoutineExercise updateRoutineExercise(int id, Integer routineId, Integer exerciseId) {
        RoutineExercise existingRoutineExercise = getRoutineExerciseById(id);

        if (routineId != null) {
            Routine routine = routineRepository.findById(routineId)
                    .orElseThrow(() -> new EntityNotFoundException("Rutina no encontrada con id: " + routineId));
            existingRoutineExercise.setRoutine(routine);
        }

        if (exerciseId != null) {
            Exercise exercise = exerciseRepository.findById(exerciseId)
                    .orElseThrow(() -> new EntityNotFoundException("Ejercicio no encontrado con id: " + exerciseId));
            existingRoutineExercise.setExercise(exercise);
        }

        return routineExerciseRepository.save(existingRoutineExercise);
    }

    @Transactional
    public void deleteRoutineExercise(int id) {
        RoutineExercise existingRoutineExercise = getRoutineExerciseById(id);
        routineExerciseRepository.delete(existingRoutineExercise);
    }
}
