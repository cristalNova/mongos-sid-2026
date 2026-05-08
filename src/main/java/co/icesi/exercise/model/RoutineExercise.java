package co.icesi.exercise.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "routine_exercise")
@Data
public class RoutineExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "routineId")
    private Routine routine;

    @ManyToOne
    @JoinColumn(name = "exerciseId")
    private Exercise exercise;

    @OneToMany(mappedBy = "routineExercise")
    private List<ProgressRecord> progressRecords;
}
