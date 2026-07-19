package com.casualapp.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.casualapp.backend.model.Job;
import com.casualapp.backend.model.JobStatus;
import com.casualapp.backend.model.Role;
import com.casualapp.backend.model.User;
import com.casualapp.backend.repository.JobRepository;
import com.casualapp.backend.repository.UserRepository;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobController(
            JobRepository jobRepository,
            UserRepository userRepository
    ) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @GetMapping("/{jobId}")
    public Job getJobById(@PathVariable Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job not found"
                ));
    }

    @GetMapping("/coordinator/{coordinatorId}")
    public List<Job> getJobsByCoordinator(
            @PathVariable Long coordinatorId
    ) {
        requireRole(coordinatorId, Role.COORDINATOR, "Coordinator");

        return jobRepository.findByCoordinatorId(coordinatorId);
    }

    @PostMapping
    public Job createJob(
            @RequestBody Job job,
            @RequestParam Long coordinatorId
    ) {
        User coordinator = requireRole(
                coordinatorId,
                Role.COORDINATOR,
                "Coordinator"
        );

        validateJob(job);

        job.setCoordinator(coordinator);

        // Never trust these values from the Android request.
        job.setFilledSlots(0);
        job.setStatus(JobStatus.OPEN);

        return jobRepository.save(job);
    }

    private void validateJob(Job job) {
        if (job == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Job request body is required"
            );
        }

        if (job.getTitle() == null || job.getTitle().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Job title is required"
            );
        }

        if (job.getLocation() == null || job.getLocation().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Job location is required"
            );
        }

        if (job.getJobDate() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Job date is required"
            );
        }

        if (job.getTotalSlots() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Total slots must be greater than zero"
            );
        }
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
}