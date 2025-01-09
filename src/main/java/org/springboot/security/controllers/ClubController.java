package org.springboot.security.controllers;

import jakarta.validation.Valid;
import org.springboot.security.dtos.RegisterClubRequest;
import org.springboot.security.entities.Club;
import org.springboot.security.entities.User;
import org.springboot.security.entities.UserPrincipal;
import org.springboot.security.services.ClubService;
import org.springboot.security.services.JWTService;
import org.springboot.security.utilities.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clubs")
public class ClubController{
    @Autowired
    ClubService clubService;
    @Autowired
    JWTService jwtService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerClub(@RequestBody @Valid RegisterClubRequest request ) {
        Club registeredClub;
        try {
            UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email = principal.getUsername();

            System.out.println(principal);
            System.out.println(email);
            // As soon as we get the DTO we map it with its corresponding entity after some validations in the Service layer
            registeredClub = this.clubService.registerClub(request, principal.getUsername());
            String jwtToken = this.jwtService.generateToken(principal.getUsername());
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + jwtToken);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(new ApiResponse(500, null, e.getMessage()));
        }
        return ResponseEntity.status(201).body(new ApiResponse(201, registeredClub, "Club registered successfully and upadted the user role"));
    }

    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @GetMapping("")
    public ResponseEntity<ApiResponse> getEvents(){
        return ResponseEntity.status(200).body(new ApiResponse(200,null,"Events fetched Successfully"));
    }
}
