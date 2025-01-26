package org.springboot.event_horizon.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springboot.event_horizon.dtos.*;
import org.springboot.event_horizon.entities.Club;
import org.springboot.event_horizon.entities.UserPrincipal;
import org.springboot.event_horizon.services.ClubService;
import org.springboot.event_horizon.services.EventService;
import org.springboot.event_horizon.utilities.ApiException;
import org.springboot.event_horizon.utilities.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/clubs")
public class ClubController{

    private  final ClubService clubService;
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllClubs(){
        try{
            List<ClubDTO> clubDtos = this.clubService.getAllClubs();
            return ResponseEntity.status(200).body(new ApiResponse(200,clubDtos,"Club List fetched Successfully"));
        }catch (ApiException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(),null,e.getMessage()));
        }
    }
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerClubRequest(@RequestBody @Valid RegisterClubRequest request ) {

        try {
            UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email = principal.getUsername();
            System.out.println("EMAIL :"+email );
            // As soon as we get the ClubRequest(or a DTO) we map it with its corresponding entity after some validations in the Service layer
            Club registeredClub = this.clubService.registerClub(request,email);
            return ResponseEntity.status(201).body(new ApiResponse(201, registeredClub, "Club registered successfully and updated the user role"));
        }catch (ApiException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(),null,e.getMessage()));
        }
        catch (Exception e){
            return ResponseEntity.status(500).body(new ApiResponse(500, null, e.getMessage()));
        }
    }

    //Entire club details shouldn't be sent as the output change it
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @PostMapping("/{clubId}/members")
    public ResponseEntity<ApiResponse> addClubMembers(
            @PathVariable int clubId,
            @RequestBody @Valid List<AddMembersRequestDTO> memberRequests   ) throws ApiException {
        try{
            BatchAddClubMemberRequestDTO response = this.clubService.addClubMembers(clubId,memberRequests);
            return ResponseEntity.status(201).body(new ApiResponse(201, response, "Member added successfully"));
        }catch (ApiException e){
                return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @GetMapping("/{clubId}/events")
    public ResponseEntity<ApiResponse> getEvents(@PathVariable int clubId) throws ApiException {
        Set<EventSummaryDTO> events = this.clubService.getClubEvents(clubId);
        return ResponseEntity.status(200).body(new ApiResponse(200,events,"Events fetched Successfully"));
    }
}
