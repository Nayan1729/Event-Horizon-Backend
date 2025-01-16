package org.springboot.security.repositories;

import org.springboot.security.entities.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {
    public Optional<Club> findByName(String name);
    public Optional<Club> findByNameAndStatus(String name, String status);

    public Optional<Club> findByClubId(int id);

    Optional<List<Club>> findByStatus(String status);
}
