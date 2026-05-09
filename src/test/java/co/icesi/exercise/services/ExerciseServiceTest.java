package co.icesi.exercise.services;

import co.icesi.exercise.model.nosql.ExerciseDocument;
import co.icesi.exercise.model.nosql.VisualSupportDocument;
import co.icesi.exercise.repositories.nosql.ExerciseMongoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExerciseServiceTest {

    @Mock
    private ExerciseMongoRepository exerciseMongoRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    private ExerciseDocument exercise;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        exercise = new ExerciseDocument();
        exercise.setId("ex1");
        exercise.setExerciseName("Sentadilla");
        exercise.setType("fuerza");
        exercise.setDifficultyType("MEDIO");
        exercise.setDuration(30.0);
        exercise.setVisualSupports(new ArrayList<>());
    }

    @Test
    void getAllExercises_ShouldReturnList() {
        when(exerciseMongoRepository.findAll()).thenReturn(List.of(exercise));

        List<ExerciseDocument> result = exerciseService.getAllExercises();

        assertEquals(1, result.size());
        verify(exerciseMongoRepository).findAll();
    }

    @Test
    void getExerciseById_ShouldReturnExercise() {
        when(exerciseMongoRepository.findById("ex1")).thenReturn(Optional.of(exercise));

        ExerciseDocument result = exerciseService.getExerciseById("ex1");

        assertEquals("Sentadilla", result.getExerciseName());
    }

    @Test
    void getExerciseById_ShouldThrowIfNotFound() {
        when(exerciseMongoRepository.findById("ex1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> exerciseService.getExerciseById("ex1"));
    }

    @Test
    void searchExercisesByName_ShouldReturnMatches() {
        when(exerciseMongoRepository.findByExerciseNameContainingIgnoreCase("sent"))
                .thenReturn(List.of(exercise));

        List<ExerciseDocument> result = exerciseService.searchExercisesByName("sent");

        assertEquals(1, result.size());
        assertEquals("Sentadilla", result.get(0).getExerciseName());
    }

    @Test
    void createExercise_ShouldSave() {
        when(exerciseMongoRepository.save(exercise)).thenReturn(exercise);

        ExerciseDocument result = exerciseService.createExercise(exercise);

        assertEquals("ex1", result.getId());
        verify(exerciseMongoRepository).save(exercise);
    }

    @Test
    void updateExercise_ShouldUpdateFields() {
        ExerciseDocument updated = new ExerciseDocument();
        updated.setExerciseName("Press Banca");
        updated.setType("fuerza");
        updated.setDifficultyType("DIFÍCIL");
        updated.setDuration(45.0);
        updated.setDescription("Ejercicio pecho");

        when(exerciseMongoRepository.findById("ex1")).thenReturn(Optional.of(exercise));
        when(exerciseMongoRepository.save(any(ExerciseDocument.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ExerciseDocument result = exerciseService.updateExercise("ex1", updated);

        assertEquals("Press Banca", result.getExerciseName());
        assertEquals("DIFÍCIL", result.getDifficultyType());
        assertEquals(45.0, result.getDuration());
    }

    @Test
    void updateExercise_ShouldThrowIfNotFound() {
        when(exerciseMongoRepository.findById("ex1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> exerciseService.updateExercise("ex1", new ExerciseDocument()));
    }

    @Test
    void deleteExercise_ShouldDelete() {
        when(exerciseMongoRepository.findById("ex1")).thenReturn(Optional.of(exercise));

        exerciseService.deleteExercise("ex1");

        verify(exerciseMongoRepository).delete(exercise);
    }

    @Test
    void deleteExercise_ShouldThrowIfNotFound() {
        when(exerciseMongoRepository.findById("ex1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> exerciseService.deleteExercise("ex1"));
    }

    @Test
    void addVisualSupport_ShouldAddToList() {
        VisualSupportDocument vs = new VisualSupportDocument();
        vs.setSupportType("VIDEO");
        vs.setUrl("https://youtube.com/watch?v=abc");

        when(exerciseMongoRepository.findById("ex1")).thenReturn(Optional.of(exercise));
        when(exerciseMongoRepository.save(any(ExerciseDocument.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ExerciseDocument result = exerciseService.addVisualSupport("ex1", vs);

        assertEquals(1, result.getVisualSupports().size());
        assertEquals("VIDEO", result.getVisualSupports().get(0).getSupportType());
    }

    @Test
    void addVisualSupport_ShouldThrowIfExerciseNotFound() {
        when(exerciseMongoRepository.findById("ex1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> exerciseService.addVisualSupport("ex1", new VisualSupportDocument()));
    }

    @Test
    void removeVisualSupport_ShouldRemoveByIndex() {
        VisualSupportDocument vs = new VisualSupportDocument();
        vs.setSupportType("VIDEO");
        exercise.getVisualSupports().add(vs);

        when(exerciseMongoRepository.findById("ex1")).thenReturn(Optional.of(exercise));
        when(exerciseMongoRepository.save(any(ExerciseDocument.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ExerciseDocument result = exerciseService.removeVisualSupport("ex1", 0);

        assertTrue(result.getVisualSupports().isEmpty());
    }
}
