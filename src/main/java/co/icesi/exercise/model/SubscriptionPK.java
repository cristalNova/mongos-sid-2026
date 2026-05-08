package co.icesi.exercise.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPK {

    @Column(name = "userId")
    private int userId;
    @Column(name = "eventId")
    private int eventId;
}
