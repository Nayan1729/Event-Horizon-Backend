package org.springboot.security.services;

import org.springboot.security.entities.UserPrincipal;
import org.springboot.security.entities.User;
import org.springboot.security.repositories.MyUserDetailsRepository;
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
            User user = this.myUserDetailsRepository.findByEmail(email);
            if (user == null) {
                System.out.println("User not found");
            }
            return new UserPrincipal(user);
    }
}
