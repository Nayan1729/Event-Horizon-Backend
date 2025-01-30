package org.springboot.event_horizon.repositories;

import jakarta.validation.constraints.Email;
import org.springboot.event_horizon.entities.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {
    public Optional<Club> findByName(String name);
    public Optional<Club> findByNameAndStatus(String name, String status);

    public Optional<Club> findByClubId(int id);

    Optional<List<Club>> findByStatus(String status);

    Club findByEmail(@Email String email);
}
