package org.springboot.security.controllers;

import org.springboot.security.entities.User;
import org.springboot.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

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
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User registeredUser = this.userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error registering user: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam("token") String token) {
        try {
            System.out.println(token);
            String jwtToken = userService.verifyUserEmail(token);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + jwtToken);

            // Respond with token in headers and success message in the body
            return ResponseEntity.ok()
                    .headers(headers)
                    .body("User registered and verified successfuly. Token issued in Authorization header."); // Return JWT Token upon successful verification
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired verification token.");
        }
    }

    /**
     * Authenticates a user and returns a JWT token in the response header.
     *
     * @param user The user credentials.
     * @return A response containing the JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            String token = userService.verifyUser(user);

            // Create response headers with the token
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            // Respond with token in headers and success message in the body
            return ResponseEntity.ok()
                    .headers(headers)
                    .body("Login successful. Token issued in Authorization header.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Login failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error occurred: " + e.getMessage());
        }
    }

}
