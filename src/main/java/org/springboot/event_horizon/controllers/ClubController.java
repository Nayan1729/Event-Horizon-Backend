package org.springboot.event_horizon.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springboot.event_horizon.dtos.*;
import org.springboot.event_horizon.entities.Club;
import org.springboot.event_horizon.entities.User;
import org.springboot.event_horizon.entities.UserPrincipal;
import org.springboot.event_horizon.services.ClubService;
import org.springboot.event_horizon.services.EventService;
import org.springboot.event_horizon.services.UserService;
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
    private final UserService userService;

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
    @PostMapping(path = "/register")
    public ResponseEntity<ApiResponse> registerClubRequest(@ModelAttribute @Valid RegisterClubRequest request ) {
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
    public ResponseEntity<ApiResponse> addClubMember(
            @PathVariable int clubId,
            @RequestBody @Valid AddClubMembersRequestDTO memberRequest ){
        try{
            ClubMemberDTO clubMember = this.clubService.addClubMember(clubId,memberRequest);
            return ResponseEntity.status(201).body(new ApiResponse(201, clubMember, "Member added successfully"));
        }catch (ApiException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(),null,e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @PatchMapping("/members")
    public ResponseEntity<ApiResponse> editClubMember(@RequestBody  ClubMemberDTO memberRequest ){
        try{
            ClubMemberDTO clubMember = this.clubService.editClubMember(memberRequest);
            return ResponseEntity.ok().body(new ApiResponse(200, clubMember, "Member edited successfully"));
        }catch (ApiException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(),null,e.getMessage()));
        }
    }

    @GetMapping("/{clubId}/events")
    public ResponseEntity<ApiResponse> getEvents(@PathVariable int clubId){
        try{
            Set<EventSummaryDTO> events = this.clubService.getClubEvents(clubId);
            return ResponseEntity.status(200).body(new ApiResponse(200,events,"Events fetched Successfully"));
        }catch (ApiException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }

    @GetMapping("/{clubId}")
    public ResponseEntity<ApiResponse> getAllClubDetails(@PathVariable int clubId) {
        try{
            System.out.println(clubId);
            ClubDetailsDTO clubDetailsDTO = this.clubService.getClubDetails(clubId);
            System.out.println(clubDetailsDTO);
            return ResponseEntity.status(200).body(new ApiResponse(200,clubDetailsDTO,"Club Details fetched Successfully"));
        }catch (ApiException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @GetMapping("/pending-registrations")
    public ResponseEntity<ApiResponse> getPendingEventRegistration(){
        try {
            User currentUser = userService.getLoggedInUser();
            List<RegistrationDTO> pendingRegistrations = this.clubService.getAllPendingEventRegistrationsOfClub(currentUser.getEmail());
            System.out.println(pendingRegistrations);
            return ResponseEntity.status(200).body(new ApiResponse(200,pendingRegistrations,"Pending Registrations of all events  fetched Successfully"));
        } catch (ApiException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @PatchMapping("/{clubId}")
    public ResponseEntity<ApiResponse> updateClubDetails(@ModelAttribute @Valid RegisterClubRequest clubRequest , @PathVariable int clubId) throws ApiException {
        try{
            int id = this.clubService.updateClubDetails(clubId,clubRequest);
            return ResponseEntity.ok().body(new ApiResponse(200,id,"Club updated successfully"));
        }catch (ApiException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @DeleteMapping("/{clubId}")
    public ResponseEntity<ApiResponse> deleteClub(@PathVariable int clubId) throws ApiException {
        try{
            int id = this.clubService.deleteClub(clubId);
            return ResponseEntity.ok().body(new ApiResponse(200,id,"Your Club has been deleted successfully... Please login again..."));
        }catch (ApiException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }

    @DeleteMapping("members/{id}")
    public ResponseEntity<ApiResponse> deleteMember(@PathVariable int id) throws ApiException {
        try{
            this.clubService.deleteClubMember(id);
            return ResponseEntity.ok().body(new ApiResponse(200 , id , "Member deleted successfully"));

        }catch (ApiException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }
}
