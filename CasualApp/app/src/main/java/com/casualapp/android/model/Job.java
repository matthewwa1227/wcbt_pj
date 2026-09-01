package com.casualapp.android.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Job implements Serializable {

    private Long id;

    private String title;

    private String description;

    private String location;

    private String startDateTime;

    private String endDateTime;

    private BigDecimal hourlyRate;

    private int totalSlots;

    private int filledSlots;

    private String status;

    private Long coordinatorId;

    private String coordinatorName;

    private String createdAt;


    public Job() {
    }


    public Job(
            Long id,
            String title,
            String description,
            String location,
            String startDateTime,
            String endDateTime,
            BigDecimal hourlyRate,
            int totalSlots,
            int filledSlots,
            String status,
            Long coordinatorId,
            String coordinatorName,
            String createdAt
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.hourlyRate = hourlyRate;
        this.totalSlots = totalSlots;
        this.filledSlots = filledSlots;
        this.status = status;
        this.coordinatorId = coordinatorId;
        this.coordinatorName = coordinatorName;
        this.createdAt = createdAt;
    }


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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }


    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }


    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }


    public int getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(int totalSlots) {
        this.totalSlots = totalSlots;
    }


    public int getFilledSlots() {
        return filledSlots;
    }

    public void setFilledSlots(int filledSlots) {
        this.filledSlots = filledSlots;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public Long getCoordinatorId() {
        return coordinatorId;
    }

    public void setCoordinatorId(Long coordinatorId) {
        this.coordinatorId = coordinatorId;
    }


    public String getCoordinatorName() {
        return coordinatorName;
    }

    public void setCoordinatorName(String coordinatorName) {
        this.coordinatorName = coordinatorName;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}