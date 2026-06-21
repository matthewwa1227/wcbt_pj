package com.casualapp.backend.persistence.ports;

import com.casualapp.backend.model.Job;
import com.casualapp.backend.persistence.filters.JobFilter;

import java.util.List;
import java.util.Optional;

public interface JobPersistence {
    Optional<Job> findById(Long jobId);

    List<Job> findAll();

    List<Job> findByFilter(JobFilter filter);

    List<Job> findByCoordinatorId(Long coordinatorId);

    List<Job> findByStatus(String status);

    List<Job> findOpenJobs();

    Job save(Job job);

    Job update(Job job);

    boolean deleteById(Long jobId);
}