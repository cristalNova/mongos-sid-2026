package co.icesi.exercise.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "difficulty")
@Data
public class Difficulty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String difficultyName;
}
