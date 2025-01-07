package org.springboot.security.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.UniqueElements;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Email(message = "Enter a valid email")

    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "Password can't be null")
    private String password;

    @JsonIgnore
    private String verificationToken;  // To store the email verification token
    @JsonIgnore
    private boolean verified;  // To check if the user is verified

    @JsonIgnore
        @Enumerated(EnumType.STRING)
        private Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }



    public User() {
        super();
    }

    public User(int id, String email, String password, String verificationToken, boolean verified) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.verificationToken = verificationToken;
        this.verified = verified;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String username) {
        this.email = username;
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
                ", username='" + email + '\'' +
                ", password='" + password + '\'' +
                ", verificationToken='" + verificationToken + '\'' +
                ", verified=" + verified +
                ", role=" + role +
                '}';
    }
}
