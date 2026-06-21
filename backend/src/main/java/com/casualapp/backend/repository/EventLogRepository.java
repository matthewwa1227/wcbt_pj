package com.casualapp.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.casualapp.backend.model.EventLog;

@Repository
public interface EventLogRepository extends JpaRepository<EventLog, Long> {
    List<EventLog> findByEventType(String eventType);

    List<EventLog> findByActorUserId(Long actorUserId);

    List<EventLog> findByTargetTableAndTargetId(String targetTable, Long targetId);

    List<EventLog> findByJobId(Long jobId);

    List<EventLog> findByWorkerId(Long workerId);
}