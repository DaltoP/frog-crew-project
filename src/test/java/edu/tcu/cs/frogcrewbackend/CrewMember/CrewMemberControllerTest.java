package edu.tcu.cs.frogcrewbackend.CrewMember;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tcu.cs.frogcrewbackend.dto.UserDTO;
import edu.tcu.cs.frogcrewbackend.dto.UserSimpleDTO;
import edu.tcu.cs.frogcrewbackend.dto.CrewedUserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest(CrewMemberController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings({"unchecked", "rawtypes"})
class CrewMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CrewMemberService crewMemberService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private CrewMember crewMember;
    private UserDTO registrationRequest;
    private UserSimpleDTO regularProfileView;
    private CrewedUserDTO adminProfileView;

    @BeforeEach
    void setUp() {
        // Prepare test data
        crewMember = new CrewMember();
        crewMember.setId(1L);
        crewMember.setFirstName("John");
        crewMember.setLastName("Doe");
        crewMember.setEmail("john@example.com");
        crewMember.setPassword("password123");
        crewMember.setPhoneNumber("+11234567890");
        crewMember.setQualifiedPosition("Engineer");
        crewMember.setRole("USER");

        registrationRequest = new UserDTO();
        registrationRequest.setFirstName("John");
        registrationRequest.setLastName("Doe");
        registrationRequest.setEmail("john@example.com");
        registrationRequest.setPassword("password123");
        registrationRequest.setPhoneNumber("+11234567890");
        registrationRequest.setQualifiedPosition("Engineer");
        registrationRequest.setRole("USER");

        regularProfileView = new UserSimpleDTO();
        regularProfileView.setId(1L);
        regularProfileView.setFirstName("John");
        regularProfileView.setLastName("Doe");
        regularProfileView.setQualifiedPosition("Engineer");
        regularProfileView.setRole("USER");

        adminProfileView = new CrewedUserDTO();
        adminProfileView.setId(1L);
        adminProfileView.setFirstName("John");
        adminProfileView.setLastName("Doe");
        adminProfileView.setEmail("john@example.com");
        adminProfileView.setPhoneNumber("+11234567890");
        adminProfileView.setQualifiedPosition("Engineer");
        adminProfileView.setRole("ADMIN");
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void whenGetAllCrewMembers_asAdmin_thenReturnFullProfiles() throws Exception {
        List profiles = Arrays.asList(adminProfileView);
        when(crewMemberService.getAllCrewMembers(any())).thenReturn(profiles);

        mockMvc.perform(get("/api/v1/crew-members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").exists())
                .andExpect(jsonPath("$[0].phoneNumber").exists());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void whenGetAllCrewMembers_asRegularUser_thenReturnLimitedProfiles() throws Exception {
        List profiles = Arrays.asList(regularProfileView);
        when(crewMemberService.getAllCrewMembers(any())).thenReturn(profiles);

        mockMvc.perform(get("/api/v1/crew-members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").doesNotExist())
                .andExpect(jsonPath("$[0].phoneNumber").doesNotExist());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void whenGetCrewMemberProfile_asRegularUser_thenReturnLimitedProfile() throws Exception {
        when(crewMemberService.getCrewMemberProfile(eq(1L), any())).thenReturn(regularProfileView);

        mockMvc.perform(get("/api/v1/crew-members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.phoneNumber").doesNotExist());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void whenGetCrewMemberProfile_asAdmin_thenReturnFullProfile() throws Exception {
        when(crewMemberService.getCrewMemberProfile(eq(1L), any())).thenReturn(adminProfileView);

        mockMvc.perform(get("/api/v1/crew-members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.phoneNumber").exists());
    }

    @Test
    void whenRegisterCrewMember_withValidInput_thenSuccess() throws Exception {
        when(crewMemberService.registerCrewMember(any())).thenReturn(adminProfileView);

        mockMvc.perform(post("/api/v1/crew-members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void whenRegisterCrewMember_withInvalidInput_thenBadRequest() throws Exception {
        registrationRequest.setEmail("invalid-email");

        mockMvc.perform(post("/api/v1/crew-members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest());
    }
} 