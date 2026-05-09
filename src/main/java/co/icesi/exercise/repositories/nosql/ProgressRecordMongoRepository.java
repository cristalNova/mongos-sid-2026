package co.icesi.exercise.repositories.nosql;

import co.icesi.exercise.model.nosql.ProgressRecordDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProgressRecordMongoRepository extends MongoRepository<ProgressRecordDocument, String> {
    List<ProgressRecordDocument> findByUserId(Integer userId);
    List<ProgressRecordDocument> findByRoutineId(String routineId);
}
