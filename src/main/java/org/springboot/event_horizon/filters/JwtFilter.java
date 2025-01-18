package org.springboot.event_horizon.filters;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springboot.event_horizon.services.JWTService;
import org.springboot.event_horizon.services.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

// Every time we make a request it would pass through filters and so here this filter should only
// be executed once per request thus OncePerRequestFilter

/*
    What's the task of this filter
    -- Its only function to execute before the UserPasswordAuthenticationFilter and its overall
        function is going to be that
            user can access resource with the bearer(jwt) token . As earlier to access every resource
            user must enter their username and password as the UserPasswordAuthenticationFilter
            was getting executed first
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    @Lazy
    JWTService jwtService;

    @Autowired
    ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Bearer {token}
        /*
         Our task is to validate whether this token by the client in the Authorization header is
         correct or not. If it is valid then bypass the UserPasswordAuthenticationFilter .
         */

        System.out.println("doFilterInternal");
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            email = jwtService.extractEmail(token);
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(email);
            if (jwtService.validateToken(token, userDetails)){
                Claims claims = jwtService.extractAllClaims(token);
                List<String> roles = (List<String>) claims.get("roles");
                System.out.println("roles: " + roles);
                List<GrantedAuthority> authorities = roles.stream()
                        .map(role->new SimpleGrantedAuthority("ROLE_"+role))
                        .collect    (Collectors.toList());
                System.out.println("authorities: " + authorities);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities );
                authToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }else {
                System.out.println("JWT Token invalid or expired");  // Debugging
            }
        }
        filterChain.doFilter(request, response);
    }
}