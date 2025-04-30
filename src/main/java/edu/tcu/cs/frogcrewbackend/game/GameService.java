package edu.tcu.cs.frogcrewbackend.game;

import edu.tcu.cs.frogcrewbackend.CrewMember.CrewMemberRepository;
import edu.tcu.cs.frogcrewbackend.game.dto.GameDto;
import edu.tcu.cs.frogcrewbackend.system.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GameService {
    private final GameRepository gameRepository;
    private final CrewMemberRepository crewMemberRepository;

    public GameService(GameRepository gameRepository, CrewMemberRepository crewMemberRepository) {
        this.gameRepository = gameRepository;
        this.crewMemberRepository = crewMemberRepository;
    }

    public List<GameDto> getCrewMemberSchedule(String email) {
        return gameRepository.findByCrewAssignments_CrewMember_EmailOrderByGameDateTimeAsc(email)
                .stream()
                .map(GameDto::fromGame)
                .collect(Collectors.toList());
    }

    public List<GameDto> getUpcomingGames(LocalDateTime start, LocalDateTime end) {
        return gameRepository.findByGameDateTimeBetweenOrderByGameDateTimeAsc(start, end)
                .stream()
                .map(GameDto::fromGame)
                .collect(Collectors.toList());
    }

    public GameDto getGameDetails(Long gameId) {
        return gameRepository.findById(gameId)
                .map(GameDto::fromGame)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));
    }

    public GameDto createGame(GameDto gameDto) {
        Game game = new Game();
        updateGameFromDto(game, gameDto);
        game.setCreatedAt(LocalDateTime.now());
        game.setUpdatedAt(LocalDateTime.now());
        return GameDto.fromGame(gameRepository.save(game));
    }

    public GameDto updateGame(Long gameId, GameDto gameDto) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));
        updateGameFromDto(game, gameDto);
        return GameDto.fromGame(gameRepository.save(game));
    }

    public GameDto publishGame(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));
        game.setStatus(GameStatus.CONFIRMED);
        game.setUpdatedAt(LocalDateTime.now());
        return GameDto.fromGame(gameRepository.save(game));
    }

    public GameDto updateGameStatus(Long gameId, GameStatus status) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));
        game.setStatus(status);
        game.setUpdatedAt(LocalDateTime.now());
        return GameDto.fromGame(gameRepository.save(game));
    }

    public GameDto updateGameNotes(Long gameId, String notes) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));
        game.setNotes(notes);
        game.setUpdatedAt(LocalDateTime.now());
        return GameDto.fromGame(gameRepository.save(game));
    }

    private void updateGameFromDto(Game game, GameDto gameDto) {
        game.setOpponent(gameDto.getOpponent());
        game.setGameDateTime(gameDto.getGameDateTime());
        game.setVenue(gameDto.getVenue());
        game.setReportDateTime(gameDto.getReportDateTime());
        game.setReportLocation(gameDto.getReportLocation());
        game.setStatus(gameDto.getStatus());
        game.setRequiredPositions(gameDto.getRequiredPositions());
        game.setNotes(gameDto.getNotes());
        game.setUpdatedAt(LocalDateTime.now());
    }
} 