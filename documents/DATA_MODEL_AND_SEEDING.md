# Data Model and Seed-Data Notes

## Main domain models

### User

Represents workers, coordinators and administrators.

Roles:

```text
WORKER
COORDINATOR
ADMIN
```

### Job

Represents one published work shift.

Current responsibilities include:

- Title
- Description
- Location
- Job date/time
- Total slots
- Filled slots
- Status
- Coordinator
- Creation time

Job statuses:

```text
OPEN
FULL
CLOSED
CANCELLED
```

### JobSignup

Connects a worker to a job application.

Important fields include:

- Worker
- Job
- Status
- Signup time
- Updated time
- Acting coordinator
- Action reason

Signup statuses:

```text
PENDING
APPROVED
REJECTED
CANCELLED
```

The worker/job pair is intended to be unique.

### JobAttendance

Stores the attendance result for one worker and one job.

Important fields include:

- Worker
- Job
- Attendance status
- Late minutes
- Notes/reason
- Recorded-by user
- Recorded time

Attendance statuses:

```text
COMPLETED
LATE
NO_SHOW
```

The job/worker pair is intended to have one attendance record.

### EventLog

Provides a basis for audit-style operational logging.

### Venue

Represents a named work location and related address/district information.

### WorkerProfile

Stores worker-specific profile information beyond the base `User` record.

### JobRole

Represents job-role information that can become more structured than a free-text job title.

## Current relationship flow

```text
Coordinator User
    └── creates Job

Worker User
    └── creates JobSignup for Job

Coordinator User
    └── approves/rejects JobSignup

Coordinator User
    └── records JobAttendance for Worker + Job
```

## Current data limitation

The current schedule work exposed an important limitation: the job model does not yet provide a clean, stable start/end representation for every API consumer.

Before reliable hours and earnings calculations, introduce explicit shift boundaries:

```text
startDateTime
endDateTime
```

Work duration should then be calculated in the backend.

## Current seed users

| User | Role |
|---|---|
| Boss Chan | Coordinator |
| Worker A | Worker |
| Worker B | Worker |
| Admin Test | Admin |

Exact phone numbers and IDs belong in `DevDataSeeder.java`, not permanent documentation.

## Seed-data expansion for performance demos

Performance tables need multiple months of coherent history.

Recommended target:

```text
6–10 workers
2 coordinators
20–30 jobs
50+ signups
40+ attendance records
2–3 months
```

Create deliberate worker profiles.

### Reliable worker

- Mostly approved applications
- Completed attendance
- Minimal lateness
- No or very few no-shows

### Occasionally late worker

- Regular completed shifts
- Several late records
- Varied late minutes

### Unreliable worker

- Multiple no-shows
- Lower completion rate
- Some rejected or cancelled applications

### New worker

- Only a few recent records
- Avoid presenting a small sample as a strong rating

## Seed-data consistency rules

- Attendance should normally belong to an approved signup.
- No duplicate signup for the same worker and job.
- No duplicate attendance for the same worker and job.
- Filled slots should agree with approved signups.
- Job status should agree with slot availability.
- Job dates should cover past and future periods.
- Earnings should be reproducible from hourly rate and worked duration once those fields exist.
- IDs should never be assumed by Android code.

## Performance calculation principles

Keep calculations transparent.

Examples:

```text
attendanceRate =
    (completed + late) / attendance-eligible shifts

noShowRate =
    no-show shifts / attendance-eligible shifts

averageLateMinutes =
    total late minutes / late shifts
```

Define the exact denominator once and document it. Do not allow Android and Excel export to implement different formulas.

## Data reset warning

Running:

```powershell
docker compose down -v
```

removes development database data. On restart, seed records can be recreated with different IDs. API-response IDs must be captured dynamically.
