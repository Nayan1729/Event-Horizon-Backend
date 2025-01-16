package org.springboot.security.repositories;

import org.springboot.security.entities.Event;
import org.springboot.security.entities.RegisterForEvent;
import org.springboot.security.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegisterForEventRepository extends JpaRepository<RegisterForEvent, Integer> {
    public Optional<RegisterForEvent> findById(int id);
    public Optional<List<RegisterForEvent>> findByStatus(String status);
    public boolean existsByUserAndEvent(User user, Event event);

}
