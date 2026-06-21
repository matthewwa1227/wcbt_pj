package com.casualapp.backend.persistence.filters;

import java.time.LocalDateTime;

public class JobFilter {
    private Long coordinatorId;
    private String status;
    private String location;
    private LocalDateTime startFrom;
    private LocalDateTime startTo;
    private Boolean openOnly;

    public Long getCoordinatorId() {
        return coordinatorId;
    }

    public void setCoordinatorId(Long coordinatorId) {
        this.coordinatorId = coordinatorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getStartFrom() {
        return startFrom;
    }

    public void setStartFrom(LocalDateTime startFrom) {
        this.startFrom = startFrom;
    }

    public LocalDateTime getStartTo() {
        return startTo;
    }

    public void setStartTo(LocalDateTime startTo) {
        this.startTo = startTo;
    }

    public Boolean getOpenOnly() {
        return openOnly;
    }

    public void setOpenOnly(Boolean openOnly) {
        this.openOnly = openOnly;
    }
}