package com.casualapp.backend.controller;

import com.casualapp.backend.model.Job;
import com.casualapp.backend.model.JobSignup;
import com.casualapp.backend.model.SignupStatus;
import com.casualapp.backend.model.User;
import com.casualapp.backend.repository.JobRepository;
import com.casualapp.backend.repository.JobSignupRepository;
import com.casualapp.backend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/signups")
public class JobSignupController {

    private final JobSignupRepository signupRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public JobSignupController(JobSignupRepository signupRepository,
                               UserRepository userRepository,
                               JobRepository jobRepository) {
        this.signupRepository = signupRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    @GetMapping
    public List<JobSignup> getAllSignups() {
        return signupRepository.findAll();
    }

    @PostMapping
    public JobSignup signUp(@RequestParam Long workerId, @RequestParam Long jobId) {
        User worker = userRepository.findById(workerId)
            .orElseThrow(() -> new RuntimeException("Worker not found"));
        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));

        JobSignup signup = new JobSignup();
        signup.setWorker(worker);
        signup.setJob(job);
        return signupRepository.save(signup);
    }

    @PutMapping("/{id}/attend")
    public JobSignup markAttended(@PathVariable Long id) {
        JobSignup signup = signupRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Signup not found"));
        signup.setStatus(SignupStatus.ATTENDED);
        return signupRepository.save(signup);
    }

    @PutMapping("/{id}/noshow")
    public JobSignup markNoShow(@PathVariable Long id) {
        JobSignup signup = signupRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Signup not found"));
        signup.setStatus(SignupStatus.NO_SHOW);
        return signupRepository.save(signup);
    }
}