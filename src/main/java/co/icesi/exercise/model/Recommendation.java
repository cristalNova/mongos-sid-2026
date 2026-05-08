package co.icesi.exercise.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Entity
@Data
@Table(name = "recommendation")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String message;
    private Date date;

    @ManyToOne
    @JoinColumn(name = "sender")
    private AppUser sender;

    @ManyToOne
    @JoinColumn(name = "receiver")
    private AppUser receiver;

}
