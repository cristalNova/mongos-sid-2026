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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoutineServiceTest {

    @Mock
    private TypeRepository typeRepository;
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private DifficultyRepository difficultyRepository;
    @Mock
    private RoutineRepository routineRepository;

    @InjectMocks
    private RoutineService routineService;

    private Routine routine;
    private Difficulty difficulty;
    private AppUser owner;
    private Type type;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        difficulty = new Difficulty();
        difficulty.setId(1);

        type = new Type();
        type.setId(2);

        owner = new AppUser();
        owner.setId(3);

        routine = new Routine();
        routine.setId(1);
        routine.setRoutineName("Rutina A");
        routine.setVisibility(true);
        routine.setDifficulty(difficulty);
        routine.setType(type);
        routine.setOwner(owner);
    }

    @Test
    void getAllRoutines_ShouldReturnList() {
        when(routineRepository.findAll()).thenReturn(List.of(routine));

        List<Routine> result = routineService.getAllRoutines();

        assertEquals(1, result.size());
        verify(routineRepository).findAll();
    }

    @Test
    void getRoutineById_ShouldReturnRoutine() {
        when(routineRepository.findById(1)).thenReturn(Optional.of(routine));

        Routine result = routineService.getRoutineById(1);

        assertEquals("Rutina A", result.getRoutineName());
    }

    @Test
    void getRoutineById_ShouldThrowException() {
        when(routineRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            routineService.getRoutineById(1);
        });
    }

    @Test
    void getRoutinesByOwnerId_ShouldReturnList() {
        when(routineRepository.findByOwnerId(3)).thenReturn(List.of(routine));

        List<Routine> result = routineService.getRoutinesByOwnerId(3);

        assertEquals(1, result.size());
        verify(routineRepository).findByOwnerId(3);
    }

    @Test
    void getPublicRoutines_ShouldReturnList() {
        when(routineRepository.findByVisibilityTrue()).thenReturn(List.of(routine));

        List<Routine> result = routineService.getPublicRoutines();

        assertEquals(1, result.size());
        verify(routineRepository).findByVisibilityTrue();
    }

    @Test
    void createRoutine_ShouldSave() {
        when(difficultyRepository.findById(1)).thenReturn(Optional.of(difficulty));
        when(typeRepository.findById(2)).thenReturn(Optional.of(type));
        when(appUserRepository.findById(3)).thenReturn(Optional.of(owner));
        when(routineRepository.save(any(Routine.class))).thenReturn(routine);

        Routine result = routineService.createRoutine(routine, 1, 2, 3);

        assertEquals(difficulty, result.getDifficulty());
        assertEquals(type, result.getType());
        assertEquals(owner, result.getOwner());

        verify(routineRepository).save(routine);
    }

    @Test
    void createRoutine_ShouldThrowIfDifficultyNotFound() {
        when(difficultyRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            routineService.createRoutine(routine, 1, 2, 3);
        });
    }

    @Test
    void createRoutine_ShouldThrowIfTypeNotFound() {
        when(difficultyRepository.findById(1)).thenReturn(Optional.of(difficulty));
        when(typeRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            routineService.createRoutine(routine, 1, 2, 3);
        });
    }

    @Test
    void createRoutine_ShouldThrowIfOwnerNotFound() {
        when(difficultyRepository.findById(1)).thenReturn(Optional.of(difficulty));
        when(typeRepository.findById(2)).thenReturn(Optional.of(type));
        when(appUserRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            routineService.createRoutine(routine, 1, 2, 3);
        });
    }

    @Test
    void updateRoutine_ShouldUpdateBasicFields() {
        Routine updated = new Routine();
        updated.setRoutineName("Nueva Rutina");
        updated.setVisibility(false);

        when(routineRepository.findById(1)).thenReturn(Optional.of(routine));
        when(routineRepository.save(any(Routine.class))).thenReturn(routine);

        Routine result = routineService.updateRoutine(1, updated, null, null, null);

        assertEquals("Nueva Rutina", result.getRoutineName());
        assertFalse(result.getVisibility());
    }

    @Test
    void updateRoutine_ShouldUpdateAllRelations() {
        Difficulty newDiff = new Difficulty();
        newDiff.setId(10);

        Type newType = new Type();
        newType.setId(20);

        AppUser newOwner = new AppUser();
        newOwner.setId(30);

        when(routineRepository.findById(1)).thenReturn(Optional.of(routine));
        when(difficultyRepository.findById(10)).thenReturn(Optional.of(newDiff));
        when(typeRepository.findById(20)).thenReturn(Optional.of(newType));
        when(appUserRepository.findById(30)).thenReturn(Optional.of(newOwner));
        when(routineRepository.save(any(Routine.class))).thenReturn(routine);

        Routine result = routineService.updateRoutine(1, new Routine(), 10, 20, 30);

        assertEquals(newDiff, result.getDifficulty());
        assertEquals(newType, result.getType());
        assertEquals(newOwner, result.getOwner());
    }

    @Test
    void updateRoutine_ShouldThrowIfDifficultyNotFound() {
        when(routineRepository.findById(1)).thenReturn(Optional.of(routine));
        when(difficultyRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            routineService.updateRoutine(1, new Routine(), 10, null, null);
        });
    }

    @Test
    void updateRoutine_ShouldThrowIfTypeNotFound() {
        when(routineRepository.findById(1)).thenReturn(Optional.of(routine));
        when(typeRepository.findById(20)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            routineService.updateRoutine(1, new Routine(), null, 20, null);
        });
    }

    @Test
    void updateRoutine_ShouldThrowIfOwnerNotFound() {
        when(routineRepository.findById(1)).thenReturn(Optional.of(routine));
        when(appUserRepository.findById(30)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            routineService.updateRoutine(1, new Routine(), null, null, 30);
        });
    }

    @Test
    void updateRoutine_ShouldThrowIfRoutineNotFound() {
        when(routineRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            routineService.updateRoutine(1, new Routine(), null, null, null);
        });
    }

    @Test
    void deleteRoutine_ShouldDelete() {
        when(routineRepository.findById(1)).thenReturn(Optional.of(routine));

        routineService.deleteRoutine(1);

        verify(routineRepository).delete(routine);
    }
    @Test
    void deleteRoutine_ShouldThrowException() {
        when(routineRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            routineService.deleteRoutine(1);
        });
    }


}
