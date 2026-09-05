package com.casualapp.backend.mapper;

import org.springframework.stereotype.Component;

import com.casualapp.backend.dto.signup.SignupActionResponse;
import com.casualapp.backend.dto.signup.SignupResponse;
import com.casualapp.backend.model.JobSignup;

@Component
public class SignupMapper {

    public SignupResponse toResponse(JobSignup signup) {

        SignupResponse response = new SignupResponse();

        response.setId(signup.getId());

        if (signup.getWorker() != null) {
            response.setWorkerId(
                    signup.getWorker().getId()
            );

            response.setWorkerName(
                    signup.getWorker().getName()
            );

            response.setWorkerPhoneNumber(
                    signup.getWorker().getPhoneNumber()
            );
        }

        if (signup.getJob() != null) {
            response.setJobId(
                    signup.getJob().getId()
            );

            response.setJobTitle(
                    signup.getJob().getTitle()
            );

            response.setJobLocation(
                    signup.getJob().getLocation()
            );

            response.setJobDate(
                    signup.getJob().getJobDate() == null
                            ? null
                            : signup.getJob()
                                    .getJobDate()
                                    .toString()
            );
        }

        response.setStatus(
                signup.getStatus() == null
                        ? null
                        : signup.getStatus().name()
        );

        response.setSignupTime(
                signup.getSignupTime() == null
                        ? null
                        : signup.getSignupTime()
                                .toString()
        );

        response.setUpdatedAt(
                signup.getUpdatedAt() == null
                        ? null
                        : signup.getUpdatedAt()
                                .toString()
        );

        response.setActionReason(
                signup.getActionReason()
        );

        if (signup.getActionedBy() != null) {
            response.setActionedByUserId(
                    signup.getActionedBy().getId()
            );

            response.setActionedByName(
                    signup.getActionedBy().getName()
            );
        }

        return response;
    }

    public SignupActionResponse toActionResponse(
            JobSignup signup
    ) {

        return new SignupActionResponse(
                signup.getId(),
                signup.getStatus() == null
                        ? null
                        : signup.getStatus().name(),
                signup.getUpdatedAt(),
                signup.getActionReason()
        );
    }
}