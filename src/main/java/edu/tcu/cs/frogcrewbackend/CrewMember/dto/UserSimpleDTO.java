package edu.tcu.cs.frogcrewbackend.CrewMember.dto;

import lombok.Data;

@Data
public class UserSimpleDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String role;
    private String qualifiedPosition;
} 