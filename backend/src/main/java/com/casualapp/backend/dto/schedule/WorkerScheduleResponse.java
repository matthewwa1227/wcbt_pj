package com.casualapp.backend.dto.schedule;

import java.util.ArrayList;
import java.util.List;

public class WorkerScheduleResponse {

    private Long workerId;
    private String generatedAt;
    private List<WorkerScheduleItemResponse> upcoming = new ArrayList<>();
    private List<WorkerScheduleItemResponse> completed = new ArrayList<>();

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

    public List<WorkerScheduleItemResponse> getUpcoming() {
        return upcoming;
    }

    public void setUpcoming(List<WorkerScheduleItemResponse> upcoming) {
        this.upcoming = upcoming == null ? new ArrayList<>() : upcoming;
    }

    public List<WorkerScheduleItemResponse> getCompleted() {
        return completed;
    }

    public void setCompleted(List<WorkerScheduleItemResponse> completed) {
        this.completed = completed == null ? new ArrayList<>() : completed;
    }
}
