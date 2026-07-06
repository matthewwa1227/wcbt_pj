package com.casualapp.backend.controller;

import java.util.List;

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

    @PostMapping
    public JobSignup signUp(@RequestParam Long workerId, @RequestParam Long jobId) {
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (job.getStatus() == JobStatus.FULL) {
            throw new RuntimeException("Job is already full");
        }

        if (job.getFilledSlots() >= job.getTotalSlots()) {
            job.setStatus(JobStatus.FULL);
            jobRepository.save(job);
            throw new RuntimeException("Job is already full");
        }

        jobSignupRepository.findByJobIdAndWorkerId(jobId, workerId)
                .ifPresent(existing -> {
                    throw new RuntimeException("Worker already signed up for this job");
                });

        JobSignup signup = new JobSignup();
        signup.setWorker(worker);
        signup.setJob(job);
        signup.setStatus(SignupStatus.PENDING);

        return jobSignupRepository.save(signup);
    }

    @PutMapping("/{id}/approve")
    public JobSignup approveSignup(@PathVariable Long id, @RequestParam Long coordinatorId) {
        JobSignup signup = jobSignupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Signup not found"));

        User coordinator = userRepository.findById(coordinatorId)
                .orElseThrow(() -> new RuntimeException("Coordinator not found"));

        if (signup.getStatus() == SignupStatus.APPROVED) {
            throw new RuntimeException("Signup is already approved");
        }

        Job job = signup.getJob();

        if (job.getStatus() == JobStatus.FULL) {
            throw new RuntimeException("Job is already full");
        }

        if (job.getFilledSlots() >= job.getTotalSlots()) {
            job.setStatus(JobStatus.FULL);
            jobRepository.save(job);
            throw new RuntimeException("Job is already full");
        }

        signup.setStatus(SignupStatus.APPROVED);
        signup.setActionedBy(coordinator);

        job.setFilledSlots(job.getFilledSlots() + 1);

        if (job.getFilledSlots() >= job.getTotalSlots()) {
            job.setStatus(JobStatus.FULL);
        }

        jobRepository.save(job);
        return jobSignupRepository.save(signup);
    }

    @PutMapping("/{id}/attend")
    public JobAttendance markAttended(
            @PathVariable Long id,
            @RequestParam Long recordedByUserId
    ) {
        JobSignup signup = jobSignupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Signup not found"));

        if (signup.getStatus() != SignupStatus.APPROVED) {
            throw new RuntimeException("Signup must be approved before attendance can be marked");
        }

        User recordedBy = userRepository.findById(recordedByUserId)
                .orElseThrow(() -> new RuntimeException("Recorder user not found"));

        Job job = signup.getJob();
        User worker = signup.getWorker();

        jobAttendanceRepository.findByJobIdAndWorkerId(job.getId(), worker.getId())
                .ifPresent(existing -> {
                    throw new RuntimeException("Attendance already recorded for this worker and job");
                });

        JobAttendance attendance = new JobAttendance();
        attendance.setJob(job);
        attendance.setWorker(worker);
        attendance.setRecordedBy(recordedBy);
        attendance.setStatus(AttendanceStatus.COMPLETED);
        attendance.setLateMinutes(0);
        attendance.setNotes("Marked attended from Android app");

        return jobAttendanceRepository.save(attendance);
    }
}