package com.casualapp.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casualapp.backend.model.AttendanceStatus;
import com.casualapp.backend.model.JobAttendance;

public interface JobAttendanceRepository extends JpaRepository<JobAttendance, Long> {
    Optional<JobAttendance> findByJobIdAndWorkerId(Long jobId, Long workerId);

    List<JobAttendance> findByJobId(Long jobId);

    List<JobAttendance> findByWorkerId(Long workerId);

    List<JobAttendance> findByRecordedById(Long recordedById);

    List<JobAttendance> findByStatus(AttendanceStatus status);
}