package org.springboot.security.controllers;

import org.springboot.security.entities.User;
import org.springboot.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        System.out.println("User in register controller: "+user);
        return this.userService.registerUser(user);
    }
    @PostMapping("/login")
    public String login(@RequestBody User user) {
        System.out.println(user);

        return this.userService.verifyUser(user);
    }
}
