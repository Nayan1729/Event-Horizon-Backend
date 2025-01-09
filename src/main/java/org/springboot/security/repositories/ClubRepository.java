package org.springboot.security.repositories;

import org.springboot.security.entities.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {
    public Optional<Club> findClubByName(String name);

}
