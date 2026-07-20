# Roadmap and Future Improvements

## Immediate demo priorities

### 1. Stabilize the existing happy flow

- Complete a full regression pass.
- Ensure all visible buttons work or display a meaningful message.
- Remove broken or legacy navigation from the customer demo.
- Standardize top bars and bottom navigation.
- Add consistent loading, empty and error states.

### 2. Expand seed data

The performance feature needs believable historical data.

Target:

```text
6–10 workers
2 coordinators
20–30 jobs
50+ signups
40+ attendance records
2–3 months of history
```

Seed profiles should include:

- Highly reliable worker
- Occasionally late worker
- Worker with repeated no-shows
- New worker with limited history

### 3. Performance API

Create backend calculations for:

- Total applications
- Approved shifts
- Completed shifts
- Late shifts
- No-show shifts
- Attendance rate
- Completion rate
- Total and average late minutes
- Total work hours
- Estimated earnings
- Monthly hours
- Monthly earnings

The backend must be the authoritative source for the calculations.

Suggested endpoints:

```text
GET /api/performance/workers
GET /api/performance/workers/{workerId}
GET /api/performance/workers/{workerId}/history
```

### 4. Performance UI

Add:

- Coordinator worker-comparison table
- Individual worker performance screen
- Worker personal performance screen
- Simple attendance, hours and earnings charts
- Date-range or month selection when time permits

### 5. Excel export

Generate `.xlsx` reports in the backend.

Suggested report content:

- Worker summary
- Detailed shift and attendance history
- Late and no-show records
- Monthly hours
- Monthly earnings
- Location and job information

Suggested endpoints:

```text
GET /api/performance/workers/export
GET /api/performance/workers/{workerId}/export
```

The UI and Excel file must use the same calculation service.

## Near-term engineering improvements

### DTO refactoring

Follow the plan in `DTO_REFACTORING.md`.

Priority order:

1. Schedule DTOs
2. Performance DTOs
3. Attendance DTOs
4. Signup DTOs
5. Authentication and registration DTOs
6. Job DTOs
7. Common API error and pagination DTOs

### Shift-time model

Replace the ambiguous single job date/time representation with explicit fields such as:

```text
startDateTime
endDateTime
```

or a start date plus start/end times.

Then update:

- Job creation
- Job details
- Schedule response
- Attendance history
- Work-hours calculation
- Earnings calculation

### Authentication and registration

After the demo:

- Worker registration
- Duplicate-phone validation
- Password or one-time-code authentication
- Password reset
- Phone verification
- Persistent login
- Logout
- Coordinator invitation or admin approval
- Restrict privileged role creation

### API quality

- Role-specific list endpoints
- Pagination
- Date-range filtering
- Request validation
- Automated controller/service tests
- OpenAPI documentation when the routes stabilize

### Android quality

- Reusable navigation helper or Fragment-based navigation
- Centralized error handling
- Lifecycle-safe network state
- View binding
- Better accessibility labels
- Consistent Traditional Chinese strings through resources
- Automated UI tests for the core flow

## Later-stage work

These items are intentionally deferred until the demo workflow and performance feature are stable:

- Alibaba Cloud deployment
- Production HTTPS and secrets handling
- Load testing
- Penetration and security testing
- Production monitoring and logging
- Backup and restore procedures
- Release signing and store distribution
- Advanced predictive worker scoring

## Product principle

Do not add unrelated features before the main value proposition is demonstrable:

> Attendance records become clear performance and earnings information that coordinators can view in the app and export for operational use.
