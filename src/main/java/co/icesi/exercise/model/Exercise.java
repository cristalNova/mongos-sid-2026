package co.icesi.exercise.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "exercise")
@Data
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String exerciseName;
    private String description;

    //visusal supports

    @OneToMany(mappedBy = "exercise")
    List<VisualSupport> visualSupports;

    //routine
    @OneToMany(mappedBy = "exercise")
    List<RoutineExercise> routineExercises;

}
