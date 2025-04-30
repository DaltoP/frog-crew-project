package edu.tcu.cs.frogcrewbackend.CrewMember;

import edu.tcu.cs.frogcrewbackend.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class
CrewMemberIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CrewMemberRepository crewMemberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserDTO registrationRequest;
    private TestRestTemplate adminTemplate;
    private TestRestTemplate regularUserTemplate;

    @BeforeEach
    void setUp() {
        // Clean up the database before each test
        crewMemberRepository.deleteAll();

        // Create admin user
        CrewMember admin = new CrewMember();
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole("ADMIN");
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setPhoneNumber("+11234567890");
        admin.setQualifiedPosition("Administrator");
        crewMemberRepository.save(admin);

        // Create regular user
        CrewMember regularUser = new CrewMember();
        regularUser.setEmail("user@example.com");
        regularUser.setPassword(passwordEncoder.encode("password"));
        regularUser.setRole("USER");
        regularUser.setFirstName("Regular");
        regularUser.setLastName("User");
        regularUser.setPhoneNumber("+19876543210");
        regularUser.setQualifiedPosition("Camera Operator");
        crewMemberRepository.save(regularUser);

        // Prepare test data
        registrationRequest = new UserDTO();
        registrationRequest.setFirstName("John");
        registrationRequest.setLastName("Doe");
        registrationRequest.setEmail("john.doe@example.com");
        registrationRequest.setPhoneNumber("+11234567890");
        registrationRequest.setPassword("password123");
        registrationRequest.setRole("USER");
        registrationRequest.setQualifiedPosition("Camera Operator");

        // Create authenticated TestRestTemplate instances
        adminTemplate = restTemplate.withBasicAuth("admin@example.com", "admin123");
        regularUserTemplate = restTemplate.withBasicAuth("user@example.com", "password");
    }

    @Test
    void whenValidInput_thenCreateCrewMember() {
        // When
        ResponseEntity<CrewMember> response = restTemplate.postForEntity(
                "/api/v1/crew-members/register",
                registrationRequest,
                CrewMember.class
        );

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("Doe", response.getBody().getLastName());
        assertEquals("john.doe@example.com", response.getBody().getEmail());
        assertEquals("+11234567890", response.getBody().getPhoneNumber());
        assertEquals("USER", response.getBody().getRole());
        assertEquals("Camera Operator", response.getBody().getQualifiedPosition());
    }

    @Test
    void whenDuplicateEmail_thenReturn400() {
        // Given
        restTemplate.postForEntity("/api/v1/crew-members/register", registrationRequest, CrewMember.class);

        // When
        ResponseEntity<CrewMember> response = restTemplate.postForEntity(
                "/api/v1/crew-members/register",
                registrationRequest,
                CrewMember.class
        );

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void whenInvalidInput_thenReturn400() {
        // Given
        registrationRequest.setEmail("invalid-email"); // Invalid email format

        // When
        ResponseEntity<CrewMember> response = restTemplate.postForEntity(
                "/api/v1/crew-members/register",
                registrationRequest,
                CrewMember.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void whenGetAllCrewMembers_asAdmin_thenReturnFullProfiles() {
        // Given
        restTemplate.postForEntity("/api/v1/crew-members/register", registrationRequest, CrewMember.class);

        // When
        ResponseEntity<List<CrewMember>> response = adminTemplate.exchange(
                "/api/v1/crew-members",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CrewMember>>() {}
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<CrewMember> profiles = response.getBody();
        assertNotNull(profiles);
        assertTrue(profiles.size() >= 1);
        assertNotNull(profiles.get(0).getEmail());
        assertNotNull(profiles.get(0).getPhoneNumber());
    }

    @Test
    void whenGetAllCrewMembers_asRegularUser_thenReturnLimitedProfiles() {
        // Given
        restTemplate.postForEntity("/api/v1/crew-members/register", registrationRequest, CrewMember.class);

        // When
        ResponseEntity<List<CrewMember>> response = regularUserTemplate.exchange(
                "/api/v1/crew-members",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CrewMember>>() {}
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<CrewMember> profiles = response.getBody();
        assertNotNull(profiles);
        assertTrue(profiles.size() >= 1);
        assertNull(profiles.get(0).getEmail());
        assertNull(profiles.get(0).getPhoneNumber());
    }

    @Test
    void whenGetCrewMemberProfile_asAdmin_thenReturnFullProfile() {
        // Given
        ResponseEntity<CrewMember> createResponse = restTemplate.postForEntity(
                "/api/v1/crew-members/register",
                registrationRequest,
                CrewMember.class
        );
        Long createdId = createResponse.getBody().getId();

        // When
        ResponseEntity<CrewMember> response = adminTemplate.getForEntity(
                "/api/v1/crew-members/" + createdId,
                CrewMember.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getEmail());
        assertNotNull(response.getBody().getPhoneNumber());
    }

    @Test
    void whenGetCrewMemberProfile_asRegularUser_thenReturnLimitedProfile() {
        // Given
        ResponseEntity<CrewMember> createResponse = restTemplate.postForEntity(
                "/api/v1/crew-members/register",
                registrationRequest,
                CrewMember.class
        );
        Long createdId = createResponse.getBody().getId();

        // When
        ResponseEntity<CrewMember> response = regularUserTemplate.getForEntity(
                "/api/v1/crew-members/" + createdId,
                CrewMember.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getEmail());
        assertNull(response.getBody().getPhoneNumber());
    }

    @Test
    void whenGetCrewMemberProfile_withInvalidId_thenReturn404() {
        // When
        ResponseEntity<CrewMember> response = adminTemplate.getForEntity(
                "/api/v1/crew-members/999",
                CrewMember.class
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
} 