package edu.tcu.cs.frogcrewbackend.availability;

import edu.tcu.cs.frogcrewbackend.CrewMember.CrewMember;
import edu.tcu.cs.frogcrewbackend.CrewMember.CrewMemberRepository;
import edu.tcu.cs.frogcrewbackend.availability.dto.AvailabilityDto;
import edu.tcu.cs.frogcrewbackend.system.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private CrewMemberRepository crewMemberRepository;

    @InjectMocks
    private AvailabilityService availabilityService;

    private CrewMember crewMember;
    private Availability availability;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        crewMember = new CrewMember();
        crewMember.setId(1L);
        crewMember.setEmail("test@example.com");
        crewMember.setFirstName("John");
        crewMember.setLastName("Doe");

        availability = new Availability();
        availability.setId(1L);
        availability.setCrewMember(crewMember);
        availability.setStartTime(now.plusDays(1));
        availability.setEndTime(now.plusDays(1).plusHours(4));
        availability.setAvailable(true);
        availability.setNotes("Available for game");
    }

    @Test
    void whenGetCrewMemberAvailability_thenReturnAvailabilityList() {
        when(crewMemberRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(crewMember));
        when(availabilityRepository.findByCrewMemberOrderByStartTimeAsc(crewMember))
                .thenReturn(Arrays.asList(availability));

        List<AvailabilityDto> result = availabilityService.getCrewMemberAvailability("test@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(availability.getId(), result.get(0).getId());
        assertEquals(availability.isAvailable(), result.get(0).isAvailable());
    }

    @Test
    void whenSubmitAvailability_thenReturnSavedAvailability() {
        when(crewMemberRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(crewMember));
        when(availabilityRepository.save(any(Availability.class)))
                .thenReturn(availability);

        AvailabilityDto result = availabilityService.submitAvailability(
                "test@example.com",
                now.plusDays(1),
                now.plusDays(1).plusHours(4),
                "Available for game"
        );

        assertNotNull(result);
        assertEquals(availability.getId(), result.getId());
        assertEquals(availability.isAvailable(), result.isAvailable());
    }

    @Test
    void whenUpdateAvailability_thenReturnUpdatedAvailability() {
        when(crewMemberRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(crewMember));
        when(availabilityRepository.findById(1L))
                .thenReturn(Optional.of(availability));
        when(availabilityRepository.save(any(Availability.class)))
                .thenReturn(availability);

        AvailabilityDto result = availabilityService.updateAvailability(
                "test@example.com",
                1L,
                now.plusDays(2),
                now.plusDays(2).plusHours(4),
                "Updated availability"
        );

        assertNotNull(result);
        assertEquals(availability.getId(), result.getId());
        assertEquals(availability.isAvailable(), result.isAvailable());
    }

    @Test
    void whenUpdateAvailability_withWrongOwner_thenThrowException() {
        CrewMember otherCrewMember = new CrewMember();
        otherCrewMember.setId(2L);
        otherCrewMember.setEmail("other@example.com");

        when(crewMemberRepository.findByEmail("other@example.com"))
                .thenReturn(Optional.of(otherCrewMember));
        when(availabilityRepository.findById(1L))
                .thenReturn(Optional.of(availability));

        assertThrows(IllegalStateException.class, () -> {
            availabilityService.updateAvailability(
                    "other@example.com",
                    1L,
                    now.plusDays(2),
                    now.plusDays(2).plusHours(4),
                    "Updated availability"
            );
        });
    }

    @Test
    void whenGetCrewMemberAvailability_withInvalidEmail_thenThrowException() {
        when(crewMemberRepository.findByEmail("invalid@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            availabilityService.getCrewMemberAvailability("invalid@example.com");
        });
    }
} 