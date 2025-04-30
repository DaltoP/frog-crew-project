package edu.tcu.cs.frogcrewbackend.availability;

import edu.tcu.cs.frogcrewbackend.CrewMember.CrewMember;
import edu.tcu.cs.frogcrewbackend.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByCrewMemberAndStartTimeBetweenOrderByStartTimeAsc(
            CrewMember crewMember, LocalDateTime start, LocalDateTime end);
    
    List<Availability> findByCrewMemberOrderByStartTimeAsc(CrewMember crewMember);
    
    boolean existsByCrewMemberAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            CrewMember crewMember, LocalDateTime endTime, LocalDateTime startTime);

    List<Availability> findByCrewMemberOrderByGame_GameDateTimeAsc(CrewMember crewMember);
    Optional<Availability> findByCrewMemberAndGame(CrewMember crewMember, Game game);
} 