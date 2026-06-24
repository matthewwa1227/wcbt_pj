package com.casualapp.android.model;

public class JobSignup {
    private Long id;
    private User worker;
    private Job job;
    private String status;
    private String signupTime;
    private String updatedAt;
    private String actionReason;
    private User actionedBy;

    // Default constructor
    public JobSignup() {}

    // All-args constructor
    public JobSignup(Long id, User worker, Job job, String status, String signupTime,
                     String updatedAt, String actionReason, User actionedBy) {
        this.id = id;
        this.worker = worker;
        this.job = job;
        this.status = status;
        this.signupTime = signupTime;
        this.updatedAt = updatedAt;
        this.actionReason = actionReason;
        this.actionedBy = actionedBy;
    }

    // Boolean helper methods
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

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getWorker() { return worker; }
    public void setWorker(User worker) { this.worker = worker; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSignupTime() { return signupTime; }
    public void setSignupTime(String signupTime) { this.signupTime = signupTime; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getActionReason() { return actionReason; }
    public void setActionReason(String actionReason) { this.actionReason = actionReason; }

    public User getActionedBy() { return actionedBy; }
    public void setActionedBy(User actionedBy) { this.actionedBy = actionedBy; }

    @Override
    public String toString() {
        return "JobSignup{" + "id=" + id + ", status='" + status + '\'' + '}';
    }
}