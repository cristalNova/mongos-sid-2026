package co.icesi.exercise.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "routine")
@Data
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String routineName;
    private Boolean visibility;

    @ManyToOne
    @JoinColumn(name = "difficultyId")
    private Difficulty difficulty;

    @ManyToOne
    @JoinColumn(name = "typeId")
    private Type type;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    private AppUser owner;

    //exercises
    @OneToMany(mappedBy = "routine")
    List<RoutineExercise> routineExercises;
}
