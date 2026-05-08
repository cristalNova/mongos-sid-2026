package co.icesi.exercise.repositories;

import co.icesi.exercise.model.ProgressRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgressRecordRepository extends JpaRepository<ProgressRecord, Integer> {
    List<ProgressRecord> findByRoutineExerciseId(Integer routineExerciseId);
}
