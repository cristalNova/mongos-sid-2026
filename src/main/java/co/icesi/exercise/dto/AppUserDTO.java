package co.icesi.exercise.dto;

import lombok.Data;

import java.util.List;

@Data
public class AppUserDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private int age;
    private double weight;
    private double height;

    private List<Integer> roleIds;

}
