package edu.tcu.cs.frogcrewbackend.game.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledGameDto {
    private Long gameId;
    private String sportType;
    private String opponent;
    private LocalDateTime gameDateTime;
    private String venue;
    private LocalDateTime reportDateTime;
    private String reportLocation;
    private String position;
    private boolean isConfirmed;
    private String status;
} 