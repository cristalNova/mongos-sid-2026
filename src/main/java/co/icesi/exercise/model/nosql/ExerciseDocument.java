package co.icesi.exercise.model.nosql;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "exercises")
@Data
public class ExerciseDocument {

    @Id
    private String id;
    private String exerciseName;
    private String description;
    private String type;
    private String difficultyType;
    private Double duration;
    private List<VisualSupportDocument> visualSupports = new ArrayList<>();
}
