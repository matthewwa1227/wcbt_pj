package com.casualapp.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casualapp.backend.dto.schedule.WorkerScheduleResponse;
import com.casualapp.backend.service.WorkerScheduleService;

@RestController
@RequestMapping("/api/schedules")
public class WorkerScheduleController {

    private final WorkerScheduleService workerScheduleService;

    public WorkerScheduleController(
            WorkerScheduleService workerScheduleService
    ) {
        this.workerScheduleService = workerScheduleService;
    }

    @GetMapping("/worker/{workerId}")
    public WorkerScheduleResponse getWorkerSchedule(
            @PathVariable Long workerId
    ) {
        return workerScheduleService.getWorkerSchedule(workerId);
    }
}
