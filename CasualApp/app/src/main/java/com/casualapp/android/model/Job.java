package com.casualapp.android.model;

public class Job {
    private Long id;
    private String title;
    private String description;
    private String location;
    private String jobDate;
    private int totalSlots;
    private int filledSlots;
    private String status;
    private User coordinator;
    private String createdAt;

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public int getTotalSlots() { return totalSlots; }
    public int getFilledSlots() { return filledSlots; }
    public String getStatus() { return status; }
    public User getCoordinator() { return coordinator; }
}