package com.casualapp.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDateTime jobDate;

    @Column(nullable = false)
    private int totalSlots;

    @Column(nullable = false)
    private int filledSlots = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status = JobStatus.OPEN;

    @ManyToOne
    @JoinColumn(name = "coordinator_id", nullable = false)
    private User coordinator;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getJobDate() { return jobDate; }
    public void setJobDate(LocalDateTime jobDate) { this.jobDate = jobDate; }

    public int getTotalSlots() { return totalSlots; }
    public void setTotalSlots(int totalSlots) { this.totalSlots = totalSlots; }

    public int getFilledSlots() { return filledSlots; }
    public void setFilledSlots(int filledSlots) { this.filledSlots = filledSlots; }

    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }

    public User getCoordinator() { return coordinator; }
    public void setCoordinator(User coordinator) { this.coordinator = coordinator; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}