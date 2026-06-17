package com.casualapp.backend.controller;

import com.casualapp.backend.model.Job;
import com.casualapp.backend.model.User;
import com.casualapp.backend.repository.JobRepository;
import com.casualapp.backend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobController(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @PostMapping
    public Job createJob(@RequestBody Job job, @RequestParam Long coordinatorId) {
        User coordinator = userRepository.findById(coordinatorId)
            .orElseThrow(() -> new RuntimeException("Coordinator not found"));
        job.setCoordinator(coordinator);
        return jobRepository.save(job);
    }
}