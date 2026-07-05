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

    private final JobSignupRepository signupRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobAttendanceRepository attendanceRepository;

    public JobSignupController(JobSignupRepository signupRepository,
                               UserRepository userRepository,
                               JobRepository jobRepository,
                               JobAttendanceRepository attendanceRepository) {
        this.signupRepository = signupRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @GetMapping
    public List<JobSignup> getAllSignups() {
        return signupRepository.findAll();
    }

    @PostMapping
    @Transactional
    public JobSignup signUp(@RequestParam Long workerId, @RequestParam Long jobId) {
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "WORKER_NOT_FOUND",
                        "Worker not found."
                ));

        if (worker.getRole() != Role.WORKER) {
            throw new ApiException(
                    HttpStatus.FORBIDDEN,
                    "WORKER_ROLE_REQUIRED",
                    "Only workers can sign up for jobs."
            );
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "JOB_NOT_FOUND",
                        "Job not found."
                ));

        if (job.getStatus() == JobStatus.CANCELLED || job.getStatus() == JobStatus.CLOSED) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "JOB_NOT_OPEN",
                    "This job is not open for signup."
            );
        }

        if (job.getStatus() == JobStatus.FULL || job.getFilledSlots() >= job.getTotalSlots()) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "JOB_FULL",
                    "This job is already full."
            );
        }

        signupRepository.findByJobIdAndWorkerId(jobId, workerId)
                .ifPresent(existingSignup -> {
                    throw new ApiException(
                            HttpStatus.CONFLICT,
                            "DUPLICATE_SIGNUP",
                            "This worker has already signed up for this job."
                    );
                });

        JobSignup signup = new JobSignup();
        signup.setWorker(worker);
        signup.setJob(job);
        signup.setStatus(SignupStatus.PENDING);

        return signupRepository.save(signup);
    }

    @PutMapping("/{id}/approve")
    @Transactional
    public JobSignup approveSignup(@PathVariable Long id,
                                   @RequestParam Long coordinatorId,
                                   @RequestParam(required = false) String reason) {
        JobSignup signup = signupRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "SIGNUP_NOT_FOUND",
                        "Signup not found."
                ));

        User coordinator = userRepository.findById(coordinatorId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "COORDINATOR_NOT_FOUND",
                        "Coordinator not found."
                ));

        if (coordinator.getRole() != Role.COORDINATOR && coordinator.getRole() != Role.ADMIN) {
            throw new ApiException(
                    HttpStatus.FORBIDDEN,
                    "COORDINATOR_ROLE_REQUIRED",
                    "Only coordinators or admins can approve signups."
            );
        }

        if (signup.getStatus() == SignupStatus.APPROVED) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "SIGNUP_ALREADY_APPROVED",
                    "This signup has already been approved."
            );
        }

        if (signup.getStatus() == SignupStatus.REJECTED) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "SIGNUP_ALREADY_REJECTED",
                    "Rejected signups cannot be approved."
            );
        }

        if (signup.getStatus() == SignupStatus.CANCELLED) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "SIGNUP_ALREADY_CANCELLED",
                    "Cancelled signups cannot be approved."
            );
        }

        Job job = signup.getJob();

        if (job.getStatus() == JobStatus.CANCELLED || job.getStatus() == JobStatus.CLOSED) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "JOB_NOT_OPEN",
                    "This job is not open for approval."
            );
        }

        if (job.getStatus() == JobStatus.FULL || job.getFilledSlots() >= job.getTotalSlots()) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "JOB_FULL",
                    "This job is already full."
            );
        }

        signup.setStatus(SignupStatus.APPROVED);
        signup.setActionedBy(coordinator);
        signup.setActionReason(reason);

        job.setFilledSlots(job.getFilledSlots() + 1);

        if (job.getFilledSlots() >= job.getTotalSlots()) {
            job.setStatus(JobStatus.FULL);
        }

        jobRepository.save(job);
        return signupRepository.save(signup);
    }

    @PutMapping("/{id}/reject")
    @Transactional
    public JobSignup rejectSignup(@PathVariable Long id,
                                  @RequestParam Long coordinatorId,
                                  @RequestParam(required = false) String reason) {
        JobSignup signup = signupRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "SIGNUP_NOT_FOUND",
                        "Signup not found."
                ));

        User coordinator = userRepository.findById(coordinatorId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "COORDINATOR_NOT_FOUND",
                        "Coordinator not found."
                ));

        if (coordinator.getRole() != Role.COORDINATOR && coordinator.getRole() != Role.ADMIN) {
            throw new ApiException(
                    HttpStatus.FORBIDDEN,
                    "COORDINATOR_ROLE_REQUIRED",
                    "Only coordinators or admins can reject signups."
            );
        }

        if (signup.getStatus() == SignupStatus.APPROVED) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "APPROVED_SIGNUP_CANNOT_BE_REJECTED",
                    "Approved signups cannot be rejected. Cancel the signup instead."
            );
        }

        if (signup.getStatus() == SignupStatus.CANCELLED) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "SIGNUP_ALREADY_CANCELLED",
                    "Cancelled signups cannot be rejected."
            );
        }

        signup.setStatus(SignupStatus.REJECTED);
        signup.setActionedBy(coordinator);
        signup.setActionReason(reason);

        return signupRepository.save(signup);
    }

    @PutMapping("/{id}/cancel")
    @Transactional
    public JobSignup cancelSignup(@PathVariable Long id,
                                  @RequestParam(required = false) Long actionedByUserId,
                                  @RequestParam(required = false) String reason) {
        JobSignup signup = signupRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "SIGNUP_NOT_FOUND",
                        "Signup not found."
                ));

        if (signup.getStatus() == SignupStatus.CANCELLED) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "SIGNUP_ALREADY_CANCELLED",
                    "This signup has already been cancelled."
            );
        }

        boolean wasApproved = signup.getStatus() == SignupStatus.APPROVED;

        signup.setStatus(SignupStatus.CANCELLED);
        signup.setActionReason(reason);

        if (actionedByUserId != null) {
            User actionedBy = userRepository.findById(actionedByUserId)
                    .orElseThrow(() -> new ApiException(
                            HttpStatus.NOT_FOUND,
                            "ACTIONED_BY_USER_NOT_FOUND",
                            "Actioned-by user not found."
                    ));
            signup.setActionedBy(actionedBy);
        }

        if (wasApproved) {
            Job job = signup.getJob();
            int newFilledSlots = Math.max(0, job.getFilledSlots() - 1);
            job.setFilledSlots(newFilledSlots);

            if (job.getStatus() == JobStatus.FULL && newFilledSlots < job.getTotalSlots()) {
                job.setStatus(JobStatus.OPEN);
            }

            jobRepository.save(job);
        }

        return signupRepository.save(signup);
    }

    @PutMapping("/{id}/attend")
    @Transactional
    public JobAttendance markAttended(@PathVariable Long id,
                                      @RequestParam Long recordedByUserId,
                                      @RequestParam(defaultValue = "0") Integer lateMinutes,
                                      @RequestParam(required = false) String notes) {
        JobSignup signup = signupRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "SIGNUP_NOT_FOUND",
                        "Signup not found."
                ));

        if (signup.getStatus() != SignupStatus.APPROVED) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "SIGNUP_NOT_APPROVED",
                    "Only approved signups can have attendance recorded."
            );
        }

        User recordedBy = userRepository.findById(recordedByUserId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "RECORDED_BY_USER_NOT_FOUND",
                        "Recorded-by user not found."
                ));

        if (recordedBy.getRole() != Role.COORDINATOR && recordedBy.getRole() != Role.ADMIN) {
            throw new ApiException(
                    HttpStatus.FORBIDDEN,
                    "COORDINATOR_ROLE_REQUIRED",
                    "Only coordinators or admins can record attendance."
            );
        }

        if (lateMinutes != null && lateMinutes < 0) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_LATE_MINUTES",
                    "Late minutes cannot be negative."
            );
        }

        JobAttendance attendance = attendanceRepository
                .findByJobIdAndWorkerId(signup.getJob().getId(), signup.getWorker().getId())
                .orElse(new JobAttendance());

        attendance.setJob(signup.getJob());
        attendance.setWorker(signup.getWorker());
        attendance.setRecordedBy(recordedBy);
        attendance.setLateMinutes(lateMinutes == null ? 0 : lateMinutes);
        attendance.setNotes(notes);

        if (lateMinutes != null && lateMinutes > 0) {
            attendance.setStatus(AttendanceStatus.LATE);
        } else {
            attendance.setStatus(AttendanceStatus.COMPLETED);
        }

        return attendanceRepository.save(attendance);
    }

    @PutMapping("/{id}/noshow")
    @Transactional
    public JobAttendance markNoShow(@PathVariable Long id,
                                    @RequestParam Long recordedByUserId,
                                    @RequestParam(required = false) String notes) {
        JobSignup signup = signupRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "SIGNUP_NOT_FOUND",
                        "Signup not found."
                ));

        if (signup.getStatus() != SignupStatus.APPROVED) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "SIGNUP_NOT_APPROVED",
                    "Only approved signups can have attendance recorded."
            );
        }

        User recordedBy = userRepository.findById(recordedByUserId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "RECORDED_BY_USER_NOT_FOUND",
                        "Recorded-by user not found."
                ));

        if (recordedBy.getRole() != Role.COORDINATOR && recordedBy.getRole() != Role.ADMIN) {
            throw new ApiException(
                    HttpStatus.FORBIDDEN,
                    "COORDINATOR_ROLE_REQUIRED",
                    "Only coordinators or admins can record attendance."
            );
        }

        JobAttendance attendance = attendanceRepository
                .findByJobIdAndWorkerId(signup.getJob().getId(), signup.getWorker().getId())
                .orElse(new JobAttendance());

        attendance.setJob(signup.getJob());
        attendance.setWorker(signup.getWorker());
        attendance.setRecordedBy(recordedBy);
        attendance.setStatus(AttendanceStatus.NO_SHOW);
        attendance.setLateMinutes(0);
        attendance.setNotes(notes);

        return attendanceRepository.save(attendance);
    }
}