package edu.tcu.cs.frogcrewbackend.CrewMember;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class CrewMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "First name is required")
    private String firstName;

    @NotEmpty(message = "Last name is required")
    private String lastName;

    @Email(message = "Please provide a valid email address")
    @NotEmpty(message = "Email is required")
    @Column(unique = true)
    private String email;

    @NotEmpty(message = "Password is required")
    private String password;

    @NotEmpty(message = "Role is required")
    private String role;

    @NotEmpty(message = "Qualified position is required")
    private String qualifiedPosition;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Please provide a valid phone number")
    private String phoneNumber;

    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    @Enumerated(EnumType.STRING)
    private CrewMemberStatus status = CrewMemberStatus.ACTIVE;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 