package com.casualapp.android.model;

public class User {
    private Long id;
    private String phoneNumber;
    private String name;
    private Role role;
    private String createdAt;

    public User() {
    }

    public User(Long id, String phoneNumber, String name, Role role, String createdAt) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.role = role;
        this.createdAt = createdAt;
    }

    public User(String phoneNumber, String name, Role role) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isWorker() {
        return role == Role.WORKER;
    }

    public boolean isCoordinator() {
        return role == Role.COORDINATOR || role == Role.COORDINATOR;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}