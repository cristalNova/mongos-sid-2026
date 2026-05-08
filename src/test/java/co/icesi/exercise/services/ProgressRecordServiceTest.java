package co.icesi.exercise.services;

import co.icesi.exercise.model.ProgressRecord;
import co.icesi.exercise.model.RoutineExercise;
import co.icesi.exercise.repositories.ProgressRecordRepository;
import co.icesi.exercise.repositories.RoutineExerciseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class ProgressRecordServiceTest {

    @Mock
    private ProgressRecordRepository repository;

    @Mock
    private RoutineExerciseRepository routineRepository;

    @InjectMocks
    private ProgressRecordService service;

    private ProgressRecord record;
    private RoutineExercise exercise;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        exercise = new RoutineExercise();
        exercise.setId(1);

        record = new ProgressRecord();
        record.setId(1);
        record.setDate(Date.valueOf(LocalDate.now()));
        record.setTime(Time.valueOf(LocalTime.now()));
        record.setRoutineExercise(exercise);
        record.setProgressNotes("Init");
        record.setSeries(3);
        record.setRepetitions(10);
        record.setWeight(50.0);
        record.setEquipmentUsed("Dumbbell");
    }

    @Test
    void getAllProgressRecords_ShouldReturnList() {
        when(repository.findAll()).thenReturn(List.of(record));

        List<ProgressRecord> result = service.getAllProgressRecords();

        assertEquals(1, result.size());
        verify(repository).findAll();
    }

    @Test
    void getProgressRecordById_ShouldReturnRecord() {
        when(repository.findById(1)).thenReturn(Optional.of(record));

        ProgressRecord result = service.getProgressRecordById(1);

        assertNotNull(result);
    }

    @Test
    void getProgressRecordById_ShouldThrowException() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            service.getProgressRecordById(1);
        });
    }

    @Test
    void getProgressRecordsByRoutineExerciseId_ShouldReturnList() {
        when(repository.findByRoutineExerciseId(1))
                .thenReturn(List.of(record));

        List<ProgressRecord> result =
                service.getProgressRecordsByRoutineExerciseId(1);

        assertEquals(1, result.size());
        verify(repository).findByRoutineExerciseId(1);
    }

    @Test
    void createProgressRecord_ShouldSave() {
        when(routineRepository.findById(1)).thenReturn(Optional.of(exercise));
        when(repository.save(any(ProgressRecord.class))).thenReturn(record);

        ProgressRecord result = service.createProgressRecord(record, 1);

        assertEquals(exercise, result.getRoutineExercise());
        verify(repository).save(record);
    }

    @Test
    void createProgressRecord_ShouldThrowException() {
        when(routineRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            service.createProgressRecord(record, 1);
        });
    }

    // ✅ 7. updateProgressRecord SIN cambiar rutina
    @Test
    void updateProgressRecord_ShouldUpdateWithoutChangingRoutine() {
        ProgressRecord updated = new ProgressRecord();
        updated.setDate(Date.valueOf(LocalDate.now().plusDays(1)));
        updated.setTime(Time.valueOf(LocalTime.now()));
        updated.setProgressNotes("Updated");
        updated.setSeries(4);
        updated.setRepetitions(12);
        updated.setWeight(60.0);
        updated.setEquipmentUsed("Barbell");

        when(repository.findById(1)).thenReturn(Optional.of(record));
        when(repository.save(any(ProgressRecord.class))).thenReturn(record);

        ProgressRecord result =
                service.updateProgressRecord(1, updated, null);

        assertEquals("Updated", result.getProgressNotes());
        assertEquals(4, result.getSeries());
        verify(repository).save(record);
    }

    @Test
    void updateProgressRecord_ShouldUpdateWithNewRoutine() {
        RoutineExercise newRoutine = new RoutineExercise();
        newRoutine.setId(2);

        ProgressRecord updated = new ProgressRecord();
        updated.setProgressNotes("Updated");

        when(repository.findById(1)).thenReturn(Optional.of(record));
        when(routineRepository.findById(2)).thenReturn(Optional.of(newRoutine));
        when(repository.save(any(ProgressRecord.class))).thenReturn(record);

        ProgressRecord result =
                service.updateProgressRecord(1, updated, 2);

        assertEquals(newRoutine, result.getRoutineExercise());
        verify(repository).save(record);
    }

    @Test
    void updateProgressRecord_ShouldThrowIfRoutineNotFound() {
        ProgressRecord updated = new ProgressRecord();

        when(repository.findById(1)).thenReturn(Optional.of(record));
        when(routineRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            service.updateProgressRecord(1, updated, 2);
        });
    }

    @Test
    void updateProgressRecord_ShouldThrowIfRecordNotFound() {
        ProgressRecord updated = new ProgressRecord();

        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            service.updateProgressRecord(1, updated, null);
        });
    }

    @Test
    void deleteProgressRecord_ShouldDelete() {
        when(repository.findById(1)).thenReturn(Optional.of(record));

        service.deleteProgressRecord(1);

        verify(repository).delete(record);
    }

    @Test
    void deleteProgressRecord_ShouldThrowException() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            service.deleteProgressRecord(1);
        });
    }


}
