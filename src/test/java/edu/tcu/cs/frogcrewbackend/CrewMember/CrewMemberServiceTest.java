package edu.tcu.cs.frogcrewbackend.CrewMember;

import edu.tcu.cs.frogcrewbackend.dto.UserDTO;
import edu.tcu.cs.frogcrewbackend.dto.UserSimpleDTO;
import edu.tcu.cs.frogcrewbackend.dto.CrewedUserDTO;
import edu.tcu.cs.frogcrewbackend.system.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class CrewMemberServiceTest {

    @Mock
    private CrewMemberRepository crewMemberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CrewMemberService crewMemberService;

    private UserDTO registrationRequest;
    private CrewMember crewMember;
    private CrewMember adminUser;
    private CrewMember regularUser;

    @BeforeEach
    void setUp() {
        // Prepare test data
        registrationRequest = new UserDTO();
        registrationRequest.setFirstName("John");
        registrationRequest.setLastName("Doe");
        registrationRequest.setEmail("john.doe@example.com");
        registrationRequest.setPhoneNumber("+11234567890");
        registrationRequest.setPassword("password123");
        registrationRequest.setRole("USER");
        registrationRequest.setQualifiedPosition("Camera Operator");

        crewMember = new CrewMember();
        crewMember.setId(1L);
        crewMember.setFirstName("John");
        crewMember.setLastName("Doe");
        crewMember.setEmail("john.doe@example.com");
        crewMember.setPhoneNumber("+11234567890");
        crewMember.setPassword("hashedPassword");
        crewMember.setRole("USER");
        crewMember.setQualifiedPosition("Camera Operator");

        // Create admin user
        adminUser = new CrewMember();
        adminUser.setId(2L);
        adminUser.setEmail("admin@example.com");
        adminUser.setRole("ADMIN");

        // Create regular user
        regularUser = new CrewMember();
        regularUser.setId(3L);
        regularUser.setEmail("regular@example.com");
        regularUser.setRole("USER");
    }

    @Test
    void whenValidRegistrationRequest_thenCrewMemberShouldBeSaved() {
        // Given
        when(crewMemberRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(crewMemberRepository.save(any(CrewMember.class))).thenReturn(crewMember);

        // When
        CrewedUserDTO savedCrewMember = crewMemberService.registerCrewMember(registrationRequest);

        // Then
        assertNotNull(savedCrewMember);
        assertEquals("John", savedCrewMember.getFirstName());
        assertEquals("Doe", savedCrewMember.getLastName());
        assertEquals("john.doe@example.com", savedCrewMember.getEmail());
        assertEquals("+11234567890", savedCrewMember.getPhoneNumber());
        assertEquals("USER", savedCrewMember.getRole());
        assertEquals("Camera Operator", savedCrewMember.getQualifiedPosition());
    }

    @Test
    void whenEmailAlreadyExists_thenThrowException() {
        // Given
        when(crewMemberRepository.existsByEmail(anyString())).thenReturn(true);

        // When/Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            crewMemberService.registerCrewMember(registrationRequest);
        });

        assertEquals("Email already registered", exception.getMessage());
    }

    @Test
    void whenGetAllCrewMembers_asAdmin_thenReturnFullProfileList() {
        // Given
        when(crewMemberRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(crewMemberRepository.findAll()).thenReturn(Arrays.asList(crewMember));

        // When
        List profiles = crewMemberService.getAllCrewMembers("admin@example.com");

        // Then
        assertNotNull(profiles);
        assertEquals(1, profiles.size());
        assertTrue(profiles.get(0) instanceof CrewedUserDTO);
        CrewedUserDTO profile = (CrewedUserDTO) profiles.get(0);
        assertEquals(crewMember.getEmail(), profile.getEmail()); // Admin can see sensitive data
        assertEquals(crewMember.getPhoneNumber(), profile.getPhoneNumber()); // Admin can see sensitive data
    }

    @Test
    void whenGetAllCrewMembers_asRegularUser_thenReturnLimitedProfileList() {
        // Given
        when(crewMemberRepository.findByEmail("regular@example.com")).thenReturn(Optional.of(regularUser));
        when(crewMemberRepository.findAll()).thenReturn(Arrays.asList(crewMember));

        // When
        List profiles = crewMemberService.getAllCrewMembers("regular@example.com");

        // Then
        assertNotNull(profiles);
        assertEquals(1, profiles.size());
        assertTrue(profiles.get(0) instanceof UserSimpleDTO);
        UserSimpleDTO profile = (UserSimpleDTO) profiles.get(0);
        assertEquals(crewMember.getFirstName(), profile.getFirstName());
        assertEquals(crewMember.getLastName(), profile.getLastName());
        assertEquals(crewMember.getRole(), profile.getRole());
        assertEquals(crewMember.getQualifiedPosition(), profile.getQualifiedPosition());
    }

    @Test
    void whenGetCrewMemberProfile_asAdmin_thenReturnFullProfile() {
        // Given
        when(crewMemberRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(crewMemberRepository.findById(1L)).thenReturn(Optional.of(crewMember));

        // When
        Object profile = crewMemberService.getCrewMemberProfile(1L, "admin@example.com");

        // Then
        assertNotNull(profile);
        assertTrue(profile instanceof CrewedUserDTO);
        CrewedUserDTO crewedProfile = (CrewedUserDTO) profile;
        assertEquals(crewMember.getEmail(), crewedProfile.getEmail()); // Admin can see sensitive data
        assertEquals(crewMember.getPhoneNumber(), crewedProfile.getPhoneNumber()); // Admin can see sensitive data
    }

    @Test
    void whenGetCrewMemberProfile_asRegularUser_thenReturnLimitedProfile() {
        // Given
        when(crewMemberRepository.findByEmail("regular@example.com")).thenReturn(Optional.of(regularUser));
        when(crewMemberRepository.findById(1L)).thenReturn(Optional.of(crewMember));

        // When
        Object profile = crewMemberService.getCrewMemberProfile(1L, "regular@example.com");

        // Then
        assertNotNull(profile);
        assertTrue(profile instanceof UserSimpleDTO);
        UserSimpleDTO simpleProfile = (UserSimpleDTO) profile;
        assertEquals(crewMember.getFirstName(), simpleProfile.getFirstName());
        assertEquals(crewMember.getLastName(), simpleProfile.getLastName());
        assertEquals(crewMember.getRole(), simpleProfile.getRole());
        assertEquals(crewMember.getQualifiedPosition(), simpleProfile.getQualifiedPosition());
    }

    @Test
    void whenGetCrewMemberProfile_withInvalidId_thenThrowException() {
        // Given
        when(crewMemberRepository.findByEmail("regular@example.com")).thenReturn(Optional.of(regularUser));
        when(crewMemberRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> {
            crewMemberService.getCrewMemberProfile(999L, "regular@example.com");
        });
    }

    @Test
    void whenGetCrewMemberProfile_withInvalidUser_thenThrowException() {
        // Given
        when(crewMemberRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> {
            crewMemberService.getCrewMemberProfile(1L, "invalid@example.com");
        });
    }
} 