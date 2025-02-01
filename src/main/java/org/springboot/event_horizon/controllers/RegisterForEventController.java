    package org.springboot.event_horizon.controllers;

    import lombok.RequiredArgsConstructor;
    import org.springboot.event_horizon.dtos.RegisterForEventDTO;
    import org.springboot.event_horizon.entities.RegisterForEvent;
    import org.springboot.event_horizon.services.RegisterForEventService;
    import org.springboot.event_horizon.utilities.ApiException;
    import org.springboot.event_horizon.utilities.ApiResponse;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequiredArgsConstructor
    @RequestMapping("/api/v1/events/registrations")
    public class RegisterForEventController {

        private final RegisterForEventService registerForEventService;


        @GetMapping("/{clubId}")
        public ResponseEntity<List<RegisterForEventDTO>> getEventRegistrations(@PathVariable Integer clubId) {
            return null;
        }

        // Add the email service
        @PostMapping("/{eventId}")
        public ResponseEntity<ApiResponse> registerForEvent(@PathVariable("eventId") Integer eventId ,@RequestBody RegisterForEvent registerForEvent) {
            try {
                System.out.println(eventId);
                this.registerForEventService.registerForEvent(eventId, registerForEvent);
                return ResponseEntity.status(201).body(new ApiResponse(201,null,"Registration Successfull... Wait for the confirmation"));
            } catch (ApiException e) {
                return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(),null,e.getMessage()));
            }
        }
        @PreAuthorize("hasRole('CLUB_ADMIN')")
        @GetMapping("/{eventId}/rejected")
        public ResponseEntity<ApiResponse> getAllUnapprovableRegistration(@PathVariable int eventId){
            try{
                List<RegisterForEventDTO> upprovedRequestsOfEvent = this.registerForEventService.getAllRegistrationsByStatus("REJECTED",eventId);
                System.out.println(upprovedRequestsOfEvent);
                return ResponseEntity.status(200).body(new ApiResponse(200,upprovedRequestsOfEvent,"All the event's rejected registrations fetched"));
            }catch (ApiException e){
                return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(),null,e.getMessage()));
            }
        }
        @PreAuthorize("hasRole('CLUB_ADMIN')")
        @GetMapping("/{eventId}/pending")
        public ResponseEntity<ApiResponse> getAllPendingRegistrations(@PathVariable int eventId){
            try{
                List<RegisterForEventDTO> pendingRequestsOfEvent = this.registerForEventService.getAllRegistrationsByStatus("PENDING",eventId);
                System.out.println(pendingRequestsOfEvent);
                return ResponseEntity.status(200).body(new ApiResponse(200,pendingRequestsOfEvent,"All the event's pending registrations fetched"));
            }catch (ApiException e){
                return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(),null,e.getMessage()));
            }
        }

        @PreAuthorize("hasRole('CLUB_ADMIN')")
        @GetMapping("/{eventId}/approved")
        public ResponseEntity<ApiResponse> getAllApprovedRegistrations(@PathVariable int eventId){
            try{
                List<RegisterForEventDTO> approved = this.registerForEventService.getAllRegistrationsByStatus("APPROVED",eventId);
                return ResponseEntity.status(200).body(new ApiResponse(200,approved,"All the event's approved registrations fetched"));
            }catch (ApiException e){
                return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(),null,e.getMessage()));
            }
        }

        @PreAuthorize("hasRole('CLUB_ADMIN')")
        @GetMapping("/{registerId}/approve")
        public ResponseEntity<ApiResponse> approveRegistration(@PathVariable int registerId){
            try{
                this.registerForEventService.approveRegistration(registerId);
                return ResponseEntity.status(200).body(new ApiResponse(200,null,"Registration Successfully approved..."));
            }catch (ApiException e){
                return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(),null,e.getMessage()));
            }
        }
        @PreAuthorize("hasRole('CLUB_ADMIN')")
        @GetMapping("/{registerId}/reject")
        public ResponseEntity<ApiResponse> rejectRegistration(@PathVariable int registerId){
            try{
                this.registerForEventService.rejectRegistration(registerId);
                return ResponseEntity.status(200).body(new ApiResponse(200,null,"Registration declined Successfully..."));
            }catch (ApiException e){
                return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(),null,e.getMessage()));
            }
        }

    }