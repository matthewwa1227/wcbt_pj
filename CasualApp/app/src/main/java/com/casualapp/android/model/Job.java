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

    // Default constructor
    public Job() {}

    // All-args constructor
    public Job(Long id, String title, String description, String location, String jobDate,
               int totalSlots, int filledSlots, String status, User coordinator, String createdAt) {
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

    // Boolean helper methods
    public boolean isOpen() {
        return "OPEN".equals(status);
    }

    public boolean isFull() {
        return filledSlots >= totalSlots;
    }

    public boolean hasAvailableSlots() {
        return filledSlots < totalSlots;
    }

    public boolean isClosed() {
        return "CLOSED".equals(status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getJobDate() { return jobDate; }
    public void setJobDate(String jobDate) { this.jobDate = jobDate; }

    public int getTotalSlots() { return totalSlots; }
    public void setTotalSlots(int totalSlots) { this.totalSlots = totalSlots; }

    public int getFilledSlots() { return filledSlots; }
    public void setFilledSlots(int filledSlots) { this.filledSlots = filledSlots; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public User getCoordinator() { return coordinator; }
    public void setCoordinator(User coordinator) { this.coordinator = coordinator; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Job{" + "id=" + id + ", title='" + title + '\'' + ", status='" + status + '\'' + '}';
    }
}