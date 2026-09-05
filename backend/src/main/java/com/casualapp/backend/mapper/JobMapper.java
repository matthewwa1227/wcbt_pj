package com.casualapp.backend.mapper;

import org.springframework.stereotype.Component;

import com.casualapp.backend.dto.job.JobResponse;
import com.casualapp.backend.model.Job;

@Component
public class JobMapper {

    public JobResponse toResponse(
            Job job
    ) {

        JobResponse response =
                new JobResponse();

        response.setId(
                job.getId()
        );

        response.setTitle(
                job.getTitle()
        );

        response.setDescription(
                job.getDescription()
        );

        response.setLocation(
                job.getLocation()
        );

        /*
         * Entity field is still named jobDate,
         * but it represents the shift start datetime.
         */
        response.setStartDateTime(
                job.getJobDate()
        );

        response.setEndDateTime(
                job.getEndDateTime()
        );

        response.setHourlyRate(
                job.getHourlyRate()
        );

        response.setTotalSlots(
                job.getTotalSlots()
        );

        response.setFilledSlots(
                job.getFilledSlots()
        );

        response.setStatus(
                job.getStatus() == null
                        ? null
                        : job.getStatus().name()
        );

        if (job.getCoordinator() != null) {

            response.setCoordinatorId(
                    job.getCoordinator().getId()
            );

            response.setCoordinatorName(
                    job.getCoordinator().getName()
            );
        }

        response.setCreatedAt(
                job.getCreatedAt()
        );

        return response;
    }
}