package com.casualapp.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casualapp.backend.controller.ApiException;
import com.casualapp.backend.dto.job.CreateJobRequest;
import com.casualapp.backend.dto.job.JobResponse;
import com.casualapp.backend.model.Job;
import com.casualapp.backend.model.JobStatus;
import com.casualapp.backend.model.Role;
import com.casualapp.backend.model.User;
import com.casualapp.backend.repository.JobRepository;
import com.casualapp.backend.repository.UserRepository;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobService(
            JobRepository jobRepository,
            UserRepository userRepository
    ) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getAllJobs() {

        return jobRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public JobResponse getJobById(
            Long jobId
    ) {

        Job job = requireJob(jobId);

        return toResponse(job);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getJobsByCoordinator(
            Long coordinatorId
    ) {

        requireRole(
                coordinatorId,
                Role.COORDINATOR,
                "Coordinator"
        );

        return jobRepository
                .findByCoordinatorId(coordinatorId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public JobResponse createJob(
            CreateJobRequest request
    ) {

        validateCreateRequest(request);

        User coordinator = requireRole(
                request.getCoordinatorId(),
                Role.COORDINATOR,
                "Coordinator"
        );

        Job job = new Job();

        job.setTitle(
                request.getTitle().trim()
        );

        job.setDescription(
                hasText(request.getDescription())
                        ? request.getDescription().trim()
                        : null
        );

        job.setLocation(
                request.getLocation().trim()
        );

        /*
         * The entity still calls this field jobDate for
         * database compatibility, but it represents the
         * shift start datetime.
         */
        job.setJobDate(
                request.getStartDateTime()
        );

        job.setEndDateTime(
                request.getEndDateTime()
        );

        job.setHourlyRate(
                request.getHourlyRate()
        );

        job.setTotalSlots(
                request.getTotalSlots()
        );

        // Backend-controlled fields.
        job.setFilledSlots(0);
        job.setStatus(JobStatus.OPEN);
        job.setCoordinator(coordinator);

        Job savedJob =
                jobRepository.save(job);

        return toResponse(savedJob);
    }

    private void validateCreateRequest(
            CreateJobRequest request
    ) {

        if (request == null) {

            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "JOB_REQUEST_REQUIRED",
                    "Job request body is required"
            );
        }

        if (request.getCoordinatorId() == null) {

            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "COORDINATOR_ID_REQUIRED",
                    "Coordinator ID is required"
            );
        }

        if (!hasText(request.getTitle())) {

            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "JOB_TITLE_REQUIRED",
                    "Job title is required"
            );
        }

        if (!hasText(request.getLocation())) {

            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "JOB_LOCATION_REQUIRED",
                    "Job location is required"
            );
        }

        if (request.getStartDateTime() == null) {

            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "JOB_START_TIME_REQUIRED",
                    "Job start date and time are required"
            );
        }

        if (request.getEndDateTime() == null) {

            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "JOB_END_TIME_REQUIRED",
                    "Job end date and time are required"
            );
        }

        if (!request.getEndDateTime()
                .isAfter(request.getStartDateTime())) {

            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_JOB_TIME_RANGE",
                    "Job end time must be after start time"
            );
        }

        if (request.getHourlyRate() == null) {

            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "HOURLY_RATE_REQUIRED",
                    "Hourly rate is required"
            );
        }

        if (request.getHourlyRate()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_HOURLY_RATE",
                    "Hourly rate must be greater than zero"
            );
        }

        if (request.getTotalSlots() == null) {

            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "TOTAL_SLOTS_REQUIRED",
                    "Total slots are required"
            );
        }

        if (request.getTotalSlots() <= 0) {

            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_TOTAL_SLOTS",
                    "Total slots must be greater than zero"
            );
        }
    }

    private Job requireJob(
            Long jobId
    ) {

        if (jobId == null) {

            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "JOB_ID_REQUIRED",
                    "Job ID is required"
            );
        }

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

        if (userId == null) {

            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "USER_ID_REQUIRED",
                    userType + " ID is required"
            );
        }

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

    private JobResponse toResponse(
            Job job
    ) {

        JobResponse response =
                new JobResponse();

        response.setId(
                job.getId()
        );

        response.setTitle(
                job.getTitle()
        );

        response.setDescription(
                job.getDescription()
        );

        response.setLocation(
                job.getLocation()
        );

        response.setStartDateTime(
                job.getJobDate()
        );

        response.setEndDateTime(
                job.getEndDateTime()
        );

        response.setHourlyRate(
                job.getHourlyRate()
        );

        response.setTotalSlots(
                job.getTotalSlots()
        );

        response.setFilledSlots(
                job.getFilledSlots()
        );

        response.setStatus(
                job.getStatus() == null
                        ? null
                        : job.getStatus().name()
        );

        if (job.getCoordinator() != null) {

            response.setCoordinatorId(
                    job.getCoordinator().getId()
            );

            response.setCoordinatorName(
                    job.getCoordinator().getName()
            );
        }

        response.setCreatedAt(
                job.getCreatedAt()
        );

        return response;
    }

    private boolean hasText(
            String value
    ) {

        return value != null
                && !value.isBlank();
    }
}