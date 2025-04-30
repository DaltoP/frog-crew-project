package edu.tcu.cs.frogcrewbackend.game;

import edu.tcu.cs.frogcrewbackend.CrewMember.CrewMember;
import edu.tcu.cs.frogcrewbackend.CrewMember.CrewMemberRepository;
import edu.tcu.cs.frogcrewbackend.game.dto.GameDto;
import edu.tcu.cs.frogcrewbackend.game.dto.CrewAssignmentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private CrewMemberRepository crewMemberRepository;

    @InjectMocks
    private GameService gameService;

    private Game game;
    private CrewMember crewMember;
    private CrewAssignment crewAssignment;

    @BeforeEach
    void setUp() {
        game = new Game();
        game.setId(1L);
        game.setOpponent("Opponent Team");
        game.setGameDateTime(LocalDateTime.now().plusDays(1));
        game.setVenue("Home Stadium");
        game.setSportType("Basketball");
        game.setStatus(GameStatus.PENDING);

        crewMember = new CrewMember();
        crewMember.setId(1L);
        crewMember.setEmail("test@example.com");

        crewAssignment = new CrewAssignment();
        crewAssignment.setId(1L);
        crewAssignment.setGame(game);
        crewAssignment.setCrewMember(crewMember);
        crewAssignment.setPosition("Referee");
        crewAssignment.setConfirmed(true);
        crewAssignment.setReportTime(LocalDateTime.now().plusHours(1));
        crewAssignment.setReportLocation("Main Entrance");

        Set<CrewAssignment> assignments = new HashSet<>();
        assignments.add(crewAssignment);
        game.setCrewAssignments(assignments);
    }

    @Test
    void whenGetUpcomingGames_thenReturnGames() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(1);
        when(gameRepository.findByGameDateTimeBetweenOrderByGameDateTimeAsc(startDate, endDate))
                .thenReturn(Arrays.asList(game));

        // Act
        List<GameDto> result = gameService.getUpcomingGames(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(game.getId(), result.get(0).getId());
        assertEquals(game.getOpponent(), result.get(0).getOpponent());
    }

    @Test
    void whenGetCrewMemberSchedule_thenReturnGames() {
        // Arrange
        String email = "test@example.com";
        when(gameRepository.findByCrewAssignments_CrewMember_EmailOrderByGameDateTimeAsc(email))
                .thenReturn(Arrays.asList(game));

        // Act
        List<GameDto> result = gameService.getCrewMemberSchedule(email);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(game.getId(), result.get(0).getId());
        assertEquals(game.getOpponent(), result.get(0).getOpponent());
    }

    @Test
    void whenGetGameDetails_withValidId_thenReturnGame() {
        // Arrange
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

        // Act
        GameDto result = gameService.getGameDetails(1L);

        // Assert
        assertNotNull(result);
        assertEquals(game.getId(), result.getId());
        assertEquals(game.getOpponent(), result.getOpponent());
        assertEquals(1, result.getCrewAssignments().size());
    }

    @Test
    void whenGetGameDetails_withInvalidId_thenThrowException() {
        // Arrange
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> gameService.getGameDetails(1L));
    }
} 