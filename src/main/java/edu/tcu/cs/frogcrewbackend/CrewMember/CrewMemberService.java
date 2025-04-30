package edu.tcu.cs.frogcrewbackend.CrewMember;

import edu.tcu.cs.frogcrewbackend.dto.UserDTO;
import edu.tcu.cs.frogcrewbackend.dto.UserSimpleDTO;
import edu.tcu.cs.frogcrewbackend.dto.CrewedUserDTO;
import edu.tcu.cs.frogcrewbackend.system.exception.AccountAlreadyExistsException;
import edu.tcu.cs.frogcrewbackend.system.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CrewMemberService {
    private final CrewMemberRepository crewMemberRepository;
    private final PasswordEncoder passwordEncoder;

    public CrewMemberService(CrewMemberRepository crewMemberRepository, PasswordEncoder passwordEncoder) {
        this.crewMemberRepository = crewMemberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CrewedUserDTO registerCrewMember(UserDTO request) {
        // Check if email already exists
        if (crewMemberRepository.existsByEmail(request.getEmail())) {
            throw new AccountAlreadyExistsException("Email already registered");
        }

        // Create new crew member
        CrewMember crewMember = new CrewMember();
        crewMember.setFirstName(request.getFirstName());
        crewMember.setLastName(request.getLastName());
        crewMember.setEmail(request.getEmail());
        crewMember.setPhoneNumber(request.getPhoneNumber());
        crewMember.setPassword(passwordEncoder.encode(request.getPassword()));
        crewMember.setRole(request.getRole());
        crewMember.setQualifiedPosition(request.getQualifiedPosition());
        crewMember.setAddress(request.getAddress());
        crewMember.setCity(request.getCity());
        crewMember.setState(request.getState());
        crewMember.setZipCode(request.getZipCode());
        crewMember.setCountry(request.getCountry());

        // Save and return the new crew member
        CrewMember saved = crewMemberRepository.save(crewMember);
        return CrewedUserDTO.fromCrewMember(saved);
    }

    public Object updateProfile(Long id, UserDTO request, String currentUserEmail) {
        // Get the crew member
        CrewMember crewMember = crewMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Crew member not found"));

        // Check if the current user is the owner of the profile
        if (!crewMember.getEmail().equals(currentUserEmail)) {
            throw new IllegalStateException("You can only edit your own profile");
        }

        // Check if new email is already taken by another user
        if (!request.getEmail().equals(crewMember.getEmail()) && 
            crewMemberRepository.existsByEmail(request.getEmail())) {
            throw new AccountAlreadyExistsException("Email already taken by another user");
        }

        // Update the fields
        crewMember.setFirstName(request.getFirstName());
        crewMember.setLastName(request.getLastName());
        crewMember.setEmail(request.getEmail());
        crewMember.setPhoneNumber(request.getPhoneNumber());
        crewMember.setRole(request.getRole());
        crewMember.setQualifiedPosition(request.getQualifiedPosition());
        crewMember.setAddress(request.getAddress());
        crewMember.setCity(request.getCity());
        crewMember.setState(request.getState());
        crewMember.setZipCode(request.getZipCode());
        crewMember.setCountry(request.getCountry());

        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            crewMember.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Save the changes
        CrewMember saved = crewMemberRepository.save(crewMember);
        return CrewedUserDTO.fromCrewMember(saved);
    }

    public List<?> getAllCrewMembers(String currentUserEmail) {
        CrewMember currentUser = crewMemberRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());
        
        return crewMemberRepository.findAll().stream()
                .map(member -> isAdmin ? 
                     CrewedUserDTO.fromCrewMember(member) : 
                     UserSimpleDTO.fromCrewMember(member))
                .collect(Collectors.toList());
    }

    public List<?> getCrewMembersByPosition(String position, String currentUserEmail) {
        CrewMember currentUser = crewMemberRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());
        
        return crewMemberRepository.findByQualifiedPosition(position).stream()
                .map(member -> isAdmin ? 
                     CrewedUserDTO.fromCrewMember(member) : 
                     UserSimpleDTO.fromCrewMember(member))
                .collect(Collectors.toList());
    }

    public Object getCrewMemberProfile(Long id, String currentUserEmail) {
        CrewMember currentUser = crewMemberRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        
        CrewMember requestedMember = crewMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Crew member not found"));
        
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());
        
        return isAdmin ? 
               CrewedUserDTO.fromCrewMember(requestedMember) : 
               UserSimpleDTO.fromCrewMember(requestedMember);
    }
} 