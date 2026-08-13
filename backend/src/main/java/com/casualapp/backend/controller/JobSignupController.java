package com.casualapp.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.casualapp.backend.dto.attendance.AttendanceResponse;
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
        return jobSignupService.getWorkerSignups(workerId);
    }

    @GetMapping("/job/{jobId}")
    public List<SignupResponse> getJobSignups(
            @PathVariable Long jobId,
            @RequestParam Long coordinatorId
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
                .getCoordinatorSignups(coordinatorId);
    }

    @PostMapping
    public SignupResponse signUp(
            @RequestParam Long workerId,
            @RequestParam Long jobId
    ) {
        return jobSignupService.signUp(
                workerId,
                jobId
        );
    }

    @PutMapping("/{signupId}/approve")
    public SignupResponse approveSignup(
            @PathVariable Long signupId,
            @RequestParam Long coordinatorId,
            @RequestParam(required = false) String reason
    ) {
        return jobSignupService.approveSignup(
                signupId,
                coordinatorId,
                reason
        );
    }

    @PutMapping("/{signupId}/reject")
    public SignupResponse rejectSignup(
            @PathVariable Long signupId,
            @RequestParam Long coordinatorId,
            @RequestParam(required = false) String reason
    ) {
        return jobSignupService.rejectSignup(
                signupId,
                coordinatorId,
                reason
        );
    }

        @PutMapping("/{signupId}/attend")
        public AttendanceResponse markAttendance(
                @PathVariable Long signupId,
                @RequestParam Long recordedByUserId,
                @RequestParam(defaultValue = "COMPLETED")
                AttendanceStatus status,
                @RequestParam(defaultValue = "0")
                Integer lateMinutes,
                @RequestParam(required = false)
                String reason
        ) {
        return attendanceService.markAttendance(
                signupId,
                recordedByUserId,
                status,
                lateMinutes,
                reason
        );
        }
}