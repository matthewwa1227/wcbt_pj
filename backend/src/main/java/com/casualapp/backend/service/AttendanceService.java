package com.casualapp.backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casualapp.backend.controller.ApiException;
import com.casualapp.backend.dto.attendance.AttendanceResponse;
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
    public AttendanceResponse markAttendance(
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
                    "Attendance can only be recorded for approved applications"
            );
        }

        User coordinator = requireCoordinator(
                recordedByUserId
        );

        Job job = signup.getJob();

        requireCoordinatorOwnsJob(
                coordinator,
                job
        );

        User worker = signup.getWorker();

        if (jobAttendanceRepository
                .findByJobIdAndWorkerId(
                        job.getId(),
                        worker.getId()
                )
                .isPresent()) {

            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "ATTENDANCE_ALREADY_RECORDED",
                    "Attendance has already been recorded for this worker and job"
            );
        }

        int normalizedLateMinutes =
                normalizeLateMinutes(
                        status,
                        lateMinutes
                );

        JobAttendance attendance =
                new JobAttendance();

        attendance.setJob(job);
        attendance.setWorker(worker);
        attendance.setRecordedBy(coordinator);
        attendance.setStatus(status);

        attendance.setLateMinutes(
                normalizedLateMinutes
        );

        attendance.setNotes(
                hasText(reason)
                        ? reason.trim()
                        : defaultReason(
                                status,
                                normalizedLateMinutes
                        )
        );

        JobAttendance savedAttendance =
                jobAttendanceRepository.save(
                        attendance
                );

        return toResponse(
                savedAttendance,
                signupId
        );
    }

    private JobSignup requireSignup(
            Long signupId
    ) {

        return jobSignupRepository
                .findById(signupId)
                .orElseThrow(() ->
                        new ApiException(
                                HttpStatus.NOT_FOUND,
                                "SIGNUP_NOT_FOUND",
                                "Signup not found"
                        )
                );
    }

    private User requireCoordinator(
            Long userId
    ) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ApiException(
                                HttpStatus.NOT_FOUND,
                                "COORDINATOR_NOT_FOUND",
                                "Coordinator not found"
                        )
                );

        if (user.getRole()
                != Role.COORDINATOR) {

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

        if (job == null
                || job.getCoordinator() == null
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

    private int normalizeLateMinutes(
            AttendanceStatus status,
            Integer lateMinutes
    ) {

        int minutes =
                lateMinutes == null
                        ? 0
                        : lateMinutes;

        if (minutes < 0) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_LATE_MINUTES",
                    "Late minutes cannot be negative"
            );
        }

        if (status == AttendanceStatus.LATE) {

            if (minutes <= 0) {
                throw new ApiException(
                        HttpStatus.BAD_REQUEST,
                        "INVALID_LATE_MINUTES",
                        "Late attendance requires late minutes greater than zero"
                );
            }

            return minutes;
        }

        return 0;
    }

    private String defaultReason(
            AttendanceStatus status,
            int lateMinutes
    ) {

        if (status == AttendanceStatus.LATE) {
            return "Worker arrived "
                    + lateMinutes
                    + " minutes late";
        }

        if (status == AttendanceStatus.NO_SHOW) {
            return "Worker did not attend";
        }

        return "Shift completed";
    }

    private AttendanceResponse toResponse(
            JobAttendance attendance,
            Long signupId
    ) {

        AttendanceResponse response =
                new AttendanceResponse();

        response.setId(
                attendance.getId()
        );

        response.setSignupId(
                signupId
        );

        if (attendance.getWorker() != null) {

            response.setWorkerId(
                    attendance
                            .getWorker()
                            .getId()
            );
        }

        if (attendance.getJob() != null) {

            response.setJobId(
                    attendance
                            .getJob()
                            .getId()
            );
        }

        response.setStatus(
                attendance.getStatus() == null
                        ? null
                        : attendance
                                .getStatus()
                                .name()
        );

        response.setLateMinutes(
                attendance.getLateMinutes()
        );

        response.setNotes(
                attendance.getNotes()
        );

        response.setRecordedAt(
                attendance.getRecordedAt() == null
                        ? null
                        : attendance
                                .getRecordedAt()
                                .toString()
        );

        if (attendance.getRecordedBy()
                != null) {

            response.setRecordedByUserId(
                    attendance
                            .getRecordedBy()
                            .getId()
            );
        }

        return response;
    }

    private boolean hasText(
            String value
    ) {
        return value != null
                && !value.isBlank();
    }
}