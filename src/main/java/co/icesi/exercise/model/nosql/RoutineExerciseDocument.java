package co.icesi.exercise.model.nosql;

import lombok.Data;

@Data
public class RoutineExerciseDocument {
    private String exerciseId;
    private String exerciseName;
    private String type;
    private String difficultyType;
    private Double duration;
}
