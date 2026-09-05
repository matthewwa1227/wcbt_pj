package com.casualapp.backend.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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

@Configuration
@Profile("local")
public class DevDataSeeder {

    @Bean
    CommandLineRunner seedDevData(
            UserRepository userRepository,
            JobRepository jobRepository,
            JobSignupRepository jobSignupRepository,
            JobAttendanceRepository jobAttendanceRepository
    ) {
        return args -> {

            /*
             * =========================================================
             * USERS
             * =========================================================
             */

            User bossChan = ensureUser(
                    userRepository,
                    "90000001",
                    "Boss Chan",
                    Role.COORDINATOR
            );

            User workerA = ensureUser(
                    userRepository,
                    "90000002",
                    "Worker A",
                    Role.WORKER
            );

            User workerB = ensureUser(
                    userRepository,
                    "90000003",
                    "Worker B",
                    Role.WORKER
            );

            ensureUser(
                    userRepository,
                    "90000004",
                    "Admin Test",
                    Role.ADMIN
            );

            User workerC = ensureUser(
                    userRepository,
                    "90000005",
                    "Worker C",
                    Role.WORKER
            );

            User workerD = ensureUser(
                    userRepository,
                    "90000006",
                    "Worker D",
                    Role.WORKER
            );

            User workerE = ensureUser(
                    userRepository,
                    "90000007",
                    "Worker E",
                    Role.WORKER
            );

            User workerF = ensureUser(
                    userRepository,
                    "90000008",
                    "Worker F",
                    Role.WORKER
            );

            User workerG = ensureUser(
                    userRepository,
                    "90000009",
                    "Worker G",
                    Role.WORKER
            );

            User managerLee = ensureUser(
                    userRepository,
                    "90000010",
                    "Manager Lee",
                    Role.COORDINATOR
            );

            User workerH = ensureUser(
                    userRepository,
                    "90000011",
                    "Worker H",
                    Role.WORKER
            );

            List<User> workers = List.of(
                    workerA,
                    workerB,
                    workerC,
                    workerD,
                    workerE,
                    workerF,
                    workerG,
                    workerH
            );

            List<User> coordinators = List.of(
                    bossChan,
                    managerLee
            );


            /*
             * =========================================================
             * HISTORICAL JOBS
             *
             * Dates are relative to the current month so Performance
             * always has several recent months of history.
             * =========================================================
             */

            LocalDate currentMonth =
                    LocalDate.now()
                            .withDayOfMonth(1);

            List<JobSeed> historicalSeeds = List.of(

                    /*
                     * Four months ago
                     */
                    new JobSeed(
                            "Wedding Banquet Server",
                            "Harbour View Hotel",
                            "Wedding banquet dinner service.",
                            4,
                            5,
                            17,
                            0,
                            6,
                            "120.00",
                            5,
                            0
                    ),

                    new JobSeed(
                            "Breakfast Buffet Server",
                            "Central Hotel",
                            "Breakfast buffet service and table reset.",
                            4,
                            11,
                            7,
                            0,
                            5,
                            "105.00",
                            4,
                            1
                    ),

                    new JobSeed(
                            "Conference Setup Crew",
                            "Metropark Hotel",
                            "Conference room setup and guest support.",
                            4,
                            18,
                            9,
                            0,
                            7,
                            "110.00",
                            5,
                            0
                    ),

                    new JobSeed(
                            "Cocktail Bartender",
                            "Grand Harbour Hotel",
                            "Evening cocktail reception service.",
                            4,
                            24,
                            18,
                            0,
                            5,
                            "140.00",
                            4,
                            1
                    ),


                    /*
                     * Three months ago
                     */
                    new JobSeed(
                            "Restaurant Server",
                            "Kowloon City Hotel",
                            "Dinner restaurant floor service.",
                            3,
                            4,
                            17,
                            30,
                            6,
                            "115.00",
                            5,
                            0
                    ),

                    new JobSeed(
                            "Kitchen Helper",
                            "Harbour View Hotel",
                            "Food preparation and kitchen support.",
                            3,
                            10,
                            15,
                            0,
                            7,
                            "100.00",
                            5,
                            1
                    ),

                    new JobSeed(
                            "Housekeeping Support",
                            "Central Hotel",
                            "Evening room turnover support.",
                            3,
                            17,
                            14,
                            0,
                            6,
                            "105.00",
                            4,
                            0
                    ),

                    new JobSeed(
                            "Event Usher",
                            "Convention Hotel",
                            "Guest direction and event entrance support.",
                            3,
                            23,
                            16,
                            0,
                            5,
                            "100.00",
                            5,
                            1
                    ),


                    /*
                     * Two months ago
                     */
                    new JobSeed(
                            "Banquet Captain Assistant",
                            "Grand Harbour Hotel",
                            "Assist banquet captain during corporate dinner.",
                            2,
                            5,
                            16,
                            30,
                            7,
                            "130.00",
                            5,
                            0
                    ),

                    new JobSeed(
                            "Poolside Server",
                            "Seaview Hotel",
                            "Poolside food and beverage service.",
                            2,
                            12,
                            11,
                            0,
                            6,
                            "115.00",
                            4,
                            1
                    ),

                    new JobSeed(
                            "Afternoon Tea Server",
                            "Peninsula Demo Hotel",
                            "Afternoon tea guest service.",
                            2,
                            19,
                            13,
                            0,
                            5,
                            "125.00",
                            5,
                            0
                    ),

                    new JobSeed(
                            "Catering Runner",
                            "Convention Hotel",
                            "Food running and catering logistics.",
                            2,
                            25,
                            15,
                            30,
                            7,
                            "110.00",
                            5,
                            1
                    ),


                    /*
                     * Previous month
                     */
                    new JobSeed(
                            "Wedding Reception Server",
                            "Harbour View Hotel",
                            "Wedding reception and dinner service.",
                            1,
                            4,
                            17,
                            0,
                            6,
                            "125.00",
                            5,
                            0
                    ),

                    new JobSeed(
                            "Barback",
                            "Central Hotel",
                            "Bar stock, glassware and bartender support.",
                            1,
                            10,
                            18,
                            0,
                            5,
                            "110.00",
                            4,
                            1
                    ),

                    new JobSeed(
                            "Stewarding Assistant",
                            "Kowloon City Hotel",
                            "Stewarding and banquet cleanup support.",
                            1,
                            17,
                            16,
                            0,
                            7,
                            "100.00",
                            5,
                            0
                    ),

                    new JobSeed(
                            "Corporate Dinner Server",
                            "Grand Harbour Hotel",
                            "Corporate dinner banquet service.",
                            1,
                            24,
                            17,
                            30,
                            6,
                            "120.00",
                            5,
                            1
                    )
            );


            /*
             * Create each historical job and then populate:
             *
             * 3 approved workers
             * 1 rejected worker
             *
             * Every approved historical worker receives attendance.
             */
            for (int i = 0;
                 i < historicalSeeds.size();
                 i++) {

                JobSeed seed =
                        historicalSeeds.get(i);

                User coordinator =
                        coordinators.get(
                                seed.coordinatorIndex()
                        );

                LocalDateTime startDateTime =
                        currentMonth
                                .minusMonths(seed.monthsAgo())
                                .plusDays(seed.dayOfMonth() - 1L)
                                .atTime(
                                        seed.startHour(),
                                        seed.startMinute()
                                );

                LocalDateTime endDateTime =
                        startDateTime.plusHours(
                                seed.durationHours()
                        );

                Job job = ensureJob(
                        jobRepository,
                        coordinator,
                        seed.title(),
                        seed.location(),
                        seed.description(),
                        startDateTime,
                        endDateTime,
                        new BigDecimal(
                                seed.hourlyRate()
                        ),
                        seed.totalSlots(),
                        JobStatus.CLOSED
                );


                /*
                 * Rotate workers through jobs so everyone has
                 * a meaningful history.
                 */
                User approvedWorker1 =
                        workers.get(
                                i % workers.size()
                        );

                User approvedWorker2 =
                        workers.get(
                                (i + 1)
                                        % workers.size()
                        );

                User approvedWorker3 =
                        workers.get(
                                (i + 2)
                                        % workers.size()
                        );

                User rejectedWorker =
                        workers.get(
                                (i + 3)
                                        % workers.size()
                        );


                JobSignup signup1 =
                        ensureSignup(
                                jobSignupRepository,
                                job,
                                approvedWorker1,
                                SignupStatus.APPROVED,
                                coordinator,
                                "Approved for shift",
                                startDateTime
                                        .minusDays(7)
                        );

                JobSignup signup2 =
                        ensureSignup(
                                jobSignupRepository,
                                job,
                                approvedWorker2,
                                SignupStatus.APPROVED,
                                coordinator,
                                "Approved for shift",
                                startDateTime
                                        .minusDays(6)
                        );

                JobSignup signup3 =
                        ensureSignup(
                                jobSignupRepository,
                                job,
                                approvedWorker3,
                                SignupStatus.APPROVED,
                                coordinator,
                                "Approved for shift",
                                startDateTime
                                        .minusDays(5)
                        );

                ensureSignup(
                        jobSignupRepository,
                        job,
                        rejectedWorker,
                        SignupStatus.REJECTED,
                        coordinator,
                        "Other applicants better matched staffing needs",
                        startDateTime
                                .minusDays(4)
                );


                /*
                 * Attendance pattern deliberately varies.
                 *
                 * Worker 1:
                 * usually COMPLETED
                 *
                 * Worker 2:
                 * occasional LATE
                 *
                 * Worker 3:
                 * mix of COMPLETED, LATE and NO_SHOW
                 */

                ensureAttendance(
                        jobAttendanceRepository,
                        job,
                        signup1.getWorker(),
                        coordinator,
                        AttendanceStatus.COMPLETED,
                        0,
                        "Shift completed",
                        endDateTime.plusMinutes(15)
                );


                AttendanceStatus secondStatus =
                        i % 4 == 0
                                ? AttendanceStatus.LATE
                                : AttendanceStatus.COMPLETED;

                int secondLateMinutes =
                        secondStatus == AttendanceStatus.LATE
                                ? 8 + (i % 3) * 5
                                : 0;

                ensureAttendance(
                        jobAttendanceRepository,
                        job,
                        signup2.getWorker(),
                        coordinator,
                        secondStatus,
                        secondLateMinutes,
                        attendanceNote(
                                secondStatus,
                                secondLateMinutes
                        ),
                        endDateTime.plusMinutes(20)
                );


                AttendanceStatus thirdStatus;

                if (i % 5 == 0) {

                    thirdStatus =
                            AttendanceStatus.NO_SHOW;

                } else if (i % 3 == 0) {

                    thirdStatus =
                            AttendanceStatus.LATE;

                } else {

                    thirdStatus =
                            AttendanceStatus.COMPLETED;
                }

                int thirdLateMinutes =
                        thirdStatus == AttendanceStatus.LATE
                                ? 12 + (i % 4) * 4
                                : 0;

                ensureAttendance(
                        jobAttendanceRepository,
                        job,
                        signup3.getWorker(),
                        coordinator,
                        thirdStatus,
                        thirdLateMinutes,
                        attendanceNote(
                                thirdStatus,
                                thirdLateMinutes
                        ),
                        endDateTime.plusMinutes(25)
                );


                syncFilledSlots(
                        jobRepository,
                        jobSignupRepository,
                        job
                );
            }


            /*
             * =========================================================
             * NORMAL FUTURE JOBS
             *
             * These remain relatively clean so they can still be used
             * for manual Android testing.
             * =========================================================
             */

            LocalDateTime bartenderStart =
                    LocalDateTime.now()
                            .plusDays(2)
                            .withHour(18)
                            .withMinute(0)
                            .withSecond(0)
                            .withNano(0);

            ensureJob(
                    jobRepository,
                    bossChan,
                    "Bartender Shift",
                    "Central Hotel",
                    "Evening bartender shift for hotel banquet service.",
                    bartenderStart,
                    bartenderStart.plusHours(5),
                    new BigDecimal("120.00"),
                    3,
                    JobStatus.OPEN
            );


            LocalDateTime banquetStart =
                    LocalDateTime.now()
                            .plusDays(3)
                            .withHour(17)
                            .withMinute(30)
                            .withSecond(0)
                            .withNano(0);

            ensureJob(
                    jobRepository,
                    bossChan,
                    "Banquet Waiter Shift",
                    "Harbour View Hotel",
                    "Banquet waiter shift for wedding dinner service.",
                    banquetStart,
                    banquetStart.plusHours(6),
                    new BigDecimal("110.00"),
                    5,
                    JobStatus.OPEN
            );


            LocalDateTime kitchenStart =
                    LocalDateTime.now()
                            .plusDays(4)
                            .withHour(16)
                            .withMinute(0)
                            .withSecond(0)
                            .withNano(0);

            ensureJob(
                    jobRepository,
                    bossChan,
                    "Kitchen Helper Shift",
                    "Kowloon City Hotel",
                    "Kitchen helper shift for evening food preparation.",
                    kitchenStart,
                    kitchenStart.plusHours(6),
                    new BigDecimal("100.00"),
                    4,
                    JobStatus.OPEN
            );


            /*
             * =========================================================
             * FUTURE DEMO JOBS WITH MIXED APPLICATION STATES
             *
             * These give the coordinator dashboard:
             *
             * APPROVED
             * PENDING
             * REJECTED
             * CANCELLED
             *
             * without requiring manual setup.
             * =========================================================
             */

            LocalDateTime galaStart =
                    LocalDateTime.now()
                            .plusDays(6)
                            .withHour(17)
                            .withMinute(0)
                            .withSecond(0)
                            .withNano(0);

            Job galaJob = ensureJob(
                    jobRepository,
                    bossChan,
                    "Autumn Gala Server",
                    "Harbour View Hotel",
                    "Large evening gala banquet service.",
                    galaStart,
                    galaStart.plusHours(7),
                    new BigDecimal("130.00"),
                    6,
                    JobStatus.OPEN
            );

            ensureSignup(
                    jobSignupRepository,
                    galaJob,
                    workerA,
                    SignupStatus.APPROVED,
                    bossChan,
                    "Approved for gala",
                    galaStart.minusDays(5)
            );

            ensureSignup(
                    jobSignupRepository,
                    galaJob,
                    workerB,
                    SignupStatus.APPROVED,
                    bossChan,
                    "Approved for gala",
                    galaStart.minusDays(5)
            );

            ensureSignup(
                    jobSignupRepository,
                    galaJob,
                    workerC,
                    SignupStatus.PENDING,
                    null,
                    null,
                    galaStart.minusDays(4)
            );

            ensureSignup(
                    jobSignupRepository,
                    galaJob,
                    workerD,
                    SignupStatus.PENDING,
                    null,
                    null,
                    galaStart.minusDays(3)
            );

            ensureSignup(
                    jobSignupRepository,
                    galaJob,
                    workerE,
                    SignupStatus.REJECTED,
                    bossChan,
                    "Position already sufficiently staffed",
                    galaStart.minusDays(3)
            );

            syncFilledSlots(
                    jobRepository,
                    jobSignupRepository,
                    galaJob
            );


            LocalDateTime conferenceStart =
                    LocalDateTime.now()
                            .plusDays(8)
                            .withHour(8)
                            .withMinute(30)
                            .withSecond(0)
                            .withNano(0);

            Job conferenceJob = ensureJob(
                    jobRepository,
                    managerLee,
                    "Conference Banquet Crew",
                    "Convention Hotel",
                    "Full-day conference food and beverage support.",
                    conferenceStart,
                    conferenceStart.plusHours(8),
                    new BigDecimal("115.00"),
                    7,
                    JobStatus.OPEN
            );

            ensureSignup(
                    jobSignupRepository,
                    conferenceJob,
                    workerC,
                    SignupStatus.APPROVED,
                    managerLee,
                    "Approved for conference",
                    conferenceStart.minusDays(6)
            );

            ensureSignup(
                    jobSignupRepository,
                    conferenceJob,
                    workerD,
                    SignupStatus.APPROVED,
                    managerLee,
                    "Approved for conference",
                    conferenceStart.minusDays(6)
            );

            ensureSignup(
                    jobSignupRepository,
                    conferenceJob,
                    workerE,
                    SignupStatus.PENDING,
                    null,
                    null,
                    conferenceStart.minusDays(5)
            );

            ensureSignup(
                    jobSignupRepository,
                    conferenceJob,
                    workerF,
                    SignupStatus.PENDING,
                    null,
                    null,
                    conferenceStart.minusDays(4)
            );

            ensureSignup(
                    jobSignupRepository,
                    conferenceJob,
                    workerG,
                    SignupStatus.REJECTED,
                    managerLee,
                    "Schedule availability did not match",
                    conferenceStart.minusDays(4)
            );

            syncFilledSlots(
                    jobRepository,
                    jobSignupRepository,
                    conferenceJob
            );


            LocalDateTime weekendBarStart =
                    LocalDateTime.now()
                            .plusDays(10)
                            .withHour(19)
                            .withMinute(0)
                            .withSecond(0)
                            .withNano(0);

            Job weekendBarJob = ensureJob(
                    jobRepository,
                    bossChan,
                    "Weekend Bar Crew",
                    "Central Hotel",
                    "Weekend bar and lounge service.",
                    weekendBarStart,
                    weekendBarStart.plusHours(5),
                    new BigDecimal("135.00"),
                    5,
                    JobStatus.OPEN
            );

            ensureSignup(
                    jobSignupRepository,
                    weekendBarJob,
                    workerF,
                    SignupStatus.APPROVED,
                    bossChan,
                    "Approved for bar shift",
                    weekendBarStart.minusDays(6)
            );

            ensureSignup(
                    jobSignupRepository,
                    weekendBarJob,
                    workerG,
                    SignupStatus.APPROVED,
                    bossChan,
                    "Approved for bar shift",
                    weekendBarStart.minusDays(5)
            );

            ensureSignup(
                    jobSignupRepository,
                    weekendBarJob,
                    workerH,
                    SignupStatus.PENDING,
                    null,
                    null,
                    weekendBarStart.minusDays(4)
            );

            ensureSignup(
                    jobSignupRepository,
                    weekendBarJob,
                    workerA,
                    SignupStatus.CANCELLED,
                    null,
                    "Worker cancelled application",
                    weekendBarStart.minusDays(4)
            );

            ensureSignup(
                    jobSignupRepository,
                    weekendBarJob,
                    workerB,
                    SignupStatus.REJECTED,
                    bossChan,
                    "Other applicants selected",
                    weekendBarStart.minusDays(3)
            );

            syncFilledSlots(
                    jobRepository,
                    jobSignupRepository,
                    weekendBarJob
            );
        };
    }


