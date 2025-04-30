package edu.tcu.cs.frogcrewbackend.CrewMember;

import edu.tcu.cs.frogcrewbackend.dto.UserDTO;
import edu.tcu.cs.frogcrewbackend.dto.UserSimpleDTO;
import edu.tcu.cs.frogcrewbackend.dto.CrewedUserDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/crew-members")
public class CrewMemberController {
    private final CrewMemberService crewMemberService;

    public CrewMemberController(CrewMemberService crewMemberService) {
        this.crewMemberService = crewMemberService;
    }

    @GetMapping
    public ResponseEntity<List<?>> getAllCrewMembers(Authentication authentication) {
        String currentUserEmail = authentication != null ? authentication.getName() : null;
        List<?> crewMembers = crewMemberService.getAllCrewMembers(currentUserEmail);
        return new ResponseEntity<>(crewMembers, HttpStatus.OK);
    }

    @GetMapping("/position/{position}")
    public ResponseEntity<List<?>> getCrewMembersByPosition(
            @PathVariable String position,
            Authentication authentication) {
        String currentUserEmail = authentication != null ? authentication.getName() : null;
        List<?> crewMembers = crewMemberService.getCrewMembersByPosition(position, currentUserEmail);
        return new ResponseEntity<>(crewMembers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCrewMemberProfile(
            @PathVariable Long id,
            Authentication authentication) {
        String currentUserEmail = authentication != null ? authentication.getName() : null;
        Object profile = crewMemberService.getCrewMemberProfile(id, currentUserEmail);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<CrewedUserDTO> registerCrewMember(@Valid @RequestBody UserDTO request) {
        CrewedUserDTO newCrewMember = crewMemberService.registerCrewMember(request);
        return new ResponseEntity<>(newCrewMember, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO request,
            Authentication authentication) {
        String currentUserEmail = authentication != null ? authentication.getName() : null;
        Object result = crewMemberService.updateProfile(id, request, currentUserEmail);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
} 