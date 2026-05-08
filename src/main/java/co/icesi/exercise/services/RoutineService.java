package co.icesi.exercise.services;

import co.icesi.exercise.model.AppUser;
import co.icesi.exercise.model.Difficulty;
import co.icesi.exercise.model.Routine;
import co.icesi.exercise.model.Type;
import co.icesi.exercise.repositories.AppUserRepository;
import co.icesi.exercise.repositories.DifficultyRepository;
import co.icesi.exercise.repositories.RoutineRepository;
import co.icesi.exercise.repositories.TypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoutineService {

    @Autowired
    private RoutineRepository routineRepository;
    @Autowired
    private DifficultyRepository difficultyRepository;
    @Autowired
    private TypeRepository typeRepository;
    @Autowired
    private AppUserRepository appUserRepository;

    public List<Routine> getAllRoutines() {
        return routineRepository.findAll();
    }

    public Routine getRoutineById(int id) {
        return routineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rutina no encontrada con id: " + id));
    }

    public List<Routine> getRoutinesByOwnerId(int ownerId) {
        return routineRepository.findByOwnerId(ownerId);
    }

    public List<Routine> getPublicRoutines() {
        return routineRepository.findByVisibilityTrue();
    }

    @Transactional
    public Routine createRoutine(Routine routine, int difficultyId, int typeId, int ownerId) {
        Difficulty difficulty = difficultyRepository.findById(difficultyId)
                .orElseThrow(() -> new EntityNotFoundException("Dificultad no encontrada con id: " + difficultyId));
        Type type = typeRepository.findById(typeId)
                .orElseThrow(() -> new EntityNotFoundException("Tipo no encontrado con id: " + typeId));
        AppUser owner = appUserRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario propietario no encontrado con id: " + ownerId));

        routine.setDifficulty(difficulty);
        routine.setType(type);
        routine.setOwner(owner);
        return routineRepository.save(routine);
    }

    @Transactional
    public Routine updateRoutine(int id, Routine updatedRoutine, Integer difficultyId, Integer typeId, Integer ownerId) {
        Routine existingRoutine = getRoutineById(id);
        existingRoutine.setRoutineName(updatedRoutine.getRoutineName());
        existingRoutine.setVisibility(updatedRoutine.getVisibility());

        if (difficultyId != null) {
            Difficulty difficulty = difficultyRepository.findById(difficultyId)
                    .orElseThrow(() -> new EntityNotFoundException("Dificultad no encontrada con id: " + difficultyId));
            existingRoutine.setDifficulty(difficulty);
        }

        if (typeId != null) {
            Type type = typeRepository.findById(typeId)
                    .orElseThrow(() -> new EntityNotFoundException("Tipo no encontrado con id: " + typeId));
            existingRoutine.setType(type);
        }

        if (ownerId != null) {
            AppUser owner = appUserRepository.findById(ownerId)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario propietario no encontrado con id: " + ownerId));
            existingRoutine.setOwner(owner);
        }

        return routineRepository.save(existingRoutine);
    }

    @Transactional
    public void deleteRoutine(int id) {
        Routine existingRoutine = getRoutineById(id);
        routineRepository.delete(existingRoutine);
    }
}
