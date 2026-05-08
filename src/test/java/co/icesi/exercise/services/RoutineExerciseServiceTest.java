package co.icesi.exercise.services;

import co.icesi.exercise.model.Exercise;
import co.icesi.exercise.model.Routine;
import co.icesi.exercise.model.RoutineExercise;
import co.icesi.exercise.repositories.ExerciseRepository;
import co.icesi.exercise.repositories.RoutineExerciseRepository;
import co.icesi.exercise.repositories.RoutineRepository;
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

public class RoutineExerciseServiceTest {

    @Mock
    private RoutineExerciseRepository routineExerciseRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private RoutineRepository routineRepository;

    @InjectMocks
    private RoutineExerciseService routineExerciseService;

    private Routine routine;
    private Exercise exercise;
    private RoutineExercise routineExercise;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        routine = new Routine();
        routine.setId(1);

        exercise = new Exercise();
        exercise.setId(2);

        routineExercise = new RoutineExercise();
        routineExercise.setId(1);
        routineExercise.setRoutine(routine);
        routineExercise.setExercise(exercise);
    }

    @Test
    void getAllRoutineExercises_ShouldReturnList() {
        when(routineExerciseRepository.findAll()).thenReturn(List.of(routineExercise));

        List<RoutineExercise> result = routineExerciseService.getAllRoutineExercises();

        assertEquals(1, result.size());
        verify(routineExerciseRepository).findAll();
    }

    @Test
    void getRoutineExerciseById_ShouldReturn() {
        when(routineExerciseRepository.findById(1)).thenReturn(Optional.of(routineExercise));

        RoutineExercise result = routineExerciseService.getRoutineExerciseById(1);

        assertNotNull(result);
    }

    @Test
    void getRoutineExerciseById_ShouldThrowException() {
        when(routineExerciseRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            routineExerciseService.getRoutineExerciseById(1);
        });
    }

    @Test
    void getRoutineExercisesByRoutineId_ShouldReturnList() {
        when(routineExerciseRepository.findByRoutineId(1))
                .thenReturn(List.of(routineExercise));

        List<RoutineExercise> result =
                routineExerciseService.getRoutineExercisesByRoutineId(1);

        assertEquals(1, result.size());
        verify(routineExerciseRepository).findByRoutineId(1);
    }

    @Test
    void getRoutineExercisesByExerciseId_ShouldReturnList() {
        when(routineExerciseRepository.findByExerciseId(2))
                .thenReturn(List.of(routineExercise));

        List<RoutineExercise> result =
                routineExerciseService.getRoutineExercisesByExerciseId(2);

        assertEquals(1, result.size());
        verify(routineExerciseRepository).findByExerciseId(2);
    }

    @Test
    void createRoutineExercise_ShouldSave() {
        when(routineRepository.findById(1)).thenReturn(Optional.of(routine));
        when(exerciseRepository.findById(2)).thenReturn(Optional.of(exercise));
        when(routineExerciseRepository.save(any(RoutineExercise.class)))
                .thenReturn(routineExercise);

        RoutineExercise result =
                routineExerciseService.createRoutineExercise(1, 2);

        assertEquals(routine, result.getRoutine());
        assertEquals(exercise, result.getExercise());

        verify(routineExerciseRepository).save(any(RoutineExercise.class));
    }


    @Test
    void createRoutineExercise_ShouldThrowIfRoutineNotFound() {
        when(routineRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            routineExerciseService.createRoutineExercise(1, 2);
        });
    }


    @Test
    void createRoutineExercise_ShouldThrowIfExerciseNotFound() {
        when(routineRepository.findById(1)).thenReturn(Optional.of(routine));
        when(exerciseRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            routineExerciseService.createRoutineExercise(1, 2);
        });
    }

    @Test
    void updateRoutineExercise_ShouldUpdateNothing() {
        when(routineExerciseRepository.findById(1)).thenReturn(Optional.of(routineExercise));
        when(routineExerciseRepository.save(any(RoutineExercise.class)))
                .thenReturn(routineExercise);

        RoutineExercise result =
                routineExerciseService.updateRoutineExercise(1, null, null);

        assertNotNull(result);
        verify(routineExerciseRepository).save(routineExercise);
    }

    @Test
    void updateRoutineExercise_ShouldUpdateRoutine() {
        Routine newRoutine = new Routine();
        newRoutine.setId(10);

        when(routineExerciseRepository.findById(1)).thenReturn(Optional.of(routineExercise));
        when(routineRepository.findById(10)).thenReturn(Optional.of(newRoutine));
        when(routineExerciseRepository.save(any(RoutineExercise.class)))
                .thenReturn(routineExercise);

        RoutineExercise result =
                routineExerciseService.updateRoutineExercise(1, 10, null);

        assertEquals(newRoutine, result.getRoutine());
    }

    @Test
    void updateRoutineExercise_ShouldUpdateExercise() {
        Exercise newExercise = new Exercise();
        newExercise.setId(20);

        when(routineExerciseRepository.findById(1)).thenReturn(Optional.of(routineExercise));
        when(exerciseRepository.findById(20)).thenReturn(Optional.of(newExercise));
        when(routineExerciseRepository.save(any(RoutineExercise.class)))
                .thenReturn(routineExercise);

        RoutineExercise result =
                routineExerciseService.updateRoutineExercise(1, null, 20);

        assertEquals(newExercise, result.getExercise());
    }

    @Test
    void updateRoutineExercise_ShouldThrowIfRoutineNotFound() {
        when(routineExerciseRepository.findById(1)).thenReturn(Optional.of(routineExercise));
        when(routineRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            routineExerciseService.updateRoutineExercise(1, 10, null);
        });
    }

    @Test
    void updateRoutineExercise_ShouldThrowIfExerciseNotFound() {
        when(routineExerciseRepository.findById(1)).thenReturn(Optional.of(routineExercise));
        when(exerciseRepository.findById(20)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            routineExerciseService.updateRoutineExercise(1, null, 20);
        });
    }

    @Test
    void updateRoutineExercise_ShouldThrowIfNotFound() {
        when(routineExerciseRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            routineExerciseService.updateRoutineExercise(1, null, null);
        });
    }

    @Test
    void deleteRoutineExercise_ShouldDelete() {
        when(routineExerciseRepository.findById(1)).thenReturn(Optional.of(routineExercise));

        routineExerciseService.deleteRoutineExercise(1);

        verify(routineExerciseRepository).delete(routineExercise);
    }

    @Test
    void deleteRoutineExercise_ShouldThrowException() {
        when(routineExerciseRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            routineExerciseService.deleteRoutineExercise(1);
        });
    }
}
