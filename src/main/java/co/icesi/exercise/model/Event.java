package co.icesi.exercise.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Entity
@Data
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private Date date;
    private String description;

    //location

    @ManyToOne
    @JoinColumn(name = "physicalSpace")
    private PhysicalSpace physicalSpace;

    //subscription

    @OneToMany(mappedBy = "event")
    private List<Subscription> subscriptions;



}
