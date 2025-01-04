package com.example.mynadma.models;

public class User {
    private String userId;
    private String username;
    private String email;
    private String contact;
    private String password;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userId, String username, String email, String contact, String password) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.contact = contact;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }

    public String getPassword() {
        return password;
    }
}
