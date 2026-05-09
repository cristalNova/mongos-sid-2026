package co.icesi.exercise.model.nosql;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Document(collection = "progress_records")
@Data
public class ProgressRecordDocument {

    @Id
    private String id;
    private Integer userId;
    private String userFirstName;
    private String userLastName;
    private String routineId;
    private String routineName;
    private String exerciseName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private String time;
    private Integer series;
    private Integer repetitions;
    private Double weight;
    private String progressNotes;
    private String equipmentUsed;
}
