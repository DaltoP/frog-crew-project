package edu.tcu.cs.frogcrewbackend.game.dto;

import edu.tcu.cs.frogcrewbackend.game.Game;
import edu.tcu.cs.frogcrewbackend.game.GameStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GameDto {
    private Long id;
    private String opponent;
    private String venue;
    private LocalDateTime gameDateTime;
    private String sport;
    private GameStatus status;
    private String notes;
    private List<CrewAssignmentDto> crewAssignments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime reportDateTime;
    private String reportLocation;
    private List<String> requiredPositions;

    public static GameDto fromGame(Game game) {
        GameDto dto = new GameDto();
        dto.setId(game.getId());
        dto.setOpponent(game.getOpponent());
        dto.setVenue(game.getVenue());
        dto.setGameDateTime(game.getGameDateTime());
        dto.setSport(game.getSportType());
        dto.setStatus(game.getStatus());
        dto.setNotes(game.getNotes());
        dto.setCrewAssignments(game.getCrewAssignments().stream().map(CrewAssignmentDto::fromCrewAssignment).toList());
        dto.setCreatedAt(game.getCreatedAt());
        dto.setUpdatedAt(game.getUpdatedAt());
        dto.setReportDateTime(game.getReportDateTime());
        dto.setReportLocation(game.getReportLocation());
        dto.setRequiredPositions(game.getRequiredPositions());
        return dto;
    }

    public LocalDateTime getReportDateTime() {
        return reportDateTime;
    }

    public void setReportDateTime(LocalDateTime reportDateTime) {
        this.reportDateTime = reportDateTime;
    }

    public String getReportLocation() {
        return reportLocation;
    }

    public void setReportLocation(String reportLocation) {
        this.reportLocation = reportLocation;
    }

    public List<String> getRequiredPositions() {
        return requiredPositions;
    }

    public void setRequiredPositions(List<String> requiredPositions) {
        this.requiredPositions = requiredPositions;
    }
} 