    /*
     * =========================================================
     * USER HELPERS
     * =========================================================
     */

    private User ensureUser(
            UserRepository userRepository,
            String phoneNumber,
            String name,
            Role role
    ) {

        return userRepository
                .findByPhoneNumber(phoneNumber)
                .orElseGet(() -> {

                    User user = new User();

                    user.setPhoneNumber(phoneNumber);
                    user.setName(name);
                    user.setRole(role);

                    return userRepository.save(user);
                });
    }


    /*
     * =========================================================
     * JOB HELPERS
     * =========================================================
     */

    private Job ensureJob(
            JobRepository jobRepository,
            User coordinator,
            String title,
            String location,
            String description,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            BigDecimal hourlyRate,
            int totalSlots,
            JobStatus status
    ) {

        return jobRepository
                .findByTitleAndLocation(
                        title,
                        location
                )
                .map(existingJob -> {

                    /*
                     * Backfill fields introduced by the structured
                     * Job refactor without destroying existing data.
                     */

                    boolean changed = false;

                    if (existingJob.getEndDateTime() == null) {

                        existingJob.setEndDateTime(
                                endDateTime
                        );

                        changed = true;
                    }

                    if (existingJob.getHourlyRate() == null) {

                        existingJob.setHourlyRate(
                                hourlyRate
                        );

                        changed = true;
                    }

                    if (changed) {
                        return jobRepository.save(
                                existingJob
                        );
                    }

                    return existingJob;
                })
                .orElseGet(() -> {

                    Job job = new Job();

                    job.setTitle(title);
                    job.setLocation(location);
                    job.setDescription(description);

                    /*
                     * jobDate remains the persistence field name,
                     * but semantically represents startDateTime.
                     */
                    job.setJobDate(startDateTime);

                    job.setEndDateTime(endDateTime);
                    job.setHourlyRate(hourlyRate);

                    job.setTotalSlots(totalSlots);
                    job.setFilledSlots(0);

                    job.setStatus(status);
                    job.setCoordinator(coordinator);

                    job.setCreatedAt(
                            startDateTime.minusDays(14)
                    );

                    return jobRepository.save(job);
                });
    }


