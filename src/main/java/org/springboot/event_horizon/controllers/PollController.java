package org.springboot.event_horizon.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springboot.event_horizon.dtos.PollRequestDTO;
import org.springboot.event_horizon.dtos.PollResponseDTO;
import org.springboot.event_horizon.entities.Poll;
import org.springboot.event_horizon.entities.Vote;
import org.springboot.event_horizon.services.PollService;
import org.springboot.event_horizon.services.VoteService;
import org.springboot.event_horizon.utilities.ApiException;
import org.springboot.event_horizon.utilities.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/polls")
@RequiredArgsConstructor
public class PollController {

    @Autowired
    @Lazy
    private  PollService pollService;

    @Autowired
    @Lazy
    private  VoteService voteService;

    @PostMapping
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    public ResponseEntity<ApiResponse> createPoll(@RequestBody @Valid PollRequestDTO poll) {
        try {
            if (poll.getEventOptions().size() <= 1) {
                throw new ApiException("Add at least 2 options of event for the poll", 400);
            }

            Poll createdPoll = this.pollService.createPoll(poll.getQuestion(), poll.getEventOptions());
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse(HttpStatus.CREATED.value(), createdPoll, "Poll created successfully"));
        } catch (ApiException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }

    @PostMapping("/{pollId}/vote")
    public ResponseEntity<ApiResponse> castVoteAndGetResults(
            @PathVariable int pollId,
            @RequestParam String votedEventName) {
        try {
            Vote vote = voteService.castVote(pollId, votedEventName);
            Map<String, Integer> results = voteService.countVotes(pollId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(HttpStatus.OK.value(), results, "Vote cast successfully"));
        } catch (ApiException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }

    @GetMapping("/{pollId}/results")
    public ResponseEntity<ApiResponse> getPollResults(@PathVariable int pollId) {
        try {
            Map<String, Integer> results = voteService.countVotes(pollId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(HttpStatus.OK.value(), results, "Poll results retrieved successfully"));
        } catch (ApiException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }
    @DeleteMapping("/{pollId}")
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    public ResponseEntity<ApiResponse> deletePoll(@PathVariable int pollId) {
        try {
            pollService.deletePoll(pollId);
            return ResponseEntity.ok(new ApiResponse(200, null, "Poll deleted successfully"));
        } catch (ApiException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllPolls() {

        try{
            List<PollResponseDTO> polls = pollService.getAllPolls();
            return ResponseEntity.ok(new ApiResponse(200, polls, "All polls retrieved successfully"));
        }catch (ApiException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }

    }
    @PutMapping("/{pollId}")
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    public ResponseEntity<ApiResponse> updatePoll(@PathVariable int pollId, @RequestBody PollRequestDTO updatedPoll) {
        try {
            Poll updated = pollService.updatePoll(pollId, updatedPoll);
            return ResponseEntity.ok(new ApiResponse(200, updated, "Poll updated successfully"));
        } catch (ApiException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }
}

