package edu.tcu.cs.frogcrewbackend.game;

import edu.tcu.cs.frogcrewbackend.CrewMember.CrewMember;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Sport type is required")
    @Column(nullable = false)
    private String sportType;

    @NotBlank(message = "Opponent is required")
    @Column(nullable = false)
    private String opponent;

    @NotNull(message = "Game date and time is required")
    @Column(nullable = false)
    private LocalDateTime gameDateTime;

    @NotBlank(message = "Venue is required")
    @Column(nullable = false)
    private String venue;

    @Column(name = "report_date_time")
    private LocalDateTime reportDateTime;

    @Column(name = "report_location")
    private String reportLocation;

    @Column(nullable = false)
    private boolean isPublished = false;

    // Status can be: SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    @NotNull(message = "Game status is required")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.PENDING;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CrewAssignment> crewAssignments = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "game_required_positions", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "position")
    private List<String> requiredPositions;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (gameDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Game date and time must be in the future");
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (gameDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Game date and time must be in the future");
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(id, game.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 