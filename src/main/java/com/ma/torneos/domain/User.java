package com.ma.torneos.domain;

public class User {
    private final String username;
    private final Role role;
    private final String email;

    public User(String username, Role role, String email) {
        this.username = username;
        this.role = role;
        this.email = email;
    }

    public String getUsername() { return username; }
    public Role getRole() { return role; }
    public String getEmail() { return email; }
}
