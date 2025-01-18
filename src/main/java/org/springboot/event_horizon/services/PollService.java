package org.springboot.event_horizon.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springboot.event_horizon.dtos.PollDetailResponseDTO;
import org.springboot.event_horizon.dtos.PollRequestDTO;
import org.springboot.event_horizon.dtos.PollResponseDTO;
import org.springboot.event_horizon.entities.Poll;
import org.springboot.event_horizon.entities.User;
import org.springboot.event_horizon.repositories.PollRepository;
import org.springboot.event_horizon.utilities.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final ModelMapper modelMapper;
    @Autowired
    @Lazy
    private  VoteService voteService;
    private final UserService userService;

    public Poll createPoll(String question, Map<String, String> eventOptions) {
        Poll poll = new Poll();
        poll.setQuestion(question);
        poll.setEventOptions(eventOptions);
        return pollRepository.save(poll);
    }

    public Optional<Poll> getPollById(int id) {
        return pollRepository.findById(id);
    }

    public void deletePoll(int pollId) throws ApiException {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ApiException("Poll not found", 404));
        pollRepository.delete(poll);
    }

    public List<PollResponseDTO> getAllPolls() throws ApiException {
        User currentUser = userService.getLoggedInUser(); // Fetch the logged-in user

        List<Poll> polls = pollRepository.findAll();

        return polls.stream().map(poll -> {
            PollResponseDTO pollResponse = modelMapper.map(poll, PollResponseDTO.class);

            // Check if the user has voted in the poll
            boolean hasVoted = poll.getVotes().stream()
                    .anyMatch(vote -> vote.getUser().getId() == currentUser.getId());

            if (hasVoted) {
                // Include vote counts in the response
                Map<String, Integer> voteCounts = null;
                try {
                    voteCounts = voteService.countVotes(poll.getId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                pollResponse.setVoteCount(voteCounts); // Populate vote counts
            } else {
                // Exclude vote counts from the response
                pollResponse.setVoteCount(null); // Or set to an empty map if preferred
            }
            return pollResponse;
        }).collect(Collectors.toList());
    }

    public Poll updatePoll(int pollId, @Valid PollRequestDTO updatedPoll) throws ApiException {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ApiException("Poll not found", 404));
        poll.setQuestion(updatedPoll.getQuestion());
        poll.setEventOptions(updatedPoll.getEventOptions());
        return pollRepository.save(poll);
    }
    public PollDetailResponseDTO getPoll(int pollId) throws ApiException {
        Poll poll = getPollById(pollId)
                .orElseThrow(() -> new ApiException("Poll not found", 404));

        // Fetch vote counts for the poll
        Map<String, Integer> voteCounts = voteService.countVotes(pollId);

        PollDetailResponseDTO response = new PollDetailResponseDTO();
        response.setId(poll.getId());
        response.setQuestion(poll.getQuestion());
        response.setEventOptions(voteCounts); // Includes vote counts
        return response;
    }

}