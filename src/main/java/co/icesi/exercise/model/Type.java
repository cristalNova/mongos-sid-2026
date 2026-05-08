package co.icesi.exercise.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "type")
@Data
public class Type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String typeName;
}
