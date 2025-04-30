package edu.tcu.cs.frogcrewbackend.availability;

import edu.tcu.cs.frogcrewbackend.availability.dto.AvailabilityDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/availability")
public class AvailabilityController {
    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping
    public ResponseEntity<List<AvailabilityDto>> getCrewMemberAvailability(Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        List<AvailabilityDto> availability = availabilityService.getCrewMemberAvailability(email);
        return new ResponseEntity<>(availability, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AvailabilityDto> submitAvailability(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) String notes) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(availabilityService.submitAvailability(
                email, startTime, endTime, notes));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvailabilityDto> updateAvailability(
            @PathVariable Long id,
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) String notes) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(availabilityService.updateAvailability(
                email, id, startTime, endTime, notes));
    }

    @GetMapping("/crew-member/{crewMemberId}")
    public ResponseEntity<List<AvailabilityDto>> getCrewMemberAvailability(
            @PathVariable Long crewMemberId) {
        return ResponseEntity.ok(availabilityService.getCrewMemberAvailability(crewMemberId));
    }

    @GetMapping("/crew-member/{crewMemberId}/range")
    public ResponseEntity<List<AvailabilityDto>> getCrewMemberAvailabilityInRange(
            @PathVariable Long crewMemberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(availabilityService.getCrewMemberAvailabilityInRange(
                crewMemberId, start, end));
    }
} 