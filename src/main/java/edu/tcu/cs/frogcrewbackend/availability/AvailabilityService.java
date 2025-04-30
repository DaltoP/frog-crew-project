package edu.tcu.cs.frogcrewbackend.availability;

import edu.tcu.cs.frogcrewbackend.CrewMember.CrewMember;
import edu.tcu.cs.frogcrewbackend.CrewMember.CrewMemberRepository;
import edu.tcu.cs.frogcrewbackend.availability.dto.AvailabilityDto;
import edu.tcu.cs.frogcrewbackend.availability.dto.AvailabilityRequest;
import edu.tcu.cs.frogcrewbackend.game.Game;
import edu.tcu.cs.frogcrewbackend.game.GameRepository;
import edu.tcu.cs.frogcrewbackend.system.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AvailabilityService {
    private final AvailabilityRepository availabilityRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final GameRepository gameRepository;

    public AvailabilityService(AvailabilityRepository availabilityRepository,
                             CrewMemberRepository crewMemberRepository,
                             GameRepository gameRepository) {
        this.availabilityRepository = availabilityRepository;
        this.crewMemberRepository = crewMemberRepository;
        this.gameRepository = gameRepository;
    }

    public List<AvailabilityDto> getCrewMemberAvailability(String email) {
        CrewMember crewMember = crewMemberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Crew member not found"));

        return availabilityRepository.findByCrewMemberOrderByStartTimeAsc(crewMember)
                .stream()
                .map(AvailabilityDto::fromAvailability)
                .collect(Collectors.toList());
    }

    public AvailabilityDto submitAvailability(String email, LocalDateTime startTime,
                                           LocalDateTime endTime, String notes) {
        CrewMember crewMember = crewMemberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Crew member not found"));

        // Check for overlapping availabilities
        if (availabilityRepository.existsByCrewMemberAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                crewMember, endTime, startTime)) {
            throw new IllegalArgumentException("Availability overlaps with existing time slot");
        }

        Availability availability = new Availability();
        availability.setCrewMember(crewMember);
        availability.setStartTime(startTime);
        availability.setEndTime(endTime);
        availability.setAvailable(true);
        availability.setNotes(notes);

        return AvailabilityDto.fromAvailability(availabilityRepository.save(availability));
    }

    public AvailabilityDto updateAvailability(String email, Long availabilityId,
                                           LocalDateTime startTime, LocalDateTime endTime,
                                           String notes) {
        CrewMember crewMember = crewMemberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Crew member not found"));

        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found"));

        // Verify ownership
        if (!availability.getCrewMember().getId().equals(crewMember.getId())) {
            throw new IllegalStateException("You can only update your own availability");
        }

        // Check for overlapping availabilities (excluding current availability)
        if (availabilityRepository.existsByCrewMemberAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                crewMember, endTime, startTime)) {
            throw new IllegalArgumentException("Availability overlaps with existing time slot");
        }

        availability.setStartTime(startTime);
        availability.setEndTime(endTime);
        availability.setNotes(notes);

        return AvailabilityDto.fromAvailability(availabilityRepository.save(availability));
    }

    public List<AvailabilityDto> getCrewMemberAvailability(Long crewMemberId) {
        CrewMember crewMember = crewMemberRepository.findById(crewMemberId)
                .orElseThrow(() -> new ResourceNotFoundException("Crew member not found"));

        return availabilityRepository.findByCrewMemberOrderByStartTimeAsc(crewMember)
                .stream()
                .map(AvailabilityDto::fromAvailability)
                .collect(Collectors.toList());
    }

    public List<AvailabilityDto> getCrewMemberAvailabilityInRange(Long crewMemberId,
                                                                LocalDateTime start,
                                                                LocalDateTime end) {
        CrewMember crewMember = crewMemberRepository.findById(crewMemberId)
                .orElseThrow(() -> new ResourceNotFoundException("Crew member not found"));

        return availabilityRepository.findByCrewMemberAndStartTimeBetweenOrderByStartTimeAsc(
                        crewMember, start, end)
                .stream()
                .map(AvailabilityDto::fromAvailability)
                .collect(Collectors.toList());
    }
} 