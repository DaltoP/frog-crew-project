package edu.tcu.cs.frogcrewbackend.CrewMember;

import edu.tcu.cs.frogcrewbackend.CrewMember.dto.UserSimpleDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class CrewListControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CrewMemberRepository crewMemberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private CrewMember crewMember1;
    private CrewMember crewMember2;
    private TestRestTemplate authenticatedTemplate;

    @BeforeEach
    void setUp() {
        // Clean up the database
        crewMemberRepository.deleteAll();

        // Create test crew members
        crewMember1 = new CrewMember();
        crewMember1.setFirstName("John");
        crewMember1.setLastName("Doe");
        crewMember1.setEmail("john@example.com");
        crewMember1.setPassword(passwordEncoder.encode("password123"));
        crewMember1.setRole("ROLE_ADMIN");
        crewMember1.setQualifiedPosition("Camera Operator");
        crewMemberRepository.save(crewMember1);

        crewMember2 = new CrewMember();
        crewMember2.setFirstName("Jane");
        crewMember2.setLastName("Smith");
        crewMember2.setEmail("jane@example.com");
        crewMember2.setPassword(passwordEncoder.encode("password123"));
        crewMember2.setRole("ROLE_CREW_MEMBER");
        crewMember2.setQualifiedPosition("Audio Technician");
        crewMemberRepository.save(crewMember2);

        // Create authenticated template with admin credentials
        authenticatedTemplate = restTemplate.withBasicAuth("john@example.com", "password123");
    }

    @Test
    void whenGetAllCrewMembers_thenReturnAllCrewMembers() {
        ResponseEntity<List<UserSimpleDTO>> response = authenticatedTemplate.exchange(
                "/api/v1/crew-members",
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<List<UserSimpleDTO>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void whenGetCrewMembersByPosition_thenReturnFilteredCrewMembers() {
        ResponseEntity<List<UserSimpleDTO>> response = authenticatedTemplate.exchange(
                "/api/v1/crew-members/position/Camera%20Operator",
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<List<UserSimpleDTO>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("John", response.getBody().get(0).getFirstName());
        assertEquals("Doe", response.getBody().get(0).getLastName());
        assertEquals("Camera Operator", response.getBody().get(0).getQualifiedPosition());
    }
} 