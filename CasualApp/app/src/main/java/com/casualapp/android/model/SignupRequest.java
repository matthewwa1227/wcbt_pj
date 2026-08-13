package com.casualapp.android.model;

public class SignupRequest {

    private Long workerId;
    private Long jobId;

    public SignupRequest(
            Long workerId,
            Long jobId
    ) {
        this.workerId = workerId;
        this.jobId = jobId;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public Long getJobId() {
        return jobId;
    }
}