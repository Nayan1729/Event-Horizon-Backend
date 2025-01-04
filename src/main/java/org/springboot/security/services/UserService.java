package org.springboot.security.services;

import org.springboot.security.entities.User;
import org.springboot.security.repositories.MyUserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    JWTService jwtService;

    @Autowired
    private JavaMailSender emailSender; // Spring Mail


    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public User registerUser(User user) {
        user.setPassword(this.encoder.encode(user.getPassword()));
        user.setVerified(false);  // Mark user as not verified initially
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


    public String verifyUserEmail(String token) {
        User user = myUserDetailsRepository.findByVerificationToken(token);

        if (user != null && !user.isVerified()) {
            user.setVerified(true);  // Mark the user as verified
            user.setVerificationToken(null);  // Clear the token after verification
            myUserDetailsRepository.save(user);

            // Generate a JWT token after email verification
            return jwtService.generateToken(user.getUsername());
        } else {
            throw new IllegalArgumentException("Invalid verification token or already verified.");
        }
    }

    public String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();  // Create a random token
        user.setVerificationToken(token);
        myUserDetailsRepository.save(user);  // Store token in DB
        return token;
    }


    public String verifyUser(User user) {
        try{
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            if (auth.isAuthenticated()) {
                return this.jwtService.generateToken(user.getUsername());
            }
        }catch(Exception e){
            throw new IllegalArgumentException("Invalid username or password");
        }

        return "fail";
    }
}
