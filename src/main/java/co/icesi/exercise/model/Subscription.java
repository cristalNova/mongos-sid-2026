package co.icesi.exercise.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "subscription")
@Data
public class Subscription {

    @EmbeddedId
    private SubscriptionPK subscriptionId;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "userId")
    private AppUser user;

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "eventId")
    private Event event;

    private Boolean attendance;
}
