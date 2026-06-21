package com.casualapp.backend.persistence.ports;

import com.casualapp.backend.model.AttendanceStatus;
import com.casualapp.backend.model.JobAttendance;
import com.casualapp.backend.persistence.filters.JobAttendanceFilter;

import java.util.List;
import java.util.Optional;

public interface JobAttendancePersistence {
    Optional<JobAttendance> findById(Long attendanceId);

    Optional<JobAttendance> findByJobIdAndWorkerId(Long jobId, Long workerId);

    List<JobAttendance> findAll();

    List<JobAttendance> findByFilter(JobAttendanceFilter filter);

    List<JobAttendance> findByJobId(Long jobId);

    List<JobAttendance> findByWorkerId(Long workerId);

    List<JobAttendance> findByRecordedById(Long recordedById);

    List<JobAttendance> findByStatus(AttendanceStatus status);

    JobAttendance save(JobAttendance attendance);

    JobAttendance update(JobAttendance attendance);

    boolean deleteById(Long attendanceId);
}