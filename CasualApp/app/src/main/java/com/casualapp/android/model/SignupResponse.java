package com.casualapp.android.model;

public class SignupResponse {

    private Long id;

    private Long workerId;
    private String workerName;
    private String workerPhoneNumber;

    private Long jobId;
    private String jobTitle;
    private String jobLocation;
    private String jobDate;

    private String status;
    private String signupTime;
    private String updatedAt;
    private String actionReason;

    private Long actionedByUserId;
    private String actionedByName;

    public SignupResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getWorkerPhoneNumber() {
        return workerPhoneNumber;
    }

    public void setWorkerPhoneNumber(String workerPhoneNumber) {
        this.workerPhoneNumber = workerPhoneNumber;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public String getJobDate() {
        return jobDate;
    }

    public void setJobDate(String jobDate) {
        this.jobDate = jobDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSignupTime() {
        return signupTime;
    }

    public void setSignupTime(String signupTime) {
        this.signupTime = signupTime;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getActionReason() {
        return actionReason;
    }

    public void setActionReason(String actionReason) {
        this.actionReason = actionReason;
    }

    public Long getActionedByUserId() {
        return actionedByUserId;
    }

    public void setActionedByUserId(Long actionedByUserId) {
        this.actionedByUserId = actionedByUserId;
    }

    public String getActionedByName() {
        return actionedByName;
    }

    public void setActionedByName(String actionedByName) {
        this.actionedByName = actionedByName;
    }

    public boolean isPending() {
        return "PENDING".equals(status);
    }

    public boolean isApproved() {
        return "APPROVED".equals(status);
    }

    public boolean isRejected() {
        return "REJECTED".equals(status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }

    @Override
    public String toString() {
        return "SignupResponse{" +
                "id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}