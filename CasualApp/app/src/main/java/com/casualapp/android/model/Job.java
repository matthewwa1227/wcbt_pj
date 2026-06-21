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

    public Job() {
    }

    public Job(
            Long id,
            String title,
            String description,
            String location,
            String jobDate,
            int totalSlots,
            int filledSlots,
            String status,
            User coordinator,
            String createdAt
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.jobDate = jobDate;
        this.totalSlots = totalSlots;
        this.filledSlots = filledSlots;
        this.status = status;
        this.coordinator = coordinator;
        this.createdAt = createdAt;
    }

    public Job(
            String title,
            String description,
            String location,
            String jobDate,
            int totalSlots,
            int filledSlots,
            String status
    ) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.jobDate = jobDate;
        this.totalSlots = totalSlots;
        this.filledSlots = filledSlots;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getJobDate() {
        return jobDate;
    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public int getFilledSlots() {
        return filledSlots;
    }

    public String getStatus() {
        return status;
    }

    public User getCoordinator() {
        return coordinator;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getAvailableSlots() {
        return totalSlots - filledSlots;
    }

    public boolean isOpen() {
        return "OPEN".equalsIgnoreCase(status) && getAvailableSlots() > 0;
    }

    public boolean isFull() {
        return filledSlots >= totalSlots;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setJobDate(String jobDate) {
        this.jobDate = jobDate;
    }

    public void setTotalSlots(int totalSlots) {
        this.totalSlots = totalSlots;
    }

    public void setFilledSlots(int filledSlots) {
        this.filledSlots = filledSlots;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCoordinator(User coordinator) {
        this.coordinator = coordinator;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}