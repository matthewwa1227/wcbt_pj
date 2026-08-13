package com.casualapp.backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casualapp.backend.controller.ApiException;
import com.casualapp.backend.model.AttendanceStatus;
import com.casualapp.backend.model.Job;
import com.casualapp.backend.model.JobAttendance;
import com.casualapp.backend.model.JobSignup;
import com.casualapp.backend.model.Role;
import com.casualapp.backend.model.SignupStatus;
import com.casualapp.backend.model.User;
import com.casualapp.backend.repository.JobAttendanceRepository;
import com.casualapp.backend.repository.JobSignupRepository;
import com.casualapp.backend.repository.UserRepository;

@Service
public class AttendanceService {

    private final JobAttendanceRepository jobAttendanceRepository;
    private final JobSignupRepository jobSignupRepository;
    private final UserRepository userRepository;

    public AttendanceService(
            JobAttendanceRepository jobAttendanceRepository,
            JobSignupRepository jobSignupRepository,
            UserRepository userRepository
    ) {
        this.jobAttendanceRepository = jobAttendanceRepository;
        this.jobSignupRepository = jobSignupRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public JobAttendance markAttendance(
            Long signupId,
            Long recordedByUserId,
            AttendanceStatus status,
            Integer lateMinutes,
            String reason
    ) {
        JobSignup signup = requireSignup(signupId);

        if (signup.getStatus() != SignupStatus.APPROVED) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "SIGNUP_NOT_APPROVED",
                    "Signup must be approved before attendance can be recorded"
            );
        }

        User coordinator = requireCoordinator(recordedByUserId);

        Job job = signup.getJob();
        User worker = signup.getWorker();

        requireCoordinatorOwnsJob(coordinator, job);

        if (jobAttendanceRepository
                .findByJobIdAndWorkerId(
                        job.getId(),
                        worker.getId()
                )
                .isPresent()) {

            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "ATTENDANCE_ALREADY_RECORDED",
                    "Attendance already recorded for this worker and job"
            );
        }

        int validatedLateMinutes =
                validateLateMinutes(status, lateMinutes);

        JobAttendance attendance = new JobAttendance();
        attendance.setJob(job);
        attendance.setWorker(worker);
        attendance.setRecordedBy(coordinator);
        attendance.setStatus(status);
        attendance.setLateMinutes(validatedLateMinutes);
        attendance.setNotes(
                hasText(reason)
                        ? reason.trim()
                        : defaultAttendanceNote(status)
        );

        return jobAttendanceRepository.save(attendance);
    }

    private JobSignup requireSignup(Long signupId) {
        return jobSignupRepository.findById(signupId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "SIGNUP_NOT_FOUND",
                        "Signup not found"
                ));
    }

    private User requireCoordinator(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "COORDINATOR_NOT_FOUND",
                        "Coordinator not found"
                ));

        if (user.getRole() != Role.COORDINATOR) {
            throw new ApiException(
                    HttpStatus.FORBIDDEN,
                    "COORDINATOR_ROLE_REQUIRED",
                    "Coordinator role is required"
            );
        }

        return user;
    }

    private void requireCoordinatorOwnsJob(
            User coordinator,
            Job job
    ) {
        if (job.getCoordinator() == null
                || job.getCoordinator().getId() == null
                || !job.getCoordinator()
                        .getId()
                        .equals(coordinator.getId())) {

            throw new ApiException(
                    HttpStatus.FORBIDDEN,
                    "COORDINATOR_NOT_JOB_OWNER",
                    "Coordinator does not own this job"
            );
        }
    }

    private int validateLateMinutes(
            AttendanceStatus status,
            Integer lateMinutes
    ) {
        int value = lateMinutes == null ? 0 : lateMinutes;

        if (value < 0) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_LATE_MINUTES",
                    "Late minutes cannot be negative"
            );
        }

        if (status == AttendanceStatus.LATE && value <= 0) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_LATE_MINUTES",
                    "Late minutes must be greater than zero for LATE attendance"
            );
        }

        return status == AttendanceStatus.LATE ? value : 0;
    }

    private String defaultAttendanceNote(
            AttendanceStatus status
    ) {
        return switch (status) {
            case COMPLETED -> "Worker completed the shift";
            case LATE -> "Worker arrived late";
            case NO_SHOW -> "Worker did not attend";
        };
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}