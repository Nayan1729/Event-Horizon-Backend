package org.springboot.security.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class User {

    @Id
    private String id;
    private String username;
    private String password;

    private String verificationToken;  // To store the email verification token
    private boolean verified;  // To check if the user is verified

    public User() {
        super();
    }

    public User(String id, String username, String password, String verificationToken, boolean verified) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.verificationToken = verificationToken;
        this.verified = verified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", verificationToken='" + verificationToken + '\'' +
                ", verified=" + verified +
                '}';
    }
}
