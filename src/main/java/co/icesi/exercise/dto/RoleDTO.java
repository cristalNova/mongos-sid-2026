package co.icesi.exercise.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleDTO {

    private String name;
    private List<Integer> permissionIds;

}
