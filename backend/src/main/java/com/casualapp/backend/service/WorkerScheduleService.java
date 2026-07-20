package com.casualapp.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.casualapp.backend.dto.WorkerScheduleItemResponse;
import com.casualapp.backend.dto.WorkerScheduleResponse;
import com.casualapp.backend.model.Job;
import com.casualapp.backend.model.JobAttendance;
import com.casualapp.backend.model.JobSignup;
import com.casualapp.backend.model.Role;
import com.casualapp.backend.model.SignupStatus;
import com.casualapp.backend.model.User;
import com.casualapp.backend.repository.JobAttendanceRepository;
import com.casualapp.backend.repository.JobSignupRepository;
import com.casualapp.backend.repository.UserRepository;

@Service
public class WorkerScheduleService {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE;

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    private final UserRepository userRepository;
    private final JobSignupRepository jobSignupRepository;
    private final JobAttendanceRepository jobAttendanceRepository;

    public WorkerScheduleService(
            UserRepository userRepository,
            JobSignupRepository jobSignupRepository,
            JobAttendanceRepository jobAttendanceRepository
    ) {
        this.userRepository = userRepository;
        this.jobSignupRepository = jobSignupRepository;
        this.jobAttendanceRepository = jobAttendanceRepository;
    }

    @Transactional(readOnly = true)
    public WorkerScheduleResponse getWorkerSchedule(Long workerId) {
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Worker not found"
                ));

        if (worker.getRole() != Role.WORKER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "The selected user is not a worker"
            );
        }

        List<JobSignup> signups =
                jobSignupRepository.findByWorkerId(workerId);

        List<JobAttendance> attendanceRecords =
                jobAttendanceRepository.findByWorkerId(workerId);

        Map<Long, JobAttendance> attendanceByJobId = new HashMap<>();

        for (JobAttendance attendance : attendanceRecords) {
            if (attendance.getJob() != null
                    && attendance.getJob().getId() != null) {
                attendanceByJobId.put(
                        attendance.getJob().getId(),
                        attendance
                );
            }
        }

        Map<Long, JobSignup> signupByJobId = new HashMap<>();

        for (JobSignup signup : signups) {
            if (signup.getJob() != null
                    && signup.getJob().getId() != null) {
                signupByJobId.put(
                        signup.getJob().getId(),
                        signup
                );
            }
        }

        LocalDate today = LocalDate.now();

        List<WorkerScheduleItemResponse> upcoming = signups.stream()
                .filter(signup -> signup.getStatus() == SignupStatus.APPROVED)
                .filter(signup -> signup.getJob() != null)
                .filter(signup -> signup.getJob().getJobDate() != null)
                .filter(signup -> !attendanceByJobId.containsKey(
                        signup.getJob().getId()
                ))
                .filter(signup -> !signup.getJob()
                        .getJobDate()
                        .toLocalDate()
                        .isBefore(today))
                .sorted(Comparator.comparing(
                        signup -> signup.getJob().getJobDate()
                ))
                .map(signup -> mapUpcoming(signup))
                .toList();

        List<WorkerScheduleItemResponse> completed = attendanceRecords.stream()
                .filter(attendance -> attendance.getJob() != null)
                .filter(attendance -> attendance.getJob().getJobDate() != null)
                .sorted(Comparator.comparing(
                        (JobAttendance attendance) ->
                                attendance.getJob().getJobDate()
                ).reversed())
                .map(attendance -> mapCompleted(
                        attendance,
                        signupByJobId.get(attendance.getJob().getId())
                ))
                .toList();

        WorkerScheduleResponse response = new WorkerScheduleResponse();
        response.setWorkerId(workerId);
        response.setGeneratedAt(LocalDateTime.now().toString());
        response.setUpcoming(upcoming);
        response.setCompleted(completed);

        return response;
    }

    private WorkerScheduleItemResponse mapUpcoming(JobSignup signup) {
        WorkerScheduleItemResponse item = baseItem(
                signup.getJob(),
                signup.getId()
        );

        item.setSignupStatus(
                signup.getStatus() == null
                        ? null
                        : signup.getStatus().name()
        );

        item.setAttendanceStatus(null);
        item.setLateMinutes(0);

        return item;
    }

    private WorkerScheduleItemResponse mapCompleted(
            JobAttendance attendance,
            JobSignup signup
    ) {
        WorkerScheduleItemResponse item = baseItem(
                attendance.getJob(),
                signup == null ? null : signup.getId()
        );

        item.setSignupStatus(
                signup == null || signup.getStatus() == null
                        ? null
                        : signup.getStatus().name()
        );

        item.setAttendanceStatus(
                attendance.getStatus() == null
                        ? null
                        : attendance.getStatus().name()
        );

        item.setLateMinutes(
                attendance.getLateMinutes() == null
                        ? 0
                        : attendance.getLateMinutes()
        );

        return item;
    }

    private WorkerScheduleItemResponse baseItem(
            Job job,
            Long signupId
    ) {
        WorkerScheduleItemResponse item =
                new WorkerScheduleItemResponse();

        item.setJobId(job.getId());
        item.setSignupId(signupId);
        item.setTitle(job.getTitle());
        item.setLocation(job.getLocation());

        LocalDateTime jobDate = job.getJobDate();

        item.setDate(
                jobDate == null
                        ? null
                        : jobDate.toLocalDate().format(DATE_FORMATTER)
        );

        item.setStartTime(
                jobDate == null
                        ? null
                        : jobDate.toLocalTime().format(TIME_FORMATTER)
        );

        /*
         * The current Job entity stores one LocalDateTime only.
         * Keep endTime null until an end-time field is added to Job.
         */
        item.setEndTime(null);

        return item;
    }
}
