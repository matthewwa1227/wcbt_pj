package com.casualapp.backend.dto.attendance;

import com.casualapp.backend.model.AttendanceStatus;

public class AttendanceRequest {

    private Long recordedByUserId;
    private AttendanceStatus status;
    private Integer lateMinutes;
    private String reason;

    public AttendanceRequest() {
    }

    public AttendanceRequest(
            Long recordedByUserId,
            AttendanceStatus status,
            Integer lateMinutes,
            String reason
    ) {
        this.recordedByUserId = recordedByUserId;
        this.status = status;
        this.lateMinutes = lateMinutes;
        this.reason = reason;
    }

    public Long getRecordedByUserId() {
        return recordedByUserId;
    }

    public void setRecordedByUserId(
            Long recordedByUserId
    ) {
        this.recordedByUserId = recordedByUserId;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(
            AttendanceStatus status
    ) {
        this.status = status;
    }

    public Integer getLateMinutes() {
        return lateMinutes;
    }

    public void setLateMinutes(
            Integer lateMinutes
    ) {
        this.lateMinutes = lateMinutes;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(
            String reason
    ) {
        this.reason = reason;
    }
}