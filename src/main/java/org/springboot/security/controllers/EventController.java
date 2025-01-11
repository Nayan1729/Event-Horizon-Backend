package org.springboot.security.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springboot.security.entities.Event;
import org.springboot.security.services.EventService;
import org.springboot.security.utilities.ApiException;
import org.springboot.security.utilities.ApiResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/clubs/{clubId}/events")
public class EventController {

    private final EventService eventService;

    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> createEvent(@PathVariable int clubId,@RequestBody @Valid Event event) {
        try{
            Event registeredEvent = eventService.registerEvent(clubId, event);
            return ResponseEntity.status(201).body(new ApiResponse(201,registeredEvent,"Event registered successfully"));
        }catch (ApiException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<ApiResponse> deleteEvent(@PathVariable int clubId , @PathVariable int eventId) {
        try{
                this.eventService.deleteEvent(clubId,eventId);
                return ResponseEntity.status(200).body(new ApiResponse(204,clubId,"Event deleted successfully"));
        }catch (ApiException e ){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponse> getEvent(@PathVariable int clubId,@PathVariable int eventId){
        try{
            Event event  = this.eventService.getEvent(clubId,eventId);
            System.out.println(event);
            return ResponseEntity.status(200).body(new ApiResponse(200,event,"Event fetched successfully"));
        }catch (ApiException e ){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }
}
