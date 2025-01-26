package org.springboot.event_horizon.services;

import lombok.RequiredArgsConstructor;
import org.springboot.event_horizon.entities.Role;
import org.springboot.event_horizon.entities.RoleName;
import org.springboot.event_horizon.entities.User;
import org.springboot.event_horizon.entities.UserPrincipal;
import org.springboot.event_horizon.repositories.MyUserDetailsRepository;
import org.springboot.event_horizon.repositories.RoleRepository;
import org.springboot.event_horizon.utilities.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.security.SecureRandom;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {


    private final MyUserDetailsRepository myUserDetailsRepository;

    private final AuthenticationManager authenticationManager;

    @Autowired
    @Lazy
    private JWTService jwtService;

    private final  MyUserDetailsService userDetailsService;

     // Spring Mail

    @Value("${mail.email}")
    private String setFromEmail;

  private final  RoleRepository roleRepository;

  private final EmailService emailService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public Optional<User> getUserByEmail(String email) {
            return myUserDetailsRepository.findByEmail(email);
    }

    public User registerUser(User user) throws ApiException,MailAuthenticationException {
            User registeredUser = null;
            Optional<User> existingUser = myUserDetailsRepository.findByEmail(user.getEmail());
            if (existingUser.isPresent()) {
                if(existingUser.get().isVerified()){
                    throw new ApiException("User already exists and is verified",HttpStatus.BAD_REQUEST.value());
                }
                user = existingUser.get();
                }
                user.setPassword(this.encoder.encode(user.getPassword()));
                user.setVerified(false);  // Mark user as not verified initially
                Optional<Role> userRole = roleRepository.findByName(RoleName.USER);

                user.setRoles(new HashSet<>(Set.of(userRole.get())));
                // Save the user in database first and then send token to get then verified
                String verificationToken = emailService.generateVerificationToken(user);

                emailService.sendVerificationEmail(user.getEmail(), verificationToken);
                System.out.println(user);

                registeredUser = this.myUserDetailsRepository.save(user);
                return registeredUser;
    }
    // Send verification email

    public Map<String,Object> verifyUserEmail(String token) throws ApiException {
        Optional<User> requestUser = myUserDetailsRepository.findByVerificationToken(token);
        if (!requestUser.isPresent()) {
            throw new ApiException("Invalid verification token",HttpStatus.UNAUTHORIZED.value());
        }
        if (requestUser.isPresent() && !requestUser.get().isVerified()) {
            User user = requestUser.get();
            user.setVerified(true);  // Mark the user as verified
            user.setVerificationToken(null);  // Clear the token after verification
            myUserDetailsRepository.save(user);

            // Generate a JWT token after email verification
            Map<String,Object> userMap = new HashMap<>();
            userMap.put("jwtToken",jwtService.generateToken(user.getEmail()));
            userMap.put("user", user);
            return userMap;
        } else {
            throw new ApiException("Invalid or already verified email",400);
        }
    }

    public void resendVerificationEmail(String email) throws ApiException {
        Optional<User> registeredUser = myUserDetailsRepository.findByEmail(email);
        if (!registeredUser.isPresent()) {
            throw new ApiException("Invalid email",404);
        }
        String otp = emailService.generateVerificationToken(registeredUser.get());
        emailService.sendVerificationEmail(registeredUser.get().getEmail(), otp);
    }



    public String login(User user) throws ApiException , BadCredentialsException {
        System.out.println("login service");
            // Attempt to authenticate the user using the authentication manager
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
            System.out.println("Auth:"+auth);
            // If authentication is successful, set the authentication in the SecurityContext
            if (auth.isAuthenticated()) {
                // Explicitly set the Authentication in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(auth);

                // Generate and return the JWT token after successful authentication
                return this.jwtService.generateToken(user.getEmail());
            } else {
                System.out.println("Authentication Failed");
                throw new ApiException("Authentication failed", 401);
            }

    }

    //Reason why we set authentication in the securityContextHolder so as to get it .
    public User getLoggedInUser() throws ApiException {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = principal.getUsername();
        System.out.println("email:"+email);
        if(email != null){
            return myUserDetailsRepository.findByEmail(email).get();
        }
        throw new ApiException("No authenticated user found", HttpStatus.UNAUTHORIZED.value());
    }
}
