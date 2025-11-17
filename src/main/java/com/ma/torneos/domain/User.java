package com.ma.torneos.domain;

public class User {
    private final Long id;
    private final String username;
    private final Role role;
    private final String email;

    public User(Long id, String username, Role role, String email) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() { return username; }
    public Role getRole() { return role; }
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
