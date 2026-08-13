package com.casualapp.android.model;

public class AttendanceRequest {

    private Long recordedByUserId;
    private String status;
    private Integer lateMinutes;
    private String reason;

    public AttendanceRequest(
            Long recordedByUserId,
            String status,
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

    public String getStatus() {
        return status;
    }

    public Integer getLateMinutes() {
        return lateMinutes;
    }

    public String getReason() {
        return reason;
    }
}