    /*
     * =========================================================
     * SIGNUP HELPERS
     * =========================================================
     */

    private JobSignup ensureSignup(
            JobSignupRepository jobSignupRepository,
            Job job,
            User worker,
            SignupStatus status,
            User actionedBy,
            String actionReason,
            LocalDateTime signupTime
    ) {

        return jobSignupRepository
                .findByJobIdAndWorkerId(
                        job.getId(),
                        worker.getId()
                )
                .orElseGet(() -> {

                    JobSignup signup =
                            new JobSignup();

                    signup.setJob(job);
                    signup.setWorker(worker);

                    signup.setStatus(status);
                    signup.setSignupTime(signupTime);

                    if (status == SignupStatus.APPROVED
                            || status == SignupStatus.REJECTED) {

                        signup.setActionedBy(
                                actionedBy
                        );

                        signup.setActionReason(
                                actionReason
                        );

                        signup.setUpdatedAt(
                                signupTime.plusDays(1)
                        );

                    } else if (status == SignupStatus.CANCELLED) {

                        signup.setActionReason(
                                actionReason
                        );

                        signup.setUpdatedAt(
                                signupTime.plusHours(8)
                        );
                    }

                    return jobSignupRepository.save(
                            signup
                    );
                });
    }


    /*
     * =========================================================
     * ATTENDANCE HELPERS
     * =========================================================
     */

