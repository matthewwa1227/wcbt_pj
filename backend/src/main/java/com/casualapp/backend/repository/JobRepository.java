package com.casualapp.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.casualapp.backend.model.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    Optional<Job> findByTitleAndLocation(String title, String location);

    // Used by the coordinator "My Posted Jobs" screen.
    List<Job> findByCoordinatorId(Long coordinatorId);
}