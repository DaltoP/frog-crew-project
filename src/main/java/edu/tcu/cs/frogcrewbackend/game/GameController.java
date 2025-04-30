package edu.tcu.cs.frogcrewbackend.game;

import edu.tcu.cs.frogcrewbackend.game.dto.GameDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/my-schedule")
    public ResponseEntity<List<GameDto>> getMySchedule() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        String email = authentication.getName();
        return ResponseEntity.ok(gameService.getCrewMemberSchedule(email));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<GameDto>> getUpcomingGames(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : LocalDateTime.now();
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : LocalDateTime.now().plusMonths(1);
        return ResponseEntity.ok(gameService.getUpcomingGames(start, end));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameDto> getGameDetails(@PathVariable Long gameId) {
        GameDto game = gameService.getGameDetails(gameId);
        return ResponseEntity.ok(game);
    }

    @PostMapping
    public ResponseEntity<GameDto> createGame(@RequestBody GameDto gameDto) {
        return ResponseEntity.ok(gameService.createGame(gameDto));
    }

    @PutMapping("/{gameId}")
    public ResponseEntity<GameDto> updateGame(@PathVariable Long gameId, @RequestBody GameDto gameDto) {
        return ResponseEntity.ok(gameService.updateGame(gameId, gameDto));
    }

    @PostMapping("/{gameId}/publish")
    public ResponseEntity<GameDto> publishGame(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.publishGame(gameId));
    }

    @PutMapping("/{gameId}/status")
    public ResponseEntity<GameDto> updateGameStatus(@PathVariable Long gameId, @RequestParam String status) {
        try {
            GameStatus gameStatus = GameStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(gameService.updateGameStatus(gameId, gameStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{gameId}/notes")
    public ResponseEntity<GameDto> updateGameNotes(@PathVariable Long gameId, @RequestParam String notes) {
        return ResponseEntity.ok(gameService.updateGameNotes(gameId, notes));
    }
} 