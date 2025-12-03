package com.commerce.ooad.E_Commerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserSQL {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "state")
    private String state;

    public UserSQL() {}

    public UserSQL(String username, String password, String email, String state) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.state = state;
    }

    public boolean authenticate(String password) {
        return this.password.equals(password);
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getState() {
        return state;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setState(String state) {
        this.state = state;
    }
}