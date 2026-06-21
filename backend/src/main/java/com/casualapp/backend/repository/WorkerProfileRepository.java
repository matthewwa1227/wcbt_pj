package com.casualapp.backend.repository;

import com.casualapp.backend.model.WorkerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerProfileRepository extends JpaRepository<WorkerProfile, Long> {
}