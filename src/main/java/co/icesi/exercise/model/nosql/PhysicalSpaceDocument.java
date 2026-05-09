package co.icesi.exercise.model.nosql;

import lombok.Data;

@Data
public class PhysicalSpaceDocument {
    private String name;
    private String location;
    private Integer capacity;
}
