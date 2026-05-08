package co.icesi.exercise.services;

import co.icesi.exercise.model.ProgressRecord;
import co.icesi.exercise.model.RoutineExercise;
import co.icesi.exercise.repositories.ProgressRecordRepository;
import co.icesi.exercise.repositories.RoutineExerciseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProgressRecordService {

    @Autowired
    private ProgressRecordRepository progressRecordRepository;
    @Autowired
    private RoutineExerciseRepository routineExerciseRepository;

    public List<ProgressRecord> getAllProgressRecords() {
        return progressRecordRepository.findAll();
    }

    public ProgressRecord getProgressRecordById(int id) {
        return progressRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registro de progreso no encontrado con id: " + id));
    }

    public List<ProgressRecord> getProgressRecordsByRoutineExerciseId(int routineExerciseId) {
        return progressRecordRepository.findByRoutineExerciseId(routineExerciseId);
    }

    @Transactional
    public ProgressRecord createProgressRecord(ProgressRecord progressRecord, int routineExerciseId) {
        RoutineExercise routineExercise = routineExerciseRepository.findById(routineExerciseId)
                .orElseThrow(() -> new EntityNotFoundException("Relación rutina-ejercicio no encontrada con id: " + routineExerciseId));
        progressRecord.setRoutineExercise(routineExercise);
        return progressRecordRepository.save(progressRecord);
    }

    @Transactional
    public ProgressRecord updateProgressRecord(int id, ProgressRecord updatedProgressRecord, Integer routineExerciseId) {
        ProgressRecord existingProgressRecord = getProgressRecordById(id);
        existingProgressRecord.setDate(updatedProgressRecord.getDate());
        existingProgressRecord.setTime(updatedProgressRecord.getTime());
        existingProgressRecord.setProgressNotes(updatedProgressRecord.getProgressNotes());
        existingProgressRecord.setSeries(updatedProgressRecord.getSeries());
        existingProgressRecord.setRepetitions(updatedProgressRecord.getRepetitions());
        existingProgressRecord.setWeight(updatedProgressRecord.getWeight());
        existingProgressRecord.setEquipmentUsed(updatedProgressRecord.getEquipmentUsed());

        if (routineExerciseId != null) {
            RoutineExercise routineExercise = routineExerciseRepository.findById(routineExerciseId)
                    .orElseThrow(() -> new EntityNotFoundException("Relación rutina-ejercicio no encontrada con id: " + routineExerciseId));
            existingProgressRecord.setRoutineExercise(routineExercise);
        }

        return progressRecordRepository.save(existingProgressRecord);
    }

    @Transactional
    public void deleteProgressRecord(int id) {
        ProgressRecord existingProgressRecord = getProgressRecordById(id);
        progressRecordRepository.delete(existingProgressRecord);
    }
}
