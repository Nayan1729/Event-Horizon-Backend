package org.springboot.security.config;
import org.springboot.security.filters.JwtFilter;
import org.springboot.security.services.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("securityFilterChain");
            return http
                    .csrf(customizer->customizer.disable())
                    .authorizeHttpRequests(
                            request->request
                                    .requestMatchers("/register","/login").permitAll()
                                    .anyRequest().authenticated())
                    .httpBasic(Customizer.withDefaults())
                    .sessionManagement(session->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
        // Just for reference all of there are functional Interfaces and hence we are able to use lambda expressions
        // This disables the csrf token requirement for the put,post requests
        // As csrf is disabled we can make the put post request without authentication so authenticate   any http request
        /*
        Now u can get the form login as without it u will be getting a forbidden error as the username
         and password that we are usinng in the application.properties is never used
         As u set it to default all the configuration of login start to work
         */
        /*
            Next is to add the same for postman httpBasic Property for the things like postman
         */
        /*
            Now this makes out httpStateless as we have disabled csrf we have to implement out own policy
            Now we will be making httpStateless so for that when ever we make a request we will be sending
                our details with it so that the server can make sure that we are the authenticated user

            Now that we make it stateless we will realise that we whenever we login we will be directed
                back to the login as we are making a request for another resource that is a home page
                Which is another request and since http is now stateless we will have to verifyAuth

                But this works with postman and we get the data
        */

        }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        // To work with the database we have the daoAuthenticationProvider
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        /*
        What it does:
        The DaoAuthenticationProvider uses the UserDetailsService to fetch user details from the database or another user data source.
        Why it's needed:
        Spring Security relies on UserDetailsService to retrieve user information (like username, password, and roles) during authentication.
        The below is how it does it
         */

        authProvider.setUserDetailsService(userDetailsService);
        // Validating the user with bycrypt encoded  password
        // Whatever password the user will pass will be converted by bcrypt and then it will be
        // validated

        /*

        2. Validating User Passwords
        What it does:

        The DaoAuthenticationProvider uses a PasswordEncoder (here, BCryptPasswordEncoder) to validate
        the password entered by the user during login.
        When a user provides their password, the provider:
        Hashes the provided password using BCryptPasswordEncoder.
        Compares the hashed value to the stored hashed password retrieved by UserDetailsService.
         */
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        return authProvider;
        /*
        3. Returning an Authentication Object
        What it does:

        If the user credentials are valid (i.e., username exists and password matches), the
        DaoAuthenticationProvider returns an Authentication object representing the authenticated
        user.

        Why it's needed:
        The Authentication object is stored in the SecurityContextHolder and used for access
        control (e.g., to determine if the user can access specific resources).
         */
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}