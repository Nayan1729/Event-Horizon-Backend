package org.springboot.event_horizon.repositories;

import org.springboot.event_horizon.entities.Club;
import org.springboot.event_horizon.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {
    @Override
    Optional<Event> findById(Integer id);
    Optional<Event> findByTitle(String name);

    @Query("SELECT e FROM Event e WHERE e.club.clubId = :clubId")
    Optional<List<Event>>findAllByClubId(@Param("clubId") int clubId);

    Optional<List<Event>> findByStatus(String status);

    Optional<List<Event>> findAllByClubAndStatus(Club clubId , String status);
}
