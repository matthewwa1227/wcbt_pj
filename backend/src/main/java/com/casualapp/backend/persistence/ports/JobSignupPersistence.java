package com.casualapp.backend.persistence.ports;

import com.casualapp.backend.model.JobSignup;
import com.casualapp.backend.persistence.filters.JobSignupFilter;

import java.util.List;
import java.util.Optional;

public interface JobSignupPersistence {
    Optional<JobSignup> findById(Long signupId);

    Optional<JobSignup> findByJobIdAndWorkerId(Long jobId, Long workerId);

    List<JobSignup> findAll();

    List<JobSignup> findByFilter(JobSignupFilter filter);

    List<JobSignup> findByJobId(Long jobId);

    List<JobSignup> findByWorkerId(Long workerId);

    List<JobSignup> findByStatus(String status);

    JobSignup save(JobSignup jobSignup);

    JobSignup update(JobSignup jobSignup);

    boolean deleteById(Long signupId);
}