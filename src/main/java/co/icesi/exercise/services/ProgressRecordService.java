package co.icesi.exercise.services;

import co.icesi.exercise.model.AppUser;
import co.icesi.exercise.model.nosql.ProgressRecordDocument;
import co.icesi.exercise.model.nosql.RoutineDocument;
import co.icesi.exercise.repositories.AppUserRepository;
import co.icesi.exercise.repositories.nosql.ProgressRecordMongoRepository;
import co.icesi.exercise.repositories.nosql.RoutineMongoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgressRecordService {

    @Autowired
    private ProgressRecordMongoRepository progressRecordMongoRepository;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private RoutineMongoRepository routineMongoRepository;

    public List<ProgressRecordDocument> getAllProgressRecords() {
        return progressRecordMongoRepository.findAll();
    }

    public ProgressRecordDocument getProgressRecordById(String id) {
        return progressRecordMongoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registro de progreso no encontrado con id: " + id));
    }

    public List<ProgressRecordDocument> getProgressRecordsByUserId(Integer userId) {
        return progressRecordMongoRepository.findByUserId(userId);
    }

    public List<ProgressRecordDocument> getProgressRecordsByRoutineId(String routineId) {
        return progressRecordMongoRepository.findByRoutineId(routineId);
    }

    public ProgressRecordDocument createProgressRecord(ProgressRecordDocument record, int userId, String routineId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + userId));
        RoutineDocument routine = routineMongoRepository.findById(routineId)
                .orElseThrow(() -> new EntityNotFoundException("Rutina no encontrada con id: " + routineId));

        record.setUserId(user.getId());
        record.setUserFirstName(user.getFirstName());
        record.setUserLastName(user.getLastName());
        record.setRoutineId(routine.getId());
        record.setRoutineName(routine.getRoutineName());
        return progressRecordMongoRepository.save(record);
    }

    public ProgressRecordDocument updateProgressRecord(String id, ProgressRecordDocument updated) {
        ProgressRecordDocument existing = getProgressRecordById(id);
        existing.setDate(updated.getDate());
        existing.setTime(updated.getTime());
        existing.setProgressNotes(updated.getProgressNotes());
        existing.setSeries(updated.getSeries());
        existing.setRepetitions(updated.getRepetitions());
        existing.setWeight(updated.getWeight());
        existing.setEquipmentUsed(updated.getEquipmentUsed());
        existing.setExerciseName(updated.getExerciseName());
        return progressRecordMongoRepository.save(existing);
    }

    public void deleteProgressRecord(String id) {
        ProgressRecordDocument existing = getProgressRecordById(id);
        progressRecordMongoRepository.delete(existing);
    }
}
