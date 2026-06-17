package com.casualapp.android.model;

public class JobSignup {
    private Long id;
    private User worker;
    private Job job;
    private String status;
    private String signupTime;

    public Long getId() { return id; }
    public String getStatus() { return status; }
}