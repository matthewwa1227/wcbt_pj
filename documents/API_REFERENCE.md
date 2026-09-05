# API Reference

Base URL during local development:

```text
http://localhost:8081
```

Android emulator base URL:

```text
http://10.0.2.2:8081/
```

The routes below reflect the current controllers and DTO-based request/response contracts.

## Authentication

### Login

```http
POST /api/auth/login
Content-Type: application/json
```

Request body:

```json
{
  "phoneNumber": "90000001",
  "password": ""
}
```

Current prototype behavior:

- Login looks up the seeded user by phone number.
- Password verification is not implemented yet.
- The response contains the user's id, phone number, name, role and creation time.

## Users

### List users

```http
GET /api/users
```

Returns user summary DTOs for development and testing.

There is currently no `POST /api/users` endpoint. Registration should be added later with dedicated request/response DTOs rather than accepting a `User` entity.

## Jobs

### List jobs

```http
GET /api/jobs
```

### Get one job

```http
GET /api/jobs/{jobId}
```

### Get jobs owned by a coordinator

```http
GET /api/jobs/coordinator/{coordinatorId}
```

### Create a job

```http
POST /api/jobs
Content-Type: application/json
```

Request body:

```json
{
  "coordinatorId": 1,
  "title": "Banquet Server",
  "description": "Dinner event shift",
  "location": "Kowloon Hotel",
  "startDateTime": "2026-09-10T17:00:00",
  "endDateTime": "2026-09-10T23:00:00",
  "hourlyRate": 90.00,
  "totalSlots": 5
}
```

The backend validates the coordinator and returns a `JobResponse` rather than the persistence entity.

## Signups

### List all signups

```http
GET /api/signups
```

Useful for development. Client screens should normally prefer the role-specific endpoints below.

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

The signup GET endpoints return the full `SignupResponse`, including worker, job and action information required by application screens.

### Apply for a job

```http
POST /api/signups
Content-Type: application/json
```

Request body:

```json
{
  "workerId": 2,
  "jobId": 10
}
```

Initial status is `PENDING`. A worker cannot create a duplicate signup for the same job.

Successful signup mutations return the focused `SignupActionResponse`:

```json
{
  "id": 25,
  "status": "PENDING",
  "updatedAt": "2026-09-05T10:20:30",
  "actionReason": null
}
```

### Approve a signup

```http
PUT /api/signups/{signupId}/approve
Content-Type: application/json
```

Request body:

```json
{
  "coordinatorId": 1,
  "reason": "Approved by coordinator"
}
```

Expected effects:

- Signup status becomes `APPROVED`.
- The acting coordinator is recorded internally.
- The action time and optional reason are recorded.
- Filled-slot information is updated.
- A full job can move to `FULL`.

The response is `SignupActionResponse`.

### Reject a signup

```http
PUT /api/signups/{signupId}/reject
Content-Type: application/json
```

Request body:

```json
{
  "coordinatorId": 1,
  "reason": "Shift already filled"
}
```

Expected status is `REJECTED`. The response is `SignupActionResponse`.

### Record attendance

```http
PUT /api/signups/{signupId}/attend
Content-Type: application/json
```

Request body:

```json
{
  "recordedByUserId": 1,
  "status": "LATE",
  "lateMinutes": 10,
  "reason": "Traffic"
}
```

Supported attendance statuses:

```text
COMPLETED
LATE
NO_SHOW
```

The response is an `AttendanceResponse` containing the attendance id, signup id, worker id, job id, status, late minutes, notes, recorded time and recorder id.

## Worker Schedule

### Get a worker's schedule

```http
GET /api/schedules/worker/{workerId}
```

The response groups schedule items into:

- `upcoming`
- `completed`

The schedule contract is DTO-based and designed for the Android schedule screens.

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
