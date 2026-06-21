package com.casualapp.backend.persistence.ports;

import com.casualapp.backend.model.JobRole;

import java.util.List;
import java.util.Optional;

public interface JobRolePersistence {
    Optional<JobRole> findById(Long roleId);

    Optional<JobRole> findByName(String roleName);

    List<JobRole> findAll();

    JobRole save(JobRole jobRole);

    JobRole update(JobRole jobRole);

    boolean deleteById(Long roleId);
}