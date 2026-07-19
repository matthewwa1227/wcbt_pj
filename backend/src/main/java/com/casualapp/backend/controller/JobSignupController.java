package com.casualapp.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.casualapp.backend.model.AttendanceStatus;
import com.casualapp.backend.model.Job;
import com.casualapp.backend.model.JobAttendance;
import com.casualapp.backend.model.JobSignup;
import com.casualapp.backend.model.JobStatus;
import com.casualapp.backend.model.Role;
import com.casualapp.backend.model.SignupStatus;
import com.casualapp.backend.model.User;
import com.casualapp.backend.repository.JobAttendanceRepository;
import com.casualapp.backend.repository.JobRepository;
import com.casualapp.backend.repository.JobSignupRepository;
import com.casualapp.backend.repository.UserRepository;

@RestController
@RequestMapping("/api/signups")
public class JobSignupController {

    private final JobSignupRepository jobSignupRepository;
    private final JobAttendanceRepository jobAttendanceRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public JobSignupController(
            JobSignupRepository jobSignupRepository,
            JobAttendanceRepository jobAttendanceRepository,
            UserRepository userRepository,
            JobRepository jobRepository
    ) {
        this.jobSignupRepository = jobSignupRepository;
        this.jobAttendanceRepository = jobAttendanceRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    @GetMapping
    public List<JobSignup> getAllSignups() {
        return jobSignupRepository.findAll();
    }

    @GetMapping("/worker/{workerId}")
    public List<JobSignup> getWorkerSignups(
            @PathVariable Long workerId
    ) {
        requireRole(workerId, Role.WORKER, "Worker");

        return jobSignupRepository.findByWorkerId(workerId);
    }

    @GetMapping("/job/{jobId}")
    public List<JobSignup> getJobSignups(
            @PathVariable Long jobId,
            @RequestParam Long coordinatorId
    ) {
        User coordinator = requireRole(
                coordinatorId,
                Role.COORDINATOR,
                "Coordinator"
        );

        Job job = requireJob(jobId);

        requireCoordinatorOwnsJob(coordinator, job);

        return jobSignupRepository.findByJobId(jobId);
    }

    @GetMapping("/coordinator/{coordinatorId}")
    public List<JobSignup> getCoordinatorSignups(
            @PathVariable Long coordinatorId
    ) {
        requireRole(
                coordinatorId,
                Role.COORDINATOR,
                "Coordinator"
        );

        return jobSignupRepository.findByJobCoordinatorId(coordinatorId);
    }

    @PostMapping
    @Transactional
    public JobSignup signUp(
            @RequestParam Long workerId,
            @RequestParam Long jobId
    ) {
        User worker = requireRole(
                workerId,
                Role.WORKER,
                "Worker"
        );

        Job job = requireJob(jobId);

        if (job.getStatus() != JobStatus.OPEN) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Job is not open for applications"
            );
        }

        if (job.getFilledSlots() >= job.getTotalSlots()) {
            job.setStatus(JobStatus.FULL);
            jobRepository.save(job);

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Job is already full"
            );
        }

        if (jobSignupRepository
                .findByJobIdAndWorkerId(jobId, workerId)
                .isPresent()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Worker already signed up for this job"
            );
        }

        JobSignup signup = new JobSignup();
        signup.setWorker(worker);
        signup.setJob(job);
        signup.setStatus(SignupStatus.PENDING);

        return jobSignupRepository.save(signup);
    }

    @PutMapping("/{signupId}/approve")
    @Transactional
    public JobSignup approveSignup(
            @PathVariable Long signupId,
            @RequestParam Long coordinatorId,
            @RequestParam(required = false) String reason
    ) {
        JobSignup signup = requireSignup(signupId);

        User coordinator = requireRole(
                coordinatorId,
                Role.COORDINATOR,
                "Coordinator"
        );

        Job job = signup.getJob();

        requireCoordinatorOwnsJob(coordinator, job);
        requirePendingSignup(signup);

        if (job.getStatus() != JobStatus.OPEN) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Job is not open"
            );
        }

        if (job.getFilledSlots() >= job.getTotalSlots()) {
            job.setStatus(JobStatus.FULL);
            jobRepository.save(job);

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Job is already full"
            );
        }

        signup.setStatus(SignupStatus.APPROVED);
        signup.setActionedBy(coordinator);
        signup.setActionReason(
                hasText(reason) ? reason.trim() : "Application approved"
        );

        job.setFilledSlots(job.getFilledSlots() + 1);

        if (job.getFilledSlots() >= job.getTotalSlots()) {
            job.setStatus(JobStatus.FULL);
        }

        jobRepository.save(job);

        return jobSignupRepository.save(signup);
    }

    @PutMapping("/{signupId}/reject")
    @Transactional
    public JobSignup rejectSignup(
            @PathVariable Long signupId,
            @RequestParam Long coordinatorId,
            @RequestParam(required = false) String reason
    ) {
        JobSignup signup = requireSignup(signupId);

        User coordinator = requireRole(
                coordinatorId,
                Role.COORDINATOR,
                "Coordinator"
        );

        requireCoordinatorOwnsJob(
                coordinator,
                signup.getJob()
        );

        requirePendingSignup(signup);

        signup.setStatus(SignupStatus.REJECTED);
        signup.setActionedBy(coordinator);
        signup.setActionReason(
                hasText(reason) ? reason.trim() : "Application rejected"
        );

        return jobSignupRepository.save(signup);
    }

    @PutMapping("/{signupId}/attend")
    @Transactional
    public JobAttendance markAttendance(
            @PathVariable Long signupId,
            @RequestParam Long recordedByUserId,
            @RequestParam(defaultValue = "COMPLETED")
            AttendanceStatus status,
            @RequestParam(defaultValue = "0")
            Integer lateMinutes,
            @RequestParam(required = false)
            String reason
    ) {
        JobSignup signup = requireSignup(signupId);

        if (signup.getStatus() != SignupStatus.APPROVED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Signup must be approved before attendance can be recorded"
            );
        }

        User coordinator = requireRole(
                recordedByUserId,
                Role.COORDINATOR,
                "Coordinator"
        );

        Job job = signup.getJob();
        User worker = signup.getWorker();

        requireCoordinatorOwnsJob(coordinator, job);

        if (jobAttendanceRepository
                .findByJobIdAndWorkerId(job.getId(), worker.getId())
                .isPresent()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Attendance already recorded for this worker and job"
            );
        }

        int validatedLateMinutes = validateLateMinutes(
                status,
                lateMinutes
        );

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
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Signup not found"
                ));
    }

    private Job requireJob(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job not found"
                ));
    }

    private User requireRole(
            Long userId,
            Role requiredRole,
            String userType
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        userType + " not found"
                ));

        if (user.getRole() != requiredRole) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    userType + " role is required"
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
                || !job.getCoordinator().getId()
                        .equals(coordinator.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Coordinator does not own this job"
            );
        }
    }

    private void requirePendingSignup(JobSignup signup) {
        if (signup.getStatus() != SignupStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only pending applications can be approved or rejected"
            );
        }
    }

    private int validateLateMinutes(
            AttendanceStatus status,
            Integer lateMinutes
    ) {
        int value = lateMinutes == null ? 0 : lateMinutes;

        if (value < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Late minutes cannot be negative"
            );
        }

        if (status == AttendanceStatus.LATE && value <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Late minutes must be greater than zero for LATE attendance"
            );
        }

        if (status != AttendanceStatus.LATE) {
            return 0;
        }

        return value;
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