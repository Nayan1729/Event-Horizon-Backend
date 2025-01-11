package org.springboot.security.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springboot.security.entities.Club;
import org.springboot.security.services.ClubService;
import org.springboot.security.utilities.ApiException;
import org.springboot.security.utilities.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/club-request")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ClubService clubService;

    @PostMapping("/{id}/approve")
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
    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse> rejectClubRequest(@PathVariable int id) throws ApiException{
        try{
            if(id==0){
                throw new ApiException("h",400);
            }
            Club unRegisteredClub = clubService.rejectClubRequest(id);
            return ResponseEntity.status(200).body(new ApiResponse(200, unRegisteredClub, "Club request approved successfully."));
        }catch(ApiException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(),null , e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(500,null , e.getMessage()));
        }
    }
}
