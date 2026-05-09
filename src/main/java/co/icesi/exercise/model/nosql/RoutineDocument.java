package co.icesi.exercise.model.nosql;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "routines")
@Data
public class RoutineDocument {

    @Id
    private String id;
    private String routineName;
    private Boolean visibility;
    private String type;
    private String difficultyType;
    private Integer ownerId;
    private String ownerFirstName;
    private String ownerLastName;
    private Date createdAt;
    private Date updatedAt;
    private List<RoutineExerciseDocument> exercises = new ArrayList<>();
}
