package org.springboot.event_horizon.repositories;

import org.springboot.event_horizon.entities.RegisterForEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegisterForEventRepository extends JpaRepository<RegisterForEvent, Integer> {
    public Optional<RegisterForEvent> findById(int id);
    public Optional<List<RegisterForEvent>> findByStatus(String status);
    public boolean existsByUserIdAndEventId(int user_id, int event_id);

}
