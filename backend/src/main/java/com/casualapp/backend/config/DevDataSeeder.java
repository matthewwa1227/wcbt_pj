package com.casualapp.backend.config;

import java.time.LocalDateTime;

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
    CommandLineRunner seedDevData(UserRepository userRepository, JobRepository jobRepository) {
        return args -> {
            User coordinator = userRepository.findByPhoneNumber("90000001")
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

            createJobIfMissing(
                    jobRepository,
                    coordinator,
                    "Bartender Shift",
                    "Central Hotel",
                    "Evening bartender shift for hotel banquet service.",
                    LocalDateTime.now().plusDays(1).withHour(18).withMinute(0).withSecond(0).withNano(0),
                    3
            );

            createJobIfMissing(
                    jobRepository,
                    coordinator,
                    "Banquet Waiter Shift",
                    "Harbour View Hotel",
                    "Banquet waiter shift for wedding dinner service.",
                    LocalDateTime.now().plusDays(2).withHour(17).withMinute(30).withSecond(0).withNano(0),
                    5
            );

            createJobIfMissing(
                    jobRepository,
                    coordinator,
                    "Kitchen Helper Shift",
                    "Kowloon City Hotel",
                    "Kitchen helper shift for evening food preparation.",
                    LocalDateTime.now().plusDays(3).withHour(16).withMinute(0).withSecond(0).withNano(0),
                    4
            );
        };
    }

    private void createJobIfMissing(JobRepository jobRepository,
                                    User coordinator,
                                    String title,
                                    String location,
                                    String description,
                                    LocalDateTime jobDate,
                                    int totalSlots) {
        jobRepository.findByTitleAndLocation(title, location)
                .orElseGet(() -> {
                    Job job = new Job();
                    job.setTitle(title);
                    job.setLocation(location);
                    job.setDescription(description);
                    job.setJobDate(jobDate);
                    job.setTotalSlots(totalSlots);
                    job.setFilledSlots(0);
                    job.setStatus(JobStatus.OPEN);
                    job.setCoordinator(coordinator);
                    return jobRepository.save(job);
                });
    }
}