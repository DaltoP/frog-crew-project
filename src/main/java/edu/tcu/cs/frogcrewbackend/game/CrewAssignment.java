package edu.tcu.cs.frogcrewbackend.game;

import edu.tcu.cs.frogcrewbackend.CrewMember.CrewMember;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "crew_assignments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"game_id", "crew_member_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrewAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne
    @JoinColumn(name = "crew_member_id", nullable = false)
    private CrewMember crewMember;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private boolean isConfirmed = false;

    @Column(nullable = false)
    private LocalDateTime reportTime;

    @Column(nullable = false)
    private String reportLocation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrewAssignment that = (CrewAssignment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 