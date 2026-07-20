# API Reference

Base URL during local development:

```text
http://localhost:8081
```

Android emulator base URL:

```text
http://10.0.2.2:8081/
```

The routes below reflect the current development flow. Update this document whenever controller mappings change.

## Authentication

### Login

```http
POST /api/auth/login
```

Current prototype behavior:

- Login is based on a seeded phone number.
- Password authentication is not implemented yet.
- The response identifies the current user and role.

## Users

### List users

```http
GET /api/users
```

Used for seed-data inspection and development testing.

### Create user

```http
POST /api/users
Content-Type: application/json
```

The current endpoint may still accept a user-shaped body directly. This should later move to a registration or create-user request DTO.

## Jobs

### List jobs

```http
GET /api/jobs
```

### Create a job

```http
POST /api/jobs?coordinatorId={coordinatorId}
Content-Type: application/json
```

The backend validates that the acting user is a coordinator.

Current job information includes the core job title, description, location, date/time, slots, status and coordinator relationship.

## Signups

### List all signups

```http
GET /api/signups
```

This is useful for development, but client screens should prefer role-specific endpoints.

### Get one worker's signups

```http
GET /api/signups/worker/{workerId}
```

### Get applicants for a job

```http
GET /api/signups/job/{jobId}?coordinatorId={coordinatorId}
```

The backend checks that the coordinator owns the job.

### Get signups for a coordinator's jobs

```http
GET /api/signups/coordinator/{coordinatorId}
```

### Apply for a job

```http
POST /api/signups?workerId={workerId}&jobId={jobId}
```

Initial status:

```text
PENDING
```

A worker cannot create a duplicate signup for the same job.

### Approve a signup

```http
PUT /api/signups/{signupId}/approve?coordinatorId={coordinatorId}&reason={optionalReason}
```

Expected effects:

- Signup status becomes `APPROVED`.
- The acting coordinator is recorded.
- The action time and optional reason are recorded.
- Filled-slot information is updated.
- A full job can move to `FULL`.

### Reject a signup

```http
PUT /api/signups/{signupId}/reject?coordinatorId={coordinatorId}&reason={optionalReason}
```

Expected status:

```text
REJECTED
```

### Record attendance

```http
PUT /api/signups/{signupId}/attend
```

Current query parameters:

```text
recordedByUserId
status
lateMinutes
reason
```

Supported attendance statuses:

```text
COMPLETED
LATE
NO_SHOW
```

Example shape:

```http
PUT /api/signups/15/attend?recordedByUserId=1&status=LATE&lateMinutes=10&reason=Traffic
```

## Worker Schedule

### Get a worker's schedule

```http
GET /api/schedules/worker/{workerId}
```

The response groups work into:

- `upcoming`
- `completed`

Each item is designed for the Android calendar and schedule list and can include:

```json
{
  "jobId": 10,
  "signupId": 25,
  "title": "Banquet Server",
  "location": "Kowloon Hotel",
  "date": "2026-07-28",
  "startTime": "11:30",
  "endTime": null,
  "signupStatus": "APPROVED",
  "attendanceStatus": null,
  "lateMinutes": 0
}
```

`endTime` may currently be `null` because the backend job model still needs a fully separated shift-time representation.

## Status reference

### User roles

```text
WORKER
COORDINATOR
ADMIN
```

### Job statuses

```text
OPEN
FULL
CLOSED
CANCELLED
```

### Signup statuses

```text
PENDING
APPROVED
REJECTED
CANCELLED
```

### Attendance statuses

```text
COMPLETED
LATE
NO_SHOW
```

## Error behavior

The backend has structured exception handling for expected failures, including:

- Unknown user, job or signup
- Invalid role
- Coordinator does not own the job
- Job is not open
- Job is full
- Duplicate signup
- Signup has already been processed
- Invalid attendance transition

Duplicate signup should return:

```text
HTTP 409 Conflict
```

Android should display the backend's meaningful message rather than only showing the numeric status code.

## Planned API improvements

- Replace direct entity request and response bodies with specific DTOs.
- Add performance-summary endpoints.
- Add worker-performance detail and history endpoints.
- Add Excel report download endpoints.
- Add registration and stronger authentication endpoints.
- Add pagination and date-range filtering where result sets become large.
