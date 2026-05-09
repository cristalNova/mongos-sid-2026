package co.icesi.exercise.model.nosql;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "events")
@Data
public class EventDocument {

    @Id
    private String id;
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private String description;
    private PhysicalSpaceDocument physicalSpace;
    private List<SubscriptionDocument> subscriptions = new ArrayList<>();
}
