package co.icesi.exercise.services;

import co.icesi.exercise.model.Exercise;
import co.icesi.exercise.model.VisualSupport;
import co.icesi.exercise.repositories.ExerciseRepository;
import co.icesi.exercise.repositories.VisualSupportRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

public class VisualSupportServiceTest {

    @Mock
    private VisualSupportRepository visualSupportRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private VisualSupportService visualSupportService;

    private VisualSupport visualSupport;
    private Exercise exercise;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        exercise = new Exercise();
        exercise.setId(1);

        visualSupport = new VisualSupport();
        visualSupport.setId(1);
        visualSupport.setSupportType("VIDEO");
        visualSupport.setUrl("http://test.com");
        visualSupport.setExercise(exercise);
    }

    @Test
    void getAllVisualSupports_ShouldReturnList() {
        when(visualSupportRepository.findAll()).thenReturn(List.of(visualSupport));

        List<VisualSupport> result = visualSupportService.getAllVisualSupports();

        assertEquals(1, result.size());
        verify(visualSupportRepository).findAll();
    }

    @Test
    void getVisualSupportById_ShouldReturn() {
        when(visualSupportRepository.findById(1)).thenReturn(Optional.of(visualSupport));

        VisualSupport result = visualSupportService.getVisualSupportById(1);

        assertNotNull(result);
    }

    @Test
    void getVisualSupportById_ShouldThrowException() {
        when(visualSupportRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            visualSupportService.getVisualSupportById(1);
        });
    }

    @Test
    void getVisualSupportsByExerciseId_ShouldReturnList() {
        when(visualSupportRepository.findByExerciseId(1))
                .thenReturn(List.of(visualSupport));

        List<VisualSupport> result =
                visualSupportService.getVisualSupportsByExerciseId(1);

        assertEquals(1, result.size());
        verify(visualSupportRepository).findByExerciseId(1);
    }

    @Test
    void createVisualSupport_ShouldSave() {
        when(exerciseRepository.findById(1)).thenReturn(Optional.of(exercise));
        when(visualSupportRepository.save(any(VisualSupport.class)))
                .thenReturn(visualSupport);

        VisualSupport result =
                visualSupportService.createVisualSupport(visualSupport, 1);

        assertEquals(exercise, result.getExercise());
        verify(visualSupportRepository).save(visualSupport);
    }

    @Test
    void createVisualSupport_ShouldThrowIfExerciseNotFound() {
        when(exerciseRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            visualSupportService.createVisualSupport(visualSupport, 1);
        });
    }

    @Test
    void updateVisualSupport_ShouldUpdateBasicFields() {
        VisualSupport updated = new VisualSupport();
        updated.setSupportType("IMAGE");
        updated.setUrl("new-url");

        when(visualSupportRepository.findById(1)).thenReturn(Optional.of(visualSupport));
        when(visualSupportRepository.save(any(VisualSupport.class)))
                .thenReturn(visualSupport);

        VisualSupport result =
                visualSupportService.updateVisualSupport(1, updated, null);

        assertEquals("IMAGE", result.getSupportType());
        assertEquals("new-url", result.getUrl());
    }

    @Test
    void updateVisualSupport_ShouldUpdateExercise() {
        Exercise newExercise = new Exercise();
        newExercise.setId(2);

        when(visualSupportRepository.findById(1)).thenReturn(Optional.of(visualSupport));
        when(exerciseRepository.findById(2)).thenReturn(Optional.of(newExercise));
        when(visualSupportRepository.save(any(VisualSupport.class)))
                .thenReturn(visualSupport);

        VisualSupport result =
                visualSupportService.updateVisualSupport(1, new VisualSupport(), 2);

        assertEquals(newExercise, result.getExercise());
    }

    @Test
    void updateVisualSupport_ShouldThrowIfExerciseNotFound() {
        when(visualSupportRepository.findById(1)).thenReturn(Optional.of(visualSupport));
        when(exerciseRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            visualSupportService.updateVisualSupport(1, new VisualSupport(), 2);
        });
    }

    @Test
    void updateVisualSupport_ShouldThrowIfNotFound() {
        when(visualSupportRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            visualSupportService.updateVisualSupport(1, new VisualSupport(), null);
        });
    }

    @Test
    void deleteVisualSupport_ShouldDelete() {
        when(visualSupportRepository.findById(1)).thenReturn(Optional.of(visualSupport));

        visualSupportService.deleteVisualSupport(1);

        verify(visualSupportRepository).delete(visualSupport);
    }

    @Test
    void deleteVisualSupport_ShouldThrowException() {
        when(visualSupportRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            visualSupportService.deleteVisualSupport(1);
        });
    }
}
