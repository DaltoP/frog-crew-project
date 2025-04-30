package edu.tcu.cs.frogcrewbackend.availability.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityRequest {
    @NotNull(message = "Game ID is required")
    private Long gameId;
    
    @NotNull(message = "Availability status is required")
    private boolean isAvailable;
    
    private String comment;
} 