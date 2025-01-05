package org.springboot.security.services;

import org.springboot.security.entities.Role;
import org.springboot.security.entities.User;
import org.springboot.security.repositories.MyUserDetailsRepository;
import org.springboot.security.utilities.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    MyUserDetailsRepository myUserDetailsRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    @Lazy
    JWTService jwtService;

    @Autowired
    private JavaMailSender emailSender; // Spring Mail

    @Autowired
    MyUserDetailsService userDetailsService;


    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public User getUserByUsername(String username) {
            return myUserDetailsRepository.findByUsername(username);
    }

    public User registerUser(User user) throws ApiException {
        if (myUserDetailsRepository.existsByUsername(user.getUsername())) {
            throw new ApiException("User already exists with the username: " + user.getUsername(), HttpStatus.BAD_REQUEST.value());
        }
        user.setPassword(this.encoder.encode(user.getPassword()));
        user.setVerified(false);  // Mark user as not verified initially
        user.setRole(Role.USER);
        // Save the user in database first and then send token to get then verified
        String verificationToken = generateVerificationToken(user);
        System.out.println("verificationToken: " + verificationToken);

        sendVerificationEmail(user.getUsername(), verificationToken);

        System.out.println("Email sent successfully");

        User registeredUser = this.myUserDetailsRepository.save(user);
        System.out.println(registeredUser);
        return registeredUser;
    }
    // Send verification email
    private void sendVerificationEmail(String username, String verificationToken) {
        String verificationLink = "http://localhost:8080/verify?token=" + verificationToken;
        String subject = "Email Verification";
        String message = "Dear " + username + ",\n\nPlease verify your email by clicking on the following link: \n" + verificationLink;

        // Create the email message
        System.out.println(message);
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(username); // Assuming the username is the email
        email.setSubject(subject);
        email.setText(message);
        email.setFrom("nayanthacker248@gmail.com"); // Replace with your email

        // Send the email
        try {
            emailSender.send(email);
        }catch (Exception e) {
            System.out.println(e);
        }


    }


    public String verifyUserEmail(String token) throws ApiException {
        User user = myUserDetailsRepository.findByVerificationToken(token);

        if (user != null && !user.isVerified()) {
            user.setVerified(true);  // Mark the user as verified
            user.setVerificationToken(null);  // Clear the token after verification
            myUserDetailsRepository.save(user);

            // Generate a JWT token after email verification
            return jwtService.generateToken(user.getUsername());
        } else {
            throw new ApiException("Invalid or already verified email",400);
        }
    }


    public String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();  // Create a random token
        user.setVerificationToken(token);
        myUserDetailsRepository.save(user);  // Store token in DB
        return token;
    }


    public String login(User user) throws ApiException {
        try {
            // Attempt to authenticate the user using the authentication manager
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            // If authentication is successful, set the authentication in the SecurityContext
            if (auth.isAuthenticated()) {
                // Explicitly set the Authentication in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(auth);

                // Generate and return the JWT token after successful authentication
                return this.jwtService.generateToken(user.getUsername());
            } else {
                throw new ApiException("Authentication failed", 401);
            }
        } catch (Exception e) {
            throw new ApiException("Invalid username or password", 401);
        }
    }


    //Reason why we set authentication in the securityContextHolder
    public User getCurrentUser() throws ApiException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication);
        if (authentication != null) {
            String username = authentication.getName(); // Get the username from the authenticated user
            return myUserDetailsRepository.findByUsername(username); // Return user details from the database
        }
        throw new ApiException("No authenticated user found", HttpStatus.UNAUTHORIZED.value());
    }
}
