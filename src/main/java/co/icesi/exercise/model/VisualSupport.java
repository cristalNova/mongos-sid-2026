package co.icesi.exercise.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "visual_support")
@Data
public class VisualSupport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String supportType;
    private String url;

    @ManyToOne
    @JoinColumn(name = "exerciseId")
    private Exercise exercise;
}
