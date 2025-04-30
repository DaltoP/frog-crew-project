package edu.tcu.cs.frogcrewbackend.availability.dto;

import edu.tcu.cs.frogcrewbackend.availability.Availability;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AvailabilityDto {
    private Long id;
    private Long crewMemberId;
    private String crewMemberName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isAvailable;
    private String notes;

    public static AvailabilityDto fromAvailability(Availability availability) {
        AvailabilityDto dto = new AvailabilityDto();
        dto.setId(availability.getId());
        dto.setCrewMemberId(availability.getCrewMember().getId());
        dto.setCrewMemberName(availability.getCrewMember().getFirstName() + " " + 
                            availability.getCrewMember().getLastName());
        dto.setStartTime(availability.getStartTime());
        dto.setEndTime(availability.getEndTime());
        dto.setAvailable(availability.isAvailable());
        dto.setNotes(availability.getNotes());
        return dto;
    }
} 