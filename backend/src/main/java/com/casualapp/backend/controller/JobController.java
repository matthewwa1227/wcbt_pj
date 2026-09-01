package com.casualapp.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casualapp.backend.dto.job.CreateJobRequest;
import com.casualapp.backend.dto.job.JobResponse;
import com.casualapp.backend.service.JobService;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public List<JobResponse> getAllJobs() {
        return jobService.getAllJobs();
    }

    @GetMapping("/{jobId}")
    public JobResponse getJobById(
            @PathVariable Long jobId
    ) {
        return jobService.getJobById(jobId);
    }

    @GetMapping("/coordinator/{coordinatorId}")
    public List<JobResponse> getJobsByCoordinator(
            @PathVariable Long coordinatorId
    ) {
        return jobService.getJobsByCoordinator(coordinatorId);
    }

    @PostMapping
    public JobResponse createJob(
            @RequestBody CreateJobRequest request
    ) {
        return jobService.createJob(request);
    }
}