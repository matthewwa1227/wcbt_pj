package com.casualapp.backend.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.casualapp.backend.model.Job;
import com.casualapp.backend.model.JobStatus;
import com.casualapp.backend.model.Role;
import com.casualapp.backend.model.User;
import com.casualapp.backend.repository.JobRepository;
import com.casualapp.backend.repository.UserRepository;

@Configuration
@Profile("local")
public class DevDataSeeder {

    @Bean
    CommandLineRunner seedDevData(
            UserRepository userRepository,
            JobRepository jobRepository
    ) {
        return args -> {

            User coordinator =
                    userRepository.findByPhoneNumber("90000001")
                            .orElseGet(() -> {
                                User user = new User();
                                user.setPhoneNumber("90000001");
                                user.setName("Boss Chan");
                                user.setRole(Role.COORDINATOR);
                                return userRepository.save(user);
                            });

            userRepository.findByPhoneNumber("90000002")
                    .orElseGet(() -> {
                        User user = new User();
                        user.setPhoneNumber("90000002");
                        user.setName("Worker A");
                        user.setRole(Role.WORKER);
                        return userRepository.save(user);
                    });

            userRepository.findByPhoneNumber("90000003")
                    .orElseGet(() -> {
                        User user = new User();
                        user.setPhoneNumber("90000003");
                        user.setName("Worker B");
                        user.setRole(Role.WORKER);
                        return userRepository.save(user);
                    });

            userRepository.findByPhoneNumber("90000004")
                    .orElseGet(() -> {
                        User user = new User();
                        user.setPhoneNumber("90000004");
                        user.setName("Admin Test");
                        user.setRole(Role.ADMIN);
                        return userRepository.save(user);
                    });


            /*
             * Demo job 1
             */
            LocalDateTime bartenderStart =
                    LocalDateTime.now()
                            .plusDays(1)
                            .withHour(18)
                            .withMinute(0)
                            .withSecond(0)
                            .withNano(0);

            createJobIfMissing(
                    jobRepository,
                    coordinator,
                    "Bartender Shift",
                    "Central Hotel",
                    "Evening bartender shift for hotel banquet service.",
                    bartenderStart,
                    bartenderStart.plusHours(5),
                    new BigDecimal("120.00"),
                    3
            );


            /*
             * Demo job 2
             */
            LocalDateTime banquetStart =
                    LocalDateTime.now()
                            .plusDays(2)
                            .withHour(17)
                            .withMinute(30)
                            .withSecond(0)
                            .withNano(0);

            createJobIfMissing(
                    jobRepository,
                    coordinator,
                    "Banquet Waiter Shift",
                    "Harbour View Hotel",
                    "Banquet waiter shift for wedding dinner service.",
                    banquetStart,
                    banquetStart.plusHours(6),
                    new BigDecimal("110.00"),
                    5
            );


            /*
             * Demo job 3
             */
            LocalDateTime kitchenStart =
                    LocalDateTime.now()
                            .plusDays(3)
                            .withHour(16)
                            .withMinute(0)
                            .withSecond(0)
                            .withNano(0);

            createJobIfMissing(
                    jobRepository,
                    coordinator,
                    "Kitchen Helper Shift",
                    "Kowloon City Hotel",
                    "Kitchen helper shift for evening food preparation.",
                    kitchenStart,
                    kitchenStart.plusHours(6),
                    new BigDecimal("100.00"),
                    4
            );
        };
    }


    private void createJobIfMissing(
            JobRepository jobRepository,
            User coordinator,
            String title,
            String location,
            String description,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            BigDecimal hourlyRate,
            int totalSlots
    ) {

        Optional<Job> existingJob =
                jobRepository.findByTitleAndLocation(
                        title,
                        location
                );

        /*
         * The development DB may already contain these jobs
         * from the old schema.
         *
         * Backfill the newly introduced fields without
         * resetting filledSlots/status or other test state.
         */
        if (existingJob.isPresent()) {

            Job job = existingJob.get();

            boolean changed = false;

            if (job.getEndDateTime() == null) {
                job.setEndDateTime(endDateTime);
                changed = true;
            }

            if (job.getHourlyRate() == null) {
                job.setHourlyRate(hourlyRate);
                changed = true;
            }

            if (changed) {
                jobRepository.save(job);
            }

            return;
        }


        /*
         * Fresh database: create the complete structured job.
         */
        Job job = new Job();

        job.setTitle(title);
        job.setLocation(location);
        job.setDescription(description);

        job.setJobDate(startDateTime);
        job.setEndDateTime(endDateTime);
        job.setHourlyRate(hourlyRate);

        job.setTotalSlots(totalSlots);
        job.setFilledSlots(0);

        job.setStatus(JobStatus.OPEN);
        job.setCoordinator(coordinator);

        jobRepository.save(job);
    }
}