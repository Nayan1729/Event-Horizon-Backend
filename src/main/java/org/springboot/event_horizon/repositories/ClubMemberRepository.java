package org.springboot.event_horizon.repositories;

import org.springboot.event_horizon.entities.ClubMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Integer> {
    public Optional<List<ClubMember>> findByClubClubId(int clubId);
}
