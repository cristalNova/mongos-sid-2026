package co.icesi.exercise.services;

import co.icesi.exercise.model.Exercise;
import co.icesi.exercise.repositories.ExerciseRepository;
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

public class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    private Exercise exercise;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        exercise = new Exercise();
        exercise.setId(1);
        exercise.setExerciseName("Push Up");
        exercise.setDescription("Chest exercise");
    }

    @Test
    void getAll_ShouldReturnAllExercises() {
        when(exerciseRepository.findAll()).thenReturn(List.of(exercise));

        List<Exercise> exercises = exerciseService.getAllExercises();

        assertEquals(1,exercises.size());
        verify(exerciseRepository).findAll();
    }
    @Test
    void getExerciseById_ShouldReturnExercise() {
        when(exerciseRepository.findById(1)).thenReturn(Optional.of(exercise));

        Exercise result = exerciseService.getExerciseById(1);

        assertEquals("Push Up", result.getExerciseName());
    }

    @Test
    void getExerciseById_ShouldThrowException() {
        when(exerciseRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            exerciseService.getExerciseById(1);
        });
    }

    @Test
    void searchExercisesByName_ShouldReturnList() {
        when(exerciseRepository.findByExerciseNameContainingIgnoreCase("push"))
                .thenReturn(List.of(exercise));

        List<Exercise> result = exerciseService.searchExercisesByName("push");

        assertEquals(1, result.size());
        verify(exerciseRepository)
                .findByExerciseNameContainingIgnoreCase("push");
    }

    @Test
    void createExercise_ShouldSave() {
        when(exerciseRepository.save(exercise)).thenReturn(exercise);

        Exercise result = exerciseService.createExercise(exercise);

        assertNotNull(result);
        verify(exerciseRepository).save(exercise);
    }

    @Test
    void updateExercise_ShouldUpdate() {
        Exercise updated = new Exercise();
        updated.setExerciseName("Squat");
        updated.setDescription("Leg exercise");

        when(exerciseRepository.findById(1)).thenReturn(Optional.of(exercise));
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(exercise);

        Exercise result = exerciseService.updateExercise(1, updated);

        assertEquals("Squat", result.getExerciseName());
        assertEquals("Leg exercise", result.getDescription());
        verify(exerciseRepository).save(exercise);
    }

    @Test
    void updateExercise_ShouldThrowException() {
        Exercise updated = new Exercise();

        when(exerciseRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            exerciseService.updateExercise(1, updated);
        });
    }

    @Test
    void deleteExercise_ShouldDelete() {
        when(exerciseRepository.findById(1)).thenReturn(Optional.of(exercise));

        exerciseService.deleteExercise(1);

        verify(exerciseRepository).delete(exercise);
    }

    @Test
    void deleteExercise_ShouldThrowException() {
        when(exerciseRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            exerciseService.deleteExercise(1);
        });
    }

}
