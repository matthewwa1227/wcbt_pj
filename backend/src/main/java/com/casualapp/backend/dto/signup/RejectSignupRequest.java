package com.casualapp.backend.dto.signup;

public class RejectSignupRequest {

    private Long coordinatorId;
    private String reason;

    public RejectSignupRequest() {
    }

    public RejectSignupRequest(
            Long coordinatorId,
            String reason
    ) {
        this.coordinatorId = coordinatorId;
        this.reason = reason;
    }

    public Long getCoordinatorId() {
        return coordinatorId;
    }

    public void setCoordinatorId(Long coordinatorId) {
        this.coordinatorId = coordinatorId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}