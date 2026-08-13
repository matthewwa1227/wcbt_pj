package com.casualapp.backend.dto.signup;

public class ApproveSignupRequest {

    private Long coordinatorId;
    private String reason;

    public ApproveSignupRequest() {
    }

    public ApproveSignupRequest(
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