    private JobAttendance ensureAttendance(
            JobAttendanceRepository jobAttendanceRepository,
            Job job,
            User worker,
            User recordedBy,
            AttendanceStatus status,
            int lateMinutes,
            String notes,
            LocalDateTime recordedAt
    ) {

        return jobAttendanceRepository
                .findByJobIdAndWorkerId(
                        job.getId(),
                        worker.getId()
                )
                .orElseGet(() -> {

                    JobAttendance attendance =
                            new JobAttendance();

                    attendance.setJob(job);
                    attendance.setWorker(worker);

                    attendance.setRecordedBy(
                            recordedBy
                    );

                    attendance.setStatus(status);

                    attendance.setLateMinutes(
                            lateMinutes
                    );

                    attendance.setNotes(notes);

                    attendance.setRecordedAt(
                            recordedAt
                    );

                    return jobAttendanceRepository.save(
                            attendance
                    );
                });
    }


    private String attendanceNote(
            AttendanceStatus status,
            int lateMinutes
    ) {

        return switch (status) {

            case COMPLETED ->
                    "Shift completed";

            case LATE ->
                    "Worker arrived "
                            + lateMinutes
                            + " minutes late";

            case NO_SHOW ->
                    "Worker did not attend shift";
        };
    }


    /*
     * =========================================================
     * SLOT SYNCHRONIZATION
     * =========================================================
     */

