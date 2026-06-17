package com.casualapp.backend.repository;

import com.casualapp.backend.model.JobSignup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobSignupRepository extends JpaRepository<JobSignup, Long> {
}