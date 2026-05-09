package co.icesi.exercise.repositories.nosql;

import co.icesi.exercise.model.nosql.RoutineDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RoutineMongoRepository extends MongoRepository<RoutineDocument, String> {
    List<RoutineDocument> findByOwnerId(Integer ownerId);
    List<RoutineDocument> findByVisibilityTrue();
}
