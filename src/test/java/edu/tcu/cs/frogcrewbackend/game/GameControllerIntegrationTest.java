package edu.tcu.cs.frogcrewbackend.game;

import edu.tcu.cs.frogcrewbackend.CrewMember.CrewMember;
import edu.tcu.cs.frogcrewbackend.CrewMember.CrewMemberRepository;
import edu.tcu.cs.frogcrewbackend.game.dto.GameDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GameControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private CrewMemberRepository crewMemberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Game game;
    private LocalDateTime now;
    private CrewMember crewMember;
    private TestRestTemplate authenticatedTemplate;

    @BeforeEach
    void setUp() {
        // Clean up the database
        gameRepository.deleteAll();
        crewMemberRepository.deleteAll();

        now = LocalDateTime.now().plusDays(1); // Set to tomorrow

        // Create test crew member
        crewMember = new CrewMember();
        crewMember.setFirstName("John");
        crewMember.setLastName("Doe");
        crewMember.setEmail("john@example.com");
        crewMember.setPassword(passwordEncoder.encode("password123"));
        crewMember.setRole("CREW_MEMBER");
        crewMember.setQualifiedPosition("Camera Operator");
        crewMemberRepository.save(crewMember);

        // Create test game
        game = new Game();
        game.setOpponent("Test Team");
        game.setGameDateTime(now.plusDays(1)); // Set game to 2 days from now
        game.setVenue("Test Venue");
        game.setSportType("Basketball");
        game.setStatus(GameStatus.PENDING);
        gameRepository.save(game);

        // Create crew assignment
        CrewAssignment assignment = new CrewAssignment();
        assignment.setCrewMember(crewMember);
        assignment.setGame(game);
        assignment.setPosition("Camera Operator");
        assignment.setConfirmed(true);
        assignment.setReportTime(now.plusHours(1));
        assignment.setReportLocation("Main Entrance");
        game.getCrewAssignments().add(assignment);
        gameRepository.save(game);

        // Create authenticated template
        authenticatedTemplate = restTemplate.withBasicAuth("john@example.com", "password123");
    }

    @Test
    void whenGetMySchedule_thenReturnGames() {
        ResponseEntity<List<GameDto>> response = authenticatedTemplate.exchange(
                "/api/v1/games/my-schedule",
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<List<GameDto>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void whenGetUpcomingGames_thenReturnGames() {
        ResponseEntity<List<GameDto>> response = authenticatedTemplate.exchange(
                "/api/v1/games/upcoming",
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<List<GameDto>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(game.getSportType(), response.getBody().get(0).getSport());
    }

    @Test
    void whenGetGameDetails_withValidId_thenReturnGame() {
        ResponseEntity<GameDto> response = authenticatedTemplate.exchange(
                "/api/v1/games/" + game.getId(),
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                GameDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(game.getSportType(), response.getBody().getSport());
    }

    @Test
    void whenGetGameDetails_withInvalidId_thenReturn404() {
        ResponseEntity<GameDto> response = authenticatedTemplate.exchange(
                "/api/v1/games/999",
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                GameDto.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
} 