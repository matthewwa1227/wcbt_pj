package com.casualapp.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casualapp.backend.dto.attendance.AttendanceRequest;
import com.casualapp.backend.dto.attendance.AttendanceResponse;
import com.casualapp.backend.dto.signup.ApproveSignupRequest;
import com.casualapp.backend.dto.signup.RejectSignupRequest;
import com.casualapp.backend.dto.signup.SignupActionResponse;
import com.casualapp.backend.dto.signup.SignupRequest;
import com.casualapp.backend.dto.signup.SignupResponse;
import com.casualapp.backend.model.AttendanceStatus;
import com.casualapp.backend.service.AttendanceService;
import com.casualapp.backend.service.JobSignupService;

@RestController
@RequestMapping("/api/signups")
public class JobSignupController {

    private final JobSignupService jobSignupService;
    private final AttendanceService attendanceService;

    public JobSignupController(
            JobSignupService jobSignupService,
            AttendanceService attendanceService
    ) {
        this.jobSignupService = jobSignupService;
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public List<SignupResponse> getAllSignups() {

        return jobSignupService.getAllSignups();
    }

    @GetMapping("/worker/{workerId}")
    public List<SignupResponse> getWorkerSignups(
            @PathVariable Long workerId
    ) {

        return jobSignupService.getWorkerSignups(
                workerId
        );
    }

    @GetMapping("/job/{jobId}")
    public List<SignupResponse> getJobSignups(
            @PathVariable Long jobId,
            Long coordinatorId
    ) {

        return jobSignupService.getJobSignups(
                jobId,
                coordinatorId
        );
    }

    @GetMapping("/coordinator/{coordinatorId}")
    public List<SignupResponse> getCoordinatorSignups(
            @PathVariable Long coordinatorId
    ) {

        return jobSignupService
                .getCoordinatorSignups(
                        coordinatorId
                );
    }

    @PostMapping
    public SignupActionResponse signUp(
            @RequestBody SignupRequest request
    ) {

        requireId(
                request.getWorkerId(),
                "workerId"
        );

        requireId(
                request.getJobId(),
                "jobId"
        );

        return jobSignupService.signUp(
                request.getWorkerId(),
                request.getJobId()
        );
    }

    @PutMapping("/{signupId}/approve")
    public SignupActionResponse approveSignup(
            @PathVariable Long signupId,
            @RequestBody ApproveSignupRequest request
    ) {

        requireId(
                request.getCoordinatorId(),
                "coordinatorId"
        );

        return jobSignupService.approveSignup(
                signupId,
                request.getCoordinatorId(),
                request.getReason()
        );
    }

    @PutMapping("/{signupId}/reject")
    public SignupActionResponse rejectSignup(
            @PathVariable Long signupId,
            @RequestBody RejectSignupRequest request
    ) {

        requireId(
                request.getCoordinatorId(),
                "coordinatorId"
        );

        return jobSignupService.rejectSignup(
                signupId,
                request.getCoordinatorId(),
                request.getReason()
        );
    }

    @PutMapping("/{signupId}/attend")
    public AttendanceResponse markAttendance(
            @PathVariable Long signupId,
            @RequestBody AttendanceRequest request
    ) {

        requireId(
                request.getRecordedByUserId(),
                "recordedByUserId"
        );

        AttendanceStatus status =
                request.getStatus() != null
                        ? request.getStatus()
                        : AttendanceStatus.COMPLETED;

        Integer lateMinutes =
                request.getLateMinutes() != null
                        ? request.getLateMinutes()
                        : 0;

        return attendanceService.markAttendance(
                signupId,
                request.getRecordedByUserId(),
                status,
                lateMinutes,
                request.getReason()
        );
    }

    private void requireId(
            Long id,
            String fieldName
    ) {

        if (id == null || id <= 0) {

            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_REQUEST",
                    fieldName + " is required"
            );
        }
    }
}