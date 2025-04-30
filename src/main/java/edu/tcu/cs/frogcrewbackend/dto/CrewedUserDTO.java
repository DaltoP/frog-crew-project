package edu.tcu.cs.frogcrewbackend.dto;

import edu.tcu.cs.frogcrewbackend.CrewMember.CrewMember;

public class CrewedUserDTO extends UserSimpleDTO {
    private String email;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    public static CrewedUserDTO fromCrewMember(CrewMember crewMember) {
        CrewedUserDTO dto = new CrewedUserDTO();
        dto.setId(crewMember.getId());
        dto.setFirstName(crewMember.getFirstName());
        dto.setLastName(crewMember.getLastName());
        dto.setEmail(crewMember.getEmail());
        dto.setPhoneNumber(crewMember.getPhoneNumber());
        dto.setRole(crewMember.getRole());
        dto.setQualifiedPosition(crewMember.getQualifiedPosition());
        dto.setAddress(crewMember.getAddress());
        dto.setCity(crewMember.getCity());
        dto.setState(crewMember.getState());
        dto.setZipCode(crewMember.getZipCode());
        dto.setCountry(crewMember.getCountry());
        return dto;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
} 