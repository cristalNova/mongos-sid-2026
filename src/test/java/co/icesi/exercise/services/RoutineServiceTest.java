package co.icesi.exercise.services;

import co.icesi.exercise.model.AppUser;
import co.icesi.exercise.model.nosql.ExerciseDocument;
import co.icesi.exercise.model.nosql.RoutineDocument;
import co.icesi.exercise.model.nosql.RoutineExerciseDocument;
import co.icesi.exercise.repositories.AppUserRepository;
import co.icesi.exercise.repositories.nosql.ExerciseMongoRepository;
import co.icesi.exercise.repositories.nosql.RoutineMongoRepository;
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

public class RoutineServiceTest {

    @Mock
    private RoutineMongoRepository routineMongoRepository;
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private ExerciseMongoRepository exerciseMongoRepository;

    @InjectMocks
    private RoutineService routineService;

    private AppUser owner;
    private RoutineDocument routine;
    private ExerciseDocument exercise;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        owner = new AppUser();
        owner.setId(1);
        owner.setFirstName("Camilo");
        owner.setLastName("Vargas");

        routine = new RoutineDocument();
        routine.setId("r1");
        routine.setRoutineName("Full Body");
        routine.setType("mixto");
        routine.setDifficultyType("MEDIO");
        routine.setVisibility(true);
        routine.setOwnerId(1);
        routine.setOwnerFirstName("Camilo");
        routine.setOwnerLastName("Vargas");
        routine.setExercises(new ArrayList<>());

