package com.casualapp.backend.model;

public enum SignupStatus {
// PENDING   = worker applied, coordinator has not decided
// APPROVED  = coordinator accepted worker for this job
// REJECTED  = coordinator rejected application
// CANCELLED = worker/coordinator cancelled signup
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED
}

