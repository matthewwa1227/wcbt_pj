package com.casualapp.backend.controller;

import com.casualapp.backend.model.AttendanceStatus;
import com.casualapp.backend.model.Job;
import com.casualapp.backend.model.JobAttendance;
import com.casualapp.backend.model.JobSignup;
import com.casualapp.backend.model.Role;
import com.casualapp.backend.model.SignupStatus;
import com.casualapp.backend.model.User;
import com.casualapp.backend.repository.JobAttendanceRepository;
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
    public JobSignup signUp(@RequestParam Long workerId, @RequestParam Long jobId) {
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        if (worker.getRole() != Role.WORKER) {
            throw new RuntimeException("Only workers can sign up for jobs");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        signupRepository.findByJobIdAndWorkerId(jobId, workerId)
                .ifPresent(existingSignup -> {
                    throw new RuntimeException("Worker has already signed up for this job");
                });

        JobSignup signup = new JobSignup();
        signup.setWorker(worker);
        signup.setJob(job);
        signup.setStatus(SignupStatus.PENDING);

        return signupRepository.save(signup);
    }

    @PutMapping("/{id}/approve")
    public JobSignup approveSignup(@PathVariable Long id,
                                   @RequestParam Long coordinatorId,
                                   @RequestParam(required = false) String reason) {
        JobSignup signup = signupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Signup not found"));

        User coordinator = userRepository.findById(coordinatorId)
                .orElseThrow(() -> new RuntimeException("Coordinator not found"));

        if (coordinator.getRole() != Role.COORDINATOR && coordinator.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only coordinators or admins can approve signups");
        }

        signup.setStatus(SignupStatus.APPROVED);
        signup.setActionedBy(coordinator);
        signup.setActionReason(reason);

        return signupRepository.save(signup);
    }

    @PutMapping("/{id}/reject")
    public JobSignup rejectSignup(@PathVariable Long id,
                                  @RequestParam Long coordinatorId,
                                  @RequestParam(required = false) String reason) {
        JobSignup signup = signupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Signup not found"));

        User coordinator = userRepository.findById(coordinatorId)
                .orElseThrow(() -> new RuntimeException("Coordinator not found"));

        if (coordinator.getRole() != Role.COORDINATOR && coordinator.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only coordinators or admins can reject signups");
        }

        signup.setStatus(SignupStatus.REJECTED);
        signup.setActionedBy(coordinator);
        signup.setActionReason(reason);

        return signupRepository.save(signup);
    }

    @PutMapping("/{id}/cancel")
    public JobSignup cancelSignup(@PathVariable Long id,
                                  @RequestParam(required = false) Long actionedByUserId,
                                  @RequestParam(required = false) String reason) {
        JobSignup signup = signupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Signup not found"));

        signup.setStatus(SignupStatus.CANCELLED);
        signup.setActionReason(reason);

        if (actionedByUserId != null) {
            User actionedBy = userRepository.findById(actionedByUserId)
                    .orElseThrow(() -> new RuntimeException("Actioned-by user not found"));
            signup.setActionedBy(actionedBy);
        }

        return signupRepository.save(signup);
    }

    @PutMapping("/{id}/attend")
    public JobAttendance markAttended(@PathVariable Long id,
                                      @RequestParam Long recordedByUserId,
                                      @RequestParam(defaultValue = "0") Integer lateMinutes,
                                      @RequestParam(required = false) String notes) {
        JobSignup signup = signupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Signup not found"));

        if (signup.getStatus() != SignupStatus.APPROVED) {
            throw new RuntimeException("Only approved signups can have attendance recorded");
        }

        User recordedBy = userRepository.findById(recordedByUserId)
                .orElseThrow(() -> new RuntimeException("Recorded-by user not found"));

        if (recordedBy.getRole() != Role.COORDINATOR && recordedBy.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only coordinators or admins can record attendance");
        }

        JobAttendance attendance = attendanceRepository
                .findByJobIdAndWorkerId(signup.getJob().getId(), signup.getWorker().getId())
                .orElse(new JobAttendance());

        attendance.setJob(signup.getJob());
        attendance.setWorker(signup.getWorker());
        attendance.setRecordedBy(recordedBy);
        attendance.setLateMinutes(lateMinutes);
        attendance.setNotes(notes);

        if (lateMinutes != null && lateMinutes > 0) {
            attendance.setStatus(AttendanceStatus.LATE);
        } else {
            attendance.setStatus(AttendanceStatus.COMPLETED);
        }

        return attendanceRepository.save(attendance);
    }

    @PutMapping("/{id}/noshow")
    public JobAttendance markNoShow(@PathVariable Long id,
                                    @RequestParam Long recordedByUserId,
                                    @RequestParam(required = false) String notes) {
        JobSignup signup = signupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Signup not found"));

        if (signup.getStatus() != SignupStatus.APPROVED) {
            throw new RuntimeException("Only approved signups can have attendance recorded");
        }

        User recordedBy = userRepository.findById(recordedByUserId)
                .orElseThrow(() -> new RuntimeException("Recorded-by user not found"));

        if (recordedBy.getRole() != Role.COORDINATOR && recordedBy.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only coordinators or admins can record attendance");
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