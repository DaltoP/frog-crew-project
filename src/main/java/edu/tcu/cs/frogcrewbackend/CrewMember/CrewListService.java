package edu.tcu.cs.frogcrewbackend.CrewMember;

import edu.tcu.cs.frogcrewbackend.CrewMember.dto.UserSimpleDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CrewListService {

    private final CrewMemberRepository crewMemberRepository;

    public CrewListService(CrewMemberRepository crewMemberRepository) {
        this.crewMemberRepository = crewMemberRepository;
    }

    public List<UserSimpleDTO> getAllCrewMembers() {
        return crewMemberRepository.findAll().stream()
                .map(crewMember -> {
                    UserSimpleDTO dto = new UserSimpleDTO();
                    dto.setId(crewMember.getId());
                    dto.setFirstName(crewMember.getFirstName());
                    dto.setLastName(crewMember.getLastName());
                    dto.setRole(crewMember.getRole());
                    dto.setQualifiedPosition(crewMember.getQualifiedPosition());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<UserSimpleDTO> getCrewMembersByPosition(String position) {
        return crewMemberRepository.findByQualifiedPosition(position).stream()
                .map(crewMember -> {
                    UserSimpleDTO dto = new UserSimpleDTO();
                    dto.setId(crewMember.getId());
                    dto.setFirstName(crewMember.getFirstName());
                    dto.setLastName(crewMember.getLastName());
                    dto.setRole(crewMember.getRole());
                    dto.setQualifiedPosition(crewMember.getQualifiedPosition());
                    return dto;
                })
                .collect(Collectors.toList());
    }
} 