package com.casualapp.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.casualapp.backend.model.JobSignup;
import com.casualapp.backend.model.SignupStatus;

@Repository
public interface JobSignupRepository extends JpaRepository<JobSignup, Long> {

    Optional<JobSignup> findByJobIdAndWorkerId(Long jobId, Long workerId);

    List<JobSignup> findByJobId(Long jobId);

    List<JobSignup> findByWorkerId(Long workerId);

    List<JobSignup> findByStatus(SignupStatus status);
}