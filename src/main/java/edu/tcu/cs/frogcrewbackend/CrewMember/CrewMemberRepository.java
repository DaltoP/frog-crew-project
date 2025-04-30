package edu.tcu.cs.frogcrewbackend.CrewMember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {
    Optional<CrewMember> findByEmail(String email);
    boolean existsByEmail(String email);
    List<CrewMember> findByQualifiedPosition(String position);
} 