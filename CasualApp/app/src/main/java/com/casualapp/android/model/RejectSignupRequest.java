package com.casualapp.android.model;

public class RejectSignupRequest {

    private Long coordinatorId;
    private String reason;

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

    public String getReason() {
        return reason;
    }
}