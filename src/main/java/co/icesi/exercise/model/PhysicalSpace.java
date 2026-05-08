package co.icesi.exercise.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "physical_space")
public class PhysicalSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String location;
    private int capacity;

    @OneToMany(mappedBy = "physicalSpace")
    private List<Event> events;

}
