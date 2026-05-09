package co.icesi.exercise.repositories.nosql;

import co.icesi.exercise.model.nosql.ExerciseDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExerciseMongoRepository extends MongoRepository<ExerciseDocument, String> {
    List<ExerciseDocument> findByExerciseNameContainingIgnoreCase(String exerciseName);
}
