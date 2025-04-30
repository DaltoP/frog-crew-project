package edu.tcu.cs.frogcrewbackend.game.dto;

import edu.tcu.cs.frogcrewbackend.CrewMember.CrewMember;
import edu.tcu.cs.frogcrewbackend.game.CrewAssignment;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CrewAssignmentDto {
    private Long id;
    private Long gameId;
    private Long crewMemberId;
    private String crewMemberName;
    private String position;
    private boolean isConfirmed;
    private LocalDateTime reportTime;
    private String reportLocation;

    public static CrewAssignmentDto fromCrewAssignment(CrewAssignment assignment) {
        CrewAssignmentDto dto = new CrewAssignmentDto();
        dto.setId(assignment.getId());
        dto.setGameId(assignment.getGame().getId());
        dto.setCrewMemberId(assignment.getCrewMember().getId());
        dto.setCrewMemberName(assignment.getCrewMember().getFirstName() + " " + assignment.getCrewMember().getLastName());
        dto.setPosition(assignment.getPosition());
        dto.setConfirmed(assignment.isConfirmed());
        dto.setReportTime(assignment.getReportTime());
        dto.setReportLocation(assignment.getReportLocation());
        return dto;
    }
} 