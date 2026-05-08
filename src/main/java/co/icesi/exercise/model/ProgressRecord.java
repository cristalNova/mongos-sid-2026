package co.icesi.exercise.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name = "progress_record")
@Data
public class ProgressRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Date date;
    private Time time;
    private String progressNotes;
    private int series;
    private int repetitions;
    private double weight;
    private String equipmentUsed;

    @ManyToOne
    @JoinColumn(name = "routineExercise")
    private RoutineExercise routineExercise;


}
