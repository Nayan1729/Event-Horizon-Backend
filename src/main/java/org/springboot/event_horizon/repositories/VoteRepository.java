package org.springboot.event_horizon.repositories;

import org.springboot.event_horizon.entities.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Integer> {

    List<Vote> findAllByPollId(int pollId);
    @Query("SELECT v.votedEventName AS eventName, COUNT(v) AS voteCount " +
            "FROM Vote v WHERE v.poll.id = :pollId GROUP BY v.votedEventName")
    Optional<List<Object[]>> countVotesByPoll(@Param("pollId") int pollId);
}


