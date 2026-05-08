package co.icesi.exercise.services;

import co.icesi.exercise.model.Difficulty;
import co.icesi.exercise.repositories.DifficultyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.*;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

public class DifficultyServiceTest {

    @Mock
    private DifficultyRepository difficultyRepository;

    @InjectMocks
    private DifficultyService difficultyService;

    private Difficulty difficulty;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        difficulty = new Difficulty();
        difficulty.setId(1);
        difficulty.setDifficultyName("Difficulty1");
    }

    @Test
    void getAllDifficulties() {
        List<Difficulty> list = List.of(difficulty);

        when(difficultyRepository.findAll()).thenReturn(list);

        List<Difficulty> allDifficulties = difficultyService.getAllDifficulties();

        assertEquals(1, allDifficulties.size());
        verify(difficultyRepository).findAll();

    }

    @Test
    void getDifficultyByIdSuccess() {
        when(difficultyRepository.findById(1)).thenReturn(Optional.of(difficulty));

        Difficulty result = difficultyService.getDifficultyById(1);

        assertEquals("Difficulty1", result.getDifficultyName());
    }

    @Test
    void getDifficultyByIdFail() {
        when(difficultyRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            difficultyService.getDifficultyById(1);
        });
    }

    @Test
    void createDifficultySuccess() {
        when(difficultyRepository.save(difficulty)).thenReturn(difficulty);

        Difficulty result = difficultyService.createDifficulty(difficulty);

        assertNotNull(result);
        verify(difficultyRepository).save(difficulty);
    }

    @Test
    void updateDifficultySuccess() {
        Difficulty updated = new Difficulty();
        updated.setDifficultyName("Hard");

        when(difficultyRepository.findById(1)).thenReturn(Optional.of(difficulty));
        when(difficultyRepository.save(any(Difficulty.class))).thenReturn(difficulty);

        Difficulty result = difficultyService.updateDifficulty(1, updated);

        assertEquals("Hard", result.getDifficultyName());
        verify(difficultyRepository).save(difficulty);
    }

    @Test
    void updateDifficultyFail() {
        Difficulty updated = new Difficulty();
        updated.setDifficultyName("Hard");

        when(difficultyRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            difficultyService.updateDifficulty(1, updated);
        });
    }

    @Test
    void deleteDifficulty_ShouldDelete() {
        when(difficultyRepository.findById(1)).thenReturn(Optional.of(difficulty));

        difficultyService.deleteDifficulty(1);

        verify(difficultyRepository).delete(difficulty);
    }

    @Test
    void deleteDifficulty_ShouldThrowException() {
        when(difficultyRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            difficultyService.deleteDifficulty(1);
        });
    }
}
