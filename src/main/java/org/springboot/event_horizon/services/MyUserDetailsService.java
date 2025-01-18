package org.springboot.event_horizon.services;

import org.springboot.event_horizon.entities.UserPrincipal;
import org.springboot.event_horizon.entities.User;
import org.springboot.event_horizon.repositories.MyUserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private MyUserDetailsRepository myUserDetailsRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Email:"+email);
            User user = this.myUserDetailsRepository.findByEmail(email).get();
        System.out.println("User:"+user);
            if (user == null) {
                System.out.println("User not found");
            }
            return new UserPrincipal(user);
    }
}
