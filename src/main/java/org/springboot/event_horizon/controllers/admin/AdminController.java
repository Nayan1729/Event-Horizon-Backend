package org.springboot.event_horizon.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springboot.event_horizon.dtos.ClubRequestDTO;
import org.springboot.event_horizon.entities.Club;
import org.springboot.event_horizon.services.ClubService;
import org.springboot.event_horizon.utilities.ApiException;
import org.springboot.event_horizon.utilities.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/admin/club-request")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ClubService clubService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllClubRequest() {
        try {
            List<ClubRequestDTO> clubRequestDTOS = this.clubService.getAllClubRequests();
            return ResponseEntity.status(200).body(new ApiResponse(200,clubRequestDTOS,"Club Request List fetched successfully..."));
        }catch (ApiException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(),null,e.getMessage()));
        }

    }

    @GetMapping("/{id}/approve")
    public ResponseEntity<ApiResponse> approveClubRequest(@PathVariable int id) throws ApiException{
        try{
            if(id==0){
                throw new ApiException("h",400);
            }
            Club registeredClub = clubService.approveClubRequest(id);
            return ResponseEntity.status(200).body(new ApiResponse(200, registeredClub, "Club request approved successfully."));
        }catch(ApiException e){
                return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(),null , e.getMessage()));
        }
    }
    @GetMapping("/{id}/reject")
    public ResponseEntity<ApiResponse> rejectClubRequest(@PathVariable int id) throws ApiException{
        try{
            if(id==0){
                throw new ApiException("h",400);
            }
            Club unRegisteredClub = clubService.rejectClubRequest(id);
            return ResponseEntity.status(200).body(new ApiResponse(200, unRegisteredClub, "Club request rejected successfully."));
        }catch(ApiException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(),null , e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(500,null , e.getMessage()));
        }
    }
}