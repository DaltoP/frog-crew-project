package edu.tcu.cs.frogcrewbackend.dto;

import edu.tcu.cs.frogcrewbackend.CrewMember.CrewMember;

public class UserSimpleDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String role;
    private String qualifiedPosition;

    public static UserSimpleDTO fromCrewMember(CrewMember crewMember) {
        UserSimpleDTO dto = new UserSimpleDTO();
        dto.setId(crewMember.getId());
        dto.setFirstName(crewMember.getFirstName());
        dto.setLastName(crewMember.getLastName());
        dto.setRole(crewMember.getRole());
        dto.setQualifiedPosition(crewMember.getQualifiedPosition());
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getQualifiedPosition() {
        return qualifiedPosition;
    }

    public void setQualifiedPosition(String qualifiedPosition) {
        this.qualifiedPosition = qualifiedPosition;
    }
} 