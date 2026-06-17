package com.casualapp.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_signups", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"worker_id", "job_id"})
})
public class JobSignup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "worker_id", nullable = false)
    private User worker;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SignupStatus status = SignupStatus.SIGNED_UP;

    private LocalDateTime signupTime = LocalDateTime.now();

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getWorker() { return worker; }
    public void setWorker(User worker) { this.worker = worker; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }

    public SignupStatus getStatus() { return status; }
    public void setStatus(SignupStatus status) { this.status = status; }

    public LocalDateTime getSignupTime() { return signupTime; }
    public void setSignupTime(LocalDateTime signupTime) { this.signupTime = signupTime; }
}