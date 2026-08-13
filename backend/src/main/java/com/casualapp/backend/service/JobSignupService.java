package com.casualapp.backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casualapp.backend.controller.ApiException;
import com.casualapp.backend.dto.signup.SignupResponse;
import com.casualapp.backend.model.Job;
import com.casualapp.backend.model.JobSignup;
import com.casualapp.backend.model.JobStatus;
import com.casualapp.backend.model.Role;
import com.casualapp.backend.model.SignupStatus;
import com.casualapp.backend.model.User;
import com.casualapp.backend.repository.JobRepository;
import com.casualapp.backend.repository.JobSignupRepository;
import com.casualapp.backend.repository.UserRepository;

@Service
public class JobSignupService {

    private final JobSignupRepository jobSignupRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public JobSignupService(
            JobSignupRepository jobSignupRepository,
            UserRepository userRepository,
            JobRepository jobRepository
    ) {
        this.jobSignupRepository = jobSignupRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    @Transactional(readOnly = true)
    public List<SignupResponse> getAllSignups() {
        return jobSignupRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SignupResponse> getWorkerSignups(Long workerId) {

        requireRole(
                workerId,
                Role.WORKER,
                "Worker"
        );

        return jobSignupRepository.findByWorkerId(workerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SignupResponse> getJobSignups(
            Long jobId,
            Long coordinatorId
    ) {

        User coordinator = requireRole(
                coordinatorId,
                Role.COORDINATOR,
                "Coordinator"
        );

        Job job = requireJob(jobId);

        requireCoordinatorOwnsJob(
                coordinator,
                job
        );

        return jobSignupRepository.findByJobId(jobId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SignupResponse> getCoordinatorSignups(
            Long coordinatorId
    ) {

        requireRole(
                coordinatorId,
                Role.COORDINATOR,
                "Coordinator"
        );

        return jobSignupRepository
                .findByJobCoordinatorId(coordinatorId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SignupResponse signUp(
            Long workerId,
            Long jobId
    ) {

        User worker = requireRole(
                workerId,
                Role.WORKER,
                "Worker"
        );

        Job job = requireJob(jobId);

        if (job.getStatus() != JobStatus.OPEN) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "JOB_NOT_OPEN",
                    "Job is not open for applications"
            );
        }

        if (job.getFilledSlots() >= job.getTotalSlots()) {

            job.setStatus(JobStatus.FULL);
            jobRepository.save(job);

            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "JOB_FULL",
                    "Job is already full"
            );
        }

        if (jobSignupRepository
                .findByJobIdAndWorkerId(
                        jobId,
                        workerId
                )
                .isPresent()) {

            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "DUPLICATE_SIGNUP",
                    "Worker already signed up for this job"
            );
        }

        JobSignup signup = new JobSignup();

        signup.setWorker(worker);
        signup.setJob(job);
        signup.setStatus(SignupStatus.PENDING);

        JobSignup savedSignup =
                jobSignupRepository.save(signup);

        return toResponse(savedSignup);
    }

    @Transactional
    public SignupResponse approveSignup(
            Long signupId,
            Long coordinatorId,
            String reason
    ) {

        JobSignup signup = requireSignup(signupId);

        User coordinator = requireRole(
                coordinatorId,
                Role.COORDINATOR,
                "Coordinator"
        );

        Job job = signup.getJob();

        requireCoordinatorOwnsJob(
                coordinator,
                job
        );

        requirePendingSignup(signup);

        if (job.getStatus() != JobStatus.OPEN) {

            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "JOB_NOT_OPEN",
                    "Job is not open"
            );
        }

        if (job.getFilledSlots() >= job.getTotalSlots()) {

            job.setStatus(JobStatus.FULL);
            jobRepository.save(job);

            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "JOB_FULL",
                    "Job is already full"
            );
        }

        signup.setStatus(
                SignupStatus.APPROVED
        );

        signup.setActionedBy(
                coordinator
        );

        signup.setActionReason(
                hasText(reason)
                        ? reason.trim()
                        : "Application approved"
        );

        job.setFilledSlots(
                job.getFilledSlots() + 1
        );

        if (job.getFilledSlots()
                >= job.getTotalSlots()) {

            job.setStatus(
                    JobStatus.FULL
            );
        }

        jobRepository.save(job);

        JobSignup savedSignup =
                jobSignupRepository.save(signup);

        return toResponse(savedSignup);
    }

    @Transactional
    public SignupResponse rejectSignup(
            Long signupId,
            Long coordinatorId,
            String reason
    ) {

        JobSignup signup =
                requireSignup(signupId);

        User coordinator =
                requireRole(
                        coordinatorId,
                        Role.COORDINATOR,
                        "Coordinator"
                );

        requireCoordinatorOwnsJob(
                coordinator,
                signup.getJob()
        );

        requirePendingSignup(signup);

        signup.setStatus(
                SignupStatus.REJECTED
        );

        signup.setActionedBy(
                coordinator
        );

        signup.setActionReason(
                hasText(reason)
                        ? reason.trim()
                        : "Application rejected"
        );

        JobSignup savedSignup =
                jobSignupRepository.save(signup);

        return toResponse(savedSignup);
    }

    public JobSignup requireSignup(
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

    private Job requireJob(
            Long jobId
    ) {

        return jobRepository
                .findById(jobId)
                .orElseThrow(() ->
                        new ApiException(
                                HttpStatus.NOT_FOUND,
                                "JOB_NOT_FOUND",
                                "Job not found"
                        )
                );
    }

    private User requireRole(
            Long userId,
            Role requiredRole,
            String userType
    ) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ApiException(
                                HttpStatus.NOT_FOUND,
                                "USER_NOT_FOUND",
                                userType + " not found"
                        )
                );

        if (user.getRole() != requiredRole) {

            throw new ApiException(
                    HttpStatus.FORBIDDEN,
                    "ROLE_REQUIRED",
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

    private void requirePendingSignup(
            JobSignup signup
    ) {

        if (signup.getStatus()
                != SignupStatus.PENDING) {

            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "SIGNUP_NOT_PENDING",
                    "Only pending applications can be approved or rejected"
            );
        }
    }

    private SignupResponse toResponse(
            JobSignup signup
    ) {

        SignupResponse response =
                new SignupResponse();

        response.setId(
                signup.getId()
        );

        if (signup.getWorker() != null) {

            response.setWorkerId(
                    signup.getWorker().getId()
            );

            response.setWorkerName(
                    signup.getWorker().getName()
            );

            response.setWorkerPhoneNumber(
                    signup.getWorker().getPhoneNumber()
            );
        }

        if (signup.getJob() != null) {

            response.setJobId(
                    signup.getJob().getId()
            );

            response.setJobTitle(
                    signup.getJob().getTitle()
            );

            response.setJobLocation(
                    signup.getJob().getLocation()
            );

            response.setJobDate(
                    signup.getJob().getJobDate() == null
                            ? null
                            : signup.getJob()
                                    .getJobDate()
                                    .toString()
            );
        }

        response.setStatus(
                signup.getStatus() == null
                        ? null
                        : signup.getStatus().name()
        );

        response.setSignupTime(
                signup.getSignupTime() == null
                        ? null
                        : signup.getSignupTime()
                                .toString()
        );

        response.setUpdatedAt(
                signup.getUpdatedAt() == null
                        ? null
                        : signup.getUpdatedAt()
                                .toString()
        );

        response.setActionReason(
                signup.getActionReason()
        );

        if (signup.getActionedBy() != null) {

            response.setActionedByUserId(
                    signup.getActionedBy()
                            .getId()
            );

            response.setActionedByName(
                    signup.getActionedBy()
                            .getName()
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