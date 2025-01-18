package org.springboot.event_horizon.repositories;

import org.springboot.event_horizon.entities.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PollRepository extends JpaRepository<Poll, Integer> {

    Optional<Poll> findById(int id);
}
