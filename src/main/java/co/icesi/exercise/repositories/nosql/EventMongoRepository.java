package co.icesi.exercise.repositories.nosql;

import co.icesi.exercise.model.nosql.EventDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventMongoRepository extends MongoRepository<EventDocument, String> {
    List<EventDocument> findByNameContainingIgnoreCase(String name);
}
