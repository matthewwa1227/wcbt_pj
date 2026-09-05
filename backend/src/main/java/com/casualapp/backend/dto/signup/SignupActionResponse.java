package com.casualapp.backend.dto.signup;

import java.time.LocalDateTime;

public class SignupActionResponse {

    private Long id;
    private String status;
    private LocalDateTime updatedAt;
    private String actionReason;

    public SignupActionResponse() {
    }

    public SignupActionResponse(
            Long id,
            String status,
            LocalDateTime updatedAt,
            String actionReason
    ) {
        this.id = id;
        this.status = status;
        this.updatedAt = updatedAt;
        this.actionReason = actionReason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getActionReason() {
        return actionReason;
    }

    public void setActionReason(String actionReason) {
        this.actionReason = actionReason;
    }
}