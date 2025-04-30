package edu.tcu.cs.frogcrewbackend.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByGameDateTimeBetweenOrderByGameDateTimeAsc(LocalDateTime start, LocalDateTime end);
    List<Game> findByCrewAssignments_CrewMember_EmailOrderByGameDateTimeAsc(String email);
} 