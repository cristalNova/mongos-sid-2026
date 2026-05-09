package co.icesi.exercise.model.nosql;

import lombok.Data;

@Data
public class SubscriptionDocument {
    private Integer userId;
    private String userFirstName;
    private String userLastName;
    private Boolean attendance;
}
