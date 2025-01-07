package org.springboot.security.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springboot.security.entities.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {


    private final UserService userService;
    private String secretKeyString = "27EE4CABC809E0F32B8BFAD9956331EA34AD2F282E1ED066D4B40D13C400BF52";
    private SecretKey secretKey;
    public JWTService(UserService userService) {

        try {
            // Converting the String secretKey into a Key
            //make a key generator for HmacSHA
            // Get the key and encode the key so that we can decode it when we use getKey()
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        this.userService = userService;
    }

    public String generateToken(String email) {
        User user = userService.getUserByEmail(email);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());

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