package com.casualapp.android.model;

public class JobSignup {
    private Long id;
    private User worker;
    private Job job;
    private String status;
    private String signupTime;

    public JobSignup() {
    }

    public JobSignup(Long id, User worker, Job job, String status, String signupTime) {
        this.id = id;
        this.worker = worker;
        this.job = job;
        this.status = status;
        this.signupTime = signupTime;
    }

    public Long getId() {
        return id;
    }

    public User getWorker() {
        return worker;
    }

    public Job getJob() {
        return job;
    }

    public String getStatus() {
        return status;
    }

    public String getSignupTime() {
        return signupTime;
    }

    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(status);
    }

    public boolean isApproved() {
        return "APPROVED".equalsIgnoreCase(status)
                || "CONFIRMED".equalsIgnoreCase(status)
                || "SIGNED_UP".equalsIgnoreCase(status);
    }

    public boolean isRejected() {
        return "REJECTED".equalsIgnoreCase(status)
                || "CANCELLED".equalsIgnoreCase(status);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setWorker(User worker) {
        this.worker = worker;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSignupTime(String signupTime) {
        this.signupTime = signupTime;
    }
}