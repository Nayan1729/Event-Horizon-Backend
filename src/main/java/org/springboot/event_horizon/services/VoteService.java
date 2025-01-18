package org.springboot.event_horizon.services;

import lombok.RequiredArgsConstructor;
import org.springboot.event_horizon.entities.Poll;
import org.springboot.event_horizon.entities.User;
import org.springboot.event_horizon.entities.Vote;
import org.springboot.event_horizon.repositories.VoteRepository;
import org.springboot.event_horizon.utilities.ApiException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VoteService {


    private final VoteRepository voteRepository;


    private final  PollService pollService;

    private final UserService userService;


    public Vote castVote(int pollId, String eventName )throws ApiException {
        User currentUser = this.userService.getLoggedInUser();
        Poll poll = pollService.getPollById(pollId)
                .orElseThrow(() -> new RuntimeException("Poll not found"));

        //Check if the option selected by the user is in the set of options provided by the admin
        if (!poll.getEventOptions().containsKey(eventName)){
            throw new IllegalArgumentException("Invalid event option");
        }
        // Check if the user has already voted for this poll
        boolean hasAlreadyVoted = poll.getVotes().stream()
                .anyMatch(vote -> vote.getUser().getId() == currentUser.getId());
        if (hasAlreadyVoted) {
            throw new ApiException("User has already voted for this poll", 400);
        }
        Vote vote = new Vote();
        vote.setPoll(poll);
        vote.setUser(currentUser);
        vote.setVotedEventName(eventName);
        return voteRepository.save(vote);
    }

    public Map<String, Integer> countVotes(int pollId) throws ApiException {
        Poll poll = pollService.getPollById(pollId)
                .orElseThrow(() -> new ApiException("Poll not found", 404));
        System.out.println(poll.getVotes());
        // Fetch and count votes grouped by event name
        List<Object[]> voteResults = voteRepository.countVotesByPoll(pollId)
                .orElseThrow(()-> new ApiException("Poll not found", 404));
        Map<String, Integer> voteCount = new HashMap<>();

        // Transform the query results into a Map
        for (Object[] result : voteResults) {
            String eventName = (String) result[0];
            Long count = (Long) result[1]; // COUNT returns Long in JPA
            voteCount.put(eventName, count.intValue());
        }
        // Ensure all options from the poll are represented, even if no votes
        for (String eventName : poll.getEventOptions().keySet()) {
            voteCount.putIfAbsent(eventName, 0);
        }
        return voteCount;
    }
}

