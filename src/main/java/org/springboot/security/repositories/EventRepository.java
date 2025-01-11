package org.springboot.security.repositories;

import org.springboot.security.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {
    @Override
    Optional<Event> findById(Integer id);
//    Optional<Event> findByName(String name);
}
