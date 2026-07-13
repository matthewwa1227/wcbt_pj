package com.casualapp.android.model;

public class User implements java.io.Serializable {
    private Long id;
    private String phoneNumber;
    private String name;
    private Role role;          // Changed from String to Role
    private String createdAt;

    // Default constructor
    public User() {}

    // All-args constructor
    public User(Long id, String phoneNumber, String name, Role role, String createdAt) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Boolean helper methods
    public boolean isCoordinator() {
        return role == Role.COORDINATOR;
    }

    public boolean isWorker() {
        return role == Role.WORKER;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name='" + name + '\'' + ", role=" + role + '}';
    }
}