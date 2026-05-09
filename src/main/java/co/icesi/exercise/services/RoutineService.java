package co.icesi.exercise.services;

import co.icesi.exercise.model.AppUser;
import co.icesi.exercise.model.nosql.ExerciseDocument;
import co.icesi.exercise.model.nosql.RoutineDocument;
import co.icesi.exercise.model.nosql.RoutineExerciseDocument;
import co.icesi.exercise.repositories.AppUserRepository;
import co.icesi.exercise.repositories.nosql.ExerciseMongoRepository;
import co.icesi.exercise.repositories.nosql.RoutineMongoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RoutineService {

    @Autowired
    private RoutineMongoRepository routineMongoRepository;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private ExerciseMongoRepository exerciseMongoRepository;

    public List<RoutineDocument> getAllRoutines() {
        return routineMongoRepository.findAll();
    }

    public RoutineDocument getRoutineById(String id) {
        return routineMongoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rutina no encontrada con id: " + id));
    }

    public List<RoutineDocument> getRoutinesByOwnerId(Integer ownerId) {
        return routineMongoRepository.findByOwnerId(ownerId);
    }

    public List<RoutineDocument> getPublicRoutines() {
        return routineMongoRepository.findByVisibilityTrue();
    }

    public RoutineDocument createRoutine(RoutineDocument routine, int ownerId) {
        AppUser owner = appUserRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + ownerId));
        routine.setOwnerId(owner.getId());
        routine.setOwnerFirstName(owner.getFirstName());
        routine.setOwnerLastName(owner.getLastName());
        routine.setCreatedAt(new Date());
        routine.setUpdatedAt(new Date());
        return routineMongoRepository.save(routine);
    }

    public RoutineDocument updateRoutine(String id, RoutineDocument updated) {
        RoutineDocument existing = getRoutineById(id);
        existing.setRoutineName(updated.getRoutineName());
        existing.setVisibility(updated.getVisibility());
        existing.setType(updated.getType());
        existing.setDifficultyType(updated.getDifficultyType());
        existing.setUpdatedAt(new Date());
        return routineMongoRepository.save(existing);
    }

    public void deleteRoutine(String id) {
        RoutineDocument existing = getRoutineById(id);
        routineMongoRepository.delete(existing);
    }

    public RoutineDocument addExercise(String routineId, String exerciseId) {
        RoutineDocument routine = getRoutineById(routineId);
        ExerciseDocument exercise = exerciseMongoRepository.findById(exerciseId)
                .orElseThrow(() -> new EntityNotFoundException("Ejercicio no encontrado con id: " + exerciseId));

        RoutineExerciseDocument re = new RoutineExerciseDocument();
        re.setExerciseId(exercise.getId());
        re.setExerciseName(exercise.getExerciseName());
        re.setType(exercise.getType());
        re.setDifficultyType(exercise.getDifficultyType());
        re.setDuration(exercise.getDuration());

        routine.getExercises().add(re);
        routine.setUpdatedAt(new Date());
        return routineMongoRepository.save(routine);
    }

    public RoutineDocument removeExercise(String routineId, int index) {
        RoutineDocument routine = getRoutineById(routineId);
        routine.getExercises().remove(index);
        routine.setUpdatedAt(new Date());
        return routineMongoRepository.save(routine);
    }
}