    private void syncFilledSlots(
            JobRepository jobRepository,
            JobSignupRepository jobSignupRepository,
            Job job
    ) {

        long approvedCount =
                jobSignupRepository
                        .findByJobId(job.getId())
                        .stream()
                        .filter(signup ->
                                signup.getStatus()
                                        == SignupStatus.APPROVED
                        )
                        .count();

        int filledSlots =
                (int) approvedCount;

        if (job.getFilledSlots()
                != filledSlots) {

            job.setFilledSlots(
                    filledSlots
            );

            /*
             * Historical jobs stay CLOSED.
             *
             * Future jobs automatically switch between
             * OPEN and FULL based on approved capacity.
             */
            if (job.getStatus()
                    != JobStatus.CLOSED
                    && job.getStatus()
                    != JobStatus.CANCELLED) {

                if (filledSlots
                        >= job.getTotalSlots()) {

                    job.setStatus(
                            JobStatus.FULL
                    );

                } else {

                    job.setStatus(
                            JobStatus.OPEN
                    );
                }
            }

            jobRepository.save(job);
        }
    }


    /*
     * =========================================================
     * HISTORICAL JOB SEED DESCRIPTION
     * =========================================================
     */

    private record JobSeed(
            String title,
            String location,
            String description,
            int monthsAgo,
            int dayOfMonth,
            int startHour,
            int startMinute,
            int durationHours,
            String hourlyRate,
            int totalSlots,
            int coordinatorIndex
    ) {
    }
}