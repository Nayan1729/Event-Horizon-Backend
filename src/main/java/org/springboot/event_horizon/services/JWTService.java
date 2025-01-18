package org.springboot.event_horizon.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springboot.event_horizon.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JWTService {
    private final UserService userService;
    @Value("${secret.key.string}")
    private String secretKeyString;
    private SecretKey secretKey;
    @PostConstruct
    public void init() {
        if (secretKeyString == null || secretKeyString.isEmpty()) {
            throw new IllegalArgumentException("secret.key.string is not configured properly.");
        }

        // Ensure the secret key is valid for HMAC-SHA256
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
        System.out.println("Secret key initialized successfully.");
    }
    public JWTService(UserService userService) {

        this.userService = userService;
    }

    public String generateToken(String email) {
        User user = userService.getUserByEmail(email);
        Map<String, Object> claims = new HashMap<>();
        //Put roles in claims
        claims.put("roles", user.getRoles().stream()
                .map(role -> role.getName().name()) // Convert Role enum to String
                .collect(Collectors.toList()));

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000*60 * 60 * 24*7))
                .and()
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        return secretKey;
    }

    public String extractEmail(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractEmail(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}