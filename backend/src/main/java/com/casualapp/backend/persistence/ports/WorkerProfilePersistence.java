package com.casualapp.backend.persistence.ports;

import com.casualapp.backend.model.WorkerProfile;

import java.util.Optional;

public interface WorkerProfilePersistence {
    Optional<WorkerProfile> findByUserId(Long userId);

    WorkerProfile save(WorkerProfile workerProfile);

    WorkerProfile update(WorkerProfile workerProfile);

    boolean deleteByUserId(Long userId);
}