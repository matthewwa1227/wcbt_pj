package com.casualapp.android.model;

import java.math.BigDecimal;

public class CreateJobRequest {

    private Long coordinatorId;

    private String title;

    private String description;

    private String location;

    private String startDateTime;

    private String endDateTime;

    private BigDecimal hourlyRate;

    private Integer totalSlots;


    public CreateJobRequest(
            Long coordinatorId,
            String title,
            String description,
            String location,
            String startDateTime,
            String endDateTime,
            BigDecimal hourlyRate,
            Integer totalSlots
    ) {
        this.coordinatorId = coordinatorId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.hourlyRate = hourlyRate;
        this.totalSlots = totalSlots;
    }


    public Long getCoordinatorId() {
        return coordinatorId;
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

    public String getStartDateTime() {
        return startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public Integer getTotalSlots() {
        return totalSlots;
    }
}