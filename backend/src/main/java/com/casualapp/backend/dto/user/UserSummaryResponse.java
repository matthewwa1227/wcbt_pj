package com.casualapp.backend.dto.user;

import java.time.LocalDateTime;

import com.casualapp.backend.model.Role;
import com.casualapp.backend.model.User;

public class UserSummaryResponse {

    private Long id;
    private String phoneNumber;
    private String name;
    private Role role;
    private LocalDateTime createdAt;

    public UserSummaryResponse() {
    }

    public UserSummaryResponse(
            Long id,
            String phoneNumber,
            String name,
            Role role,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static UserSummaryResponse from(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getPhoneNumber(),
                user.getName(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}