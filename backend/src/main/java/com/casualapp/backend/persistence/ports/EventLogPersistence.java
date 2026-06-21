package com.casualapp.backend.persistence.ports;

import com.casualapp.backend.model.EventLog;
import com.casualapp.backend.persistence.filters.EventLogFilter;

import java.util.List;
import java.util.Optional;

public interface EventLogPersistence {
    Optional<EventLog> findById(Long eventId);

    List<EventLog> findAll();

    List<EventLog> findByFilter(EventLogFilter filter);

    List<EventLog> findByEventType(String eventType);

    List<EventLog> findByActorUserId(Long actorUserId);

    List<EventLog> findByTargetTableAndTargetId(String targetTable, Long targetId);

    List<EventLog> findByJobId(Long jobId);

    List<EventLog> findByWorkerId(Long workerId);

    EventLog save(EventLog eventLog);
}