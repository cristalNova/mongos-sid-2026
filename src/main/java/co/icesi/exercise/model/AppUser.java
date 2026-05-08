package co.icesi.exercise.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    @Column(name = "password_hash")
    private String passwordHash;
    private int age;
    private double weight;
    private double height;

    //role relationship

    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "roleId")
    )

    private List<Role> roles;

    //user-trainer relationship

    @ManyToMany
    @JoinTable(
            name = "user_trainer",
            joinColumns = @JoinColumn(name = "userId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "trainerId", referencedColumnName = "id")
    )
    private List<AppUser> trainers;


    @JsonIgnore
    @ManyToMany(mappedBy = "trainers")
    private List<AppUser> users;

    //recommendation relationship
    @OneToMany(mappedBy = "sender")
    private List<Recommendation> recommendationsSent;

    @OneToMany(mappedBy = "receiver")
    private List<Recommendation> recommendationsReceived;

    //subscription
    @OneToMany(mappedBy = "user")
    private List<Subscription> subscriptions;
}
