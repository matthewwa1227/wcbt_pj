package com.casualapp.android.model;

public class JobAttendance {
    private Long id;
    private User worker;
    private Job job;
    private String status;
    private String recordedAt;
    private User recordedBy;
    private int lateMinutes;
    private String notes;

    // Default constructor
    public JobAttendance() {}

    // All-args constructor
    public JobAttendance(Long id, User worker, Job job, String status, String recordedAt,
                         User recordedBy, int lateMinutes, String notes) {
        this.id = id;
        this.worker = worker;
        this.job = job;
        this.status = status;
        this.recordedAt = recordedAt;
        this.recordedBy = recordedBy;
        this.lateMinutes = lateMinutes;
        this.notes = notes;
    }

    // Boolean helper methods
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public boolean isLate() {
        return "LATE".equals(status);
    }

    public boolean isNoShow() {
        return "NO_SHOW".equals(status);
    }

    public boolean hasLateMinutes() {
        return lateMinutes > 0;
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

    public String getRecordedAt() { return recordedAt; }
    public void setRecordedAt(String recordedAt) { this.recordedAt = recordedAt; }

    public User getRecordedBy() { return recordedBy; }
    public void setRecordedBy(User recordedBy) { this.recordedBy = recordedBy; }

    public int getLateMinutes() { return lateMinutes; }
    public void setLateMinutes(int lateMinutes) { this.lateMinutes = lateMinutes; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "JobAttendance{" + "id=" + id + ", status='" + status + '\'' + '}';
    }
}