        exercise = new ExerciseDocument();
        exercise.setId("ex1");
        exercise.setExerciseName("Sentadilla");
        exercise.setType("fuerza");
        exercise.setDifficultyType("MEDIO");
        exercise.setDuration(30.0);
    }

    @Test
    void getAllRoutines_ShouldReturnList() {
        when(routineMongoRepository.findAll()).thenReturn(List.of(routine));

        List<RoutineDocument> result = routineService.getAllRoutines();

        assertEquals(1, result.size());
        verify(routineMongoRepository).findAll();
    }

    @Test
    void getRoutineById_ShouldReturnRoutine() {
        when(routineMongoRepository.findById("r1")).thenReturn(Optional.of(routine));

        RoutineDocument result = routineService.getRoutineById("r1");

        assertEquals("Full Body", result.getRoutineName());
    }

    @Test
    void getRoutineById_ShouldThrowIfNotFound() {
        when(routineMongoRepository.findById("r1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> routineService.getRoutineById("r1"));
    }

    @Test
    void getRoutinesByOwnerId_ShouldReturnList() {
        when(routineMongoRepository.findByOwnerId(1)).thenReturn(List.of(routine));

        List<RoutineDocument> result = routineService.getRoutinesByOwnerId(1);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getOwnerId());
    }

    @Test
    void getPublicRoutines_ShouldReturnOnlyPublic() {
        when(routineMongoRepository.findByVisibilityTrue()).thenReturn(List.of(routine));

        List<RoutineDocument> result = routineService.getPublicRoutines();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getVisibility());
    }

    @Test
    void createRoutine_ShouldSetOwnerInfo() {
        when(appUserRepository.findById(1)).thenReturn(Optional.of(owner));
        when(routineMongoRepository.save(any(RoutineDocument.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        RoutineDocument newRoutine = new RoutineDocument();
        newRoutine.setRoutineName("Cardio");
        newRoutine.setVisibility(false);

        RoutineDocument result = routineService.createRoutine(newRoutine, 1);

        assertEquals(1, result.getOwnerId());
        assertEquals("Camilo", result.getOwnerFirstName());
        assertEquals("Vargas", result.getOwnerLastName());
        assertNotNull(result.getCreatedAt());
        verify(routineMongoRepository).save(newRoutine);
    }

    @Test
    void createRoutine_ShouldThrowIfOwnerNotFound() {
        when(appUserRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> routineService.createRoutine(new RoutineDocument(), 1));
    }

    @Test
    void updateRoutine_ShouldUpdateFields() {
        RoutineDocument updated = new RoutineDocument();
        updated.setRoutineName("Piernas Avanzado");
        updated.setType("fuerza");
        updated.setDifficultyType("DIFÍCIL");
        updated.setVisibility(false);

        when(routineMongoRepository.findById("r1")).thenReturn(Optional.of(routine));
        when(routineMongoRepository.save(any(RoutineDocument.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        RoutineDocument result = routineService.updateRoutine("r1", updated);

        assertEquals("Piernas Avanzado", result.getRoutineName());
        assertEquals("DIFÍCIL", result.getDifficultyType());
        assertFalse(result.getVisibility());
    }

    @Test
    void updateRoutine_ShouldThrowIfNotFound() {
        when(routineMongoRepository.findById("r1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> routineService.updateRoutine("r1", new RoutineDocument()));
    }

    @Test
    void deleteRoutine_ShouldDelete() {
        when(routineMongoRepository.findById("r1")).thenReturn(Optional.of(routine));

        routineService.deleteRoutine("r1");

        verify(routineMongoRepository).delete(routine);
    }

    @Test
    void deleteRoutine_ShouldThrowIfNotFound() {
        when(routineMongoRepository.findById("r1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> routineService.deleteRoutine("r1"));
    }

    @Test
    void addExercise_ShouldEmbedExerciseInRoutine() {
        when(routineMongoRepository.findById("r1")).thenReturn(Optional.of(routine));
        when(exerciseMongoRepository.findById("ex1")).thenReturn(Optional.of(exercise));
        when(routineMongoRepository.save(any(RoutineDocument.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        RoutineDocument result = routineService.addExercise("r1", "ex1");

        assertEquals(1, result.getExercises().size());
        assertEquals("Sentadilla", result.getExercises().get(0).getExerciseName());
    }

    @Test
    void addExercise_ShouldThrowIfExerciseNotFound() {
        when(routineMongoRepository.findById("r1")).thenReturn(Optional.of(routine));
        when(exerciseMongoRepository.findById("ex1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> routineService.addExercise("r1", "ex1"));
    }

    @Test
    void removeExercise_ShouldRemoveByIndex() {
        RoutineExerciseDocument re = new RoutineExerciseDocument();
        re.setExerciseId("ex1");
        re.setExerciseName("Sentadilla");
        routine.getExercises().add(re);

        when(routineMongoRepository.findById("r1")).thenReturn(Optional.of(routine));
        when(routineMongoRepository.save(any(RoutineDocument.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        RoutineDocument result = routineService.removeExercise("r1", 0);

        assertTrue(result.getExercises().isEmpty());
    }

    @Test
    void adoptRoutine_ShouldCreatePrivateCopyForNewOwner() {
        AppUser newOwner = new AppUser();
        newOwner.setId(2);
        newOwner.setFirstName("Valentina");
        newOwner.setLastName("Torres");

        RoutineExerciseDocument re = new RoutineExerciseDocument();
        re.setExerciseName("Press");
        routine.getExercises().add(re);

        when(routineMongoRepository.findById("r1")).thenReturn(Optional.of(routine));
        when(appUserRepository.findById(2)).thenReturn(Optional.of(newOwner));
        when(routineMongoRepository.save(any(RoutineDocument.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        RoutineDocument copy = routineService.adoptRoutine("r1", 2);

        assertEquals("Full Body (adoptada)", copy.getRoutineName());
        assertFalse(copy.getVisibility());
        assertEquals(2, copy.getOwnerId());
        assertEquals("Valentina", copy.getOwnerFirstName());
        assertEquals(1, copy.getExercises().size());
    }

    @Test
    void adoptRoutine_ShouldThrowIfRoutineNotFound() {
        when(routineMongoRepository.findById("r1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> routineService.adoptRoutine("r1", 2));
    }

    @Test
    void adoptRoutine_ShouldThrowIfNewOwnerNotFound() {
        when(routineMongoRepository.findById("r1")).thenReturn(Optional.of(routine));
        when(appUserRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> routineService.adoptRoutine("r1", 2));
    }
}
