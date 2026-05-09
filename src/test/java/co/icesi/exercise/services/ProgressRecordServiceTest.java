package co.icesi.exercise.services;

import co.icesi.exercise.model.AppUser;
import co.icesi.exercise.model.nosql.ProgressRecordDocument;
import co.icesi.exercise.model.nosql.RoutineDocument;
import co.icesi.exercise.repositories.AppUserRepository;
import co.icesi.exercise.repositories.nosql.ProgressRecordMongoRepository;
import co.icesi.exercise.repositories.nosql.RoutineMongoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProgressRecordServiceTest {

    @Mock
    private ProgressRecordMongoRepository progressRecordMongoRepository;
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private RoutineMongoRepository routineMongoRepository;

    @InjectMocks
    private ProgressRecordService progressRecordService;

    private AppUser user;
    private RoutineDocument routine;
    private ProgressRecordDocument record;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new AppUser();
        user.setId(1);
        user.setFirstName("Camilo");
        user.setLastName("Vargas");

        routine = new RoutineDocument();
        routine.setId("r1");
        routine.setRoutineName("Piernas");

        record = new ProgressRecordDocument();
        record.setId("rec1");
        record.setUserId(1);
        record.setRoutineId("r1");
        record.setExerciseName("Sentadilla");
        record.setDate(new Date());
        record.setSeries(4);
        record.setRepetitions(12);
        record.setWeight(65.0);
    }

    @Test
    void getAllProgressRecords_ShouldReturnList() {
        when(progressRecordMongoRepository.findAll()).thenReturn(List.of(record));

        List<ProgressRecordDocument> result = progressRecordService.getAllProgressRecords();

        assertEquals(1, result.size());
        verify(progressRecordMongoRepository).findAll();
    }

    @Test
    void getProgressRecordById_ShouldReturnRecord() {
        when(progressRecordMongoRepository.findById("rec1")).thenReturn(Optional.of(record));

        ProgressRecordDocument result = progressRecordService.getProgressRecordById("rec1");

        assertEquals("Sentadilla", result.getExerciseName());
    }

    @Test
    void getProgressRecordById_ShouldThrowIfNotFound() {
        when(progressRecordMongoRepository.findById("rec1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> progressRecordService.getProgressRecordById("rec1"));
    }

    @Test
    void getProgressRecordsByUserId_ShouldReturnList() {
        when(progressRecordMongoRepository.findByUserId(1)).thenReturn(List.of(record));

        List<ProgressRecordDocument> result = progressRecordService.getProgressRecordsByUserId(1);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getUserId());
    }

    @Test
    void getProgressRecordsByRoutineId_ShouldReturnList() {
        when(progressRecordMongoRepository.findByRoutineId("r1")).thenReturn(List.of(record));

        List<ProgressRecordDocument> result = progressRecordService.getProgressRecordsByRoutineId("r1");

        assertEquals(1, result.size());
        assertEquals("r1", result.get(0).getRoutineId());
    }

    @Test
    void createProgressRecord_ShouldSetUserAndRoutineInfo() {
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(routineMongoRepository.findById("r1")).thenReturn(Optional.of(routine));
        when(progressRecordMongoRepository.save(any(ProgressRecordDocument.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ProgressRecordDocument newRecord = new ProgressRecordDocument();
        newRecord.setExerciseName("Press Banca");

        ProgressRecordDocument result = progressRecordService.createProgressRecord(newRecord, 1, "r1");

        assertEquals(1, result.getUserId());
        assertEquals("Camilo", result.getUserFirstName());
        assertEquals("Vargas", result.getUserLastName());
        assertEquals("r1", result.getRoutineId());
        assertEquals("Piernas", result.getRoutineName());
        verify(progressRecordMongoRepository).save(newRecord);
    }

    @Test
    void createProgressRecord_ShouldThrowIfUserNotFound() {
        when(appUserRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> progressRecordService.createProgressRecord(record, 1, "r1"));
    }

    @Test
    void createProgressRecord_ShouldThrowIfRoutineNotFound() {
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(routineMongoRepository.findById("r1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> progressRecordService.createProgressRecord(record, 1, "r1"));
    }

    @Test
    void updateProgressRecord_ShouldUpdateFields() {
        ProgressRecordDocument updated = new ProgressRecordDocument();
        updated.setExerciseName("Peso Muerto");
        updated.setSeries(5);
        updated.setRepetitions(8);
        updated.setWeight(100.0);
        updated.setProgressNotes("Buena forma");
        updated.setEquipmentUsed("Barra");

        when(progressRecordMongoRepository.findById("rec1")).thenReturn(Optional.of(record));
        when(progressRecordMongoRepository.save(any(ProgressRecordDocument.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ProgressRecordDocument result = progressRecordService.updateProgressRecord("rec1", updated);

        assertEquals("Peso Muerto", result.getExerciseName());
        assertEquals(5, result.getSeries());
        assertEquals(8, result.getRepetitions());
        assertEquals(100.0, result.getWeight());
        assertEquals("Buena forma", result.getProgressNotes());
    }

    @Test
    void updateProgressRecord_ShouldThrowIfNotFound() {
        when(progressRecordMongoRepository.findById("rec1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> progressRecordService.updateProgressRecord("rec1", new ProgressRecordDocument()));
    }

    @Test
    void deleteProgressRecord_ShouldDelete() {
        when(progressRecordMongoRepository.findById("rec1")).thenReturn(Optional.of(record));

        progressRecordService.deleteProgressRecord("rec1");

        verify(progressRecordMongoRepository).delete(record);
    }

    @Test
    void deleteProgressRecord_ShouldThrowIfNotFound() {
        when(progressRecordMongoRepository.findById("rec1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> progressRecordService.deleteProgressRecord("rec1"));
    }
}
