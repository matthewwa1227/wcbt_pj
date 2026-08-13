package com.casualapp.backend.dto.signup;

public class SignupRequest {

    private Long workerId;
    private Long jobId;

    public SignupRequest() {
    }

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

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
}