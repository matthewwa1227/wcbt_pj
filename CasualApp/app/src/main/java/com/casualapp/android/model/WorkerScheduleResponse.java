package com.casualapp.android.model;

import java.util.ArrayList;
import java.util.List;

public class WorkerScheduleResponse {

    private Long workerId;
    private String generatedAt;
    private List<WorkerScheduleItem> upcoming = new ArrayList<>();
    private List<WorkerScheduleItem> completed = new ArrayList<>();

    public WorkerScheduleResponse() {
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }

    public List<WorkerScheduleItem> getUpcoming() {
        return upcoming;
    }

    public void setUpcoming(List<WorkerScheduleItem> upcoming) {
        this.upcoming = upcoming == null ? new ArrayList<>() : upcoming;
    }

    public List<WorkerScheduleItem> getCompleted() {
        return completed;
    }

    public void setCompleted(List<WorkerScheduleItem> completed) {
        this.completed = completed == null ? new ArrayList<>() : completed;
    }
}
