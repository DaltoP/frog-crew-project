package edu.tcu.cs.frogcrewbackend.CrewMember;

import edu.tcu.cs.frogcrewbackend.CrewMember.dto.UserSimpleDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrewListServiceTest {

    @Mock
    private CrewMemberRepository crewMemberRepository;

    @InjectMocks
    private CrewListService crewListService;

    private CrewMember crewMember1;
    private CrewMember crewMember2;

    @BeforeEach
    void setUp() {
        crewMember1 = new CrewMember();
        crewMember1.setId(1L);
        crewMember1.setFirstName("John");
        crewMember1.setLastName("Doe");
        crewMember1.setRole("CREW_MEMBER");
        crewMember1.setQualifiedPosition("Camera Operator");

        crewMember2 = new CrewMember();
        crewMember2.setId(2L);
        crewMember2.setFirstName("Jane");
        crewMember2.setLastName("Smith");
        crewMember2.setRole("CREW_MEMBER");
        crewMember2.setQualifiedPosition("Audio Technician");
    }

    @Test
    void whenGetAllCrewMembers_thenReturnAllCrewMembers() {
        when(crewMemberRepository.findAll()).thenReturn(Arrays.asList(crewMember1, crewMember2));

        List<UserSimpleDTO> result = crewListService.getAllCrewMembers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(crewMember1.getFirstName(), result.get(0).getFirstName());
        assertEquals(crewMember2.getFirstName(), result.get(1).getFirstName());
    }

    @Test
    void whenGetCrewMembersByPosition_thenReturnFilteredCrewMembers() {
        when(crewMemberRepository.findByQualifiedPosition("Camera Operator"))
                .thenReturn(Arrays.asList(crewMember1));

        List<UserSimpleDTO> result = crewListService.getCrewMembersByPosition("Camera Operator");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(crewMember1.getFirstName(), result.get(0).getFirstName());
        assertEquals(crewMember1.getQualifiedPosition(), result.get(0).getQualifiedPosition());
    }
} 