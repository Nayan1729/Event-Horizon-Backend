package org.springboot.event_horizon.controllers;

import jakarta.validation.Valid;
import org.springboot.event_horizon.entities.User;
import org.springboot.event_horizon.services.UserService;
import org.springboot.event_horizon.utilities.ApiException;
import org.springboot.event_horizon.utilities.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    ApplicationContext context;

        @GetMapping("/user/profile")
        @PreAuthorize("hasRole('ADMIN')")
        public String userProfile() {
            return "Welcome to User Profile!";
        }

    /**
     * Registers a new user.
     *
     * @param user The user data to register.
     * @return The registered user.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody User user) {
        try {
            User registeredUser = this.userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(201,registeredUser,"User created successfully"));
        }catch (ApiException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(),null,e.getMessage()));
        }

    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> verify(@RequestParam("token") String token) {
        try {
            // Verify the user and generate a JWT token
            Map<String,Object> verifyResponse= userService.verifyUserEmail(token);
            User currentUser = (User) (verifyResponse.get("user"));
            String jwtToken = (String) verifyResponse.get("jwtToken");
            // Prepare response with JWT token in the header and user data in the body
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + jwtToken);

            ApiResponse response = new ApiResponse(200, currentUser, "Email verified successfully.");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(response);
        } catch (ApiException e) {
            // Handle API exceptions (Invalid/expired token)
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody User user) {
        try {
            String token = userService.login(user);
            User currentUser = userService.getUserByEmail(user.getEmail());
            // Create response headers with the token
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            // Respond with token in headers and success message in the body
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new ApiResponse(200, currentUser, "Login successful"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), null, "Login failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "An unexpected error occurred: " + e.getMessage()));
        }
    }
}
