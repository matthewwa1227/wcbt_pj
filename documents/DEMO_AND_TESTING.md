# Demo and Testing Guide

## Demo objective

The customer demo should prove the business workflow rather than expose development internals.

Primary demo story:

```text
Coordinator creates a shift
→ Worker finds and applies for it
→ Coordinator approves the worker
→ Coordinator records attendance
→ Worker sees the application and schedule update
→ Performance data is shown
→ A performance Excel report is downloaded
```

Performance and Excel export are the next major additions. Until they are finished, the stable demo endpoint is the schedule update after approval and attendance.

## Test accounts

Current seeded roles:

| Account | Role |
|---|---|
| Boss Chan | Coordinator |
| Worker A | Worker |
| Worker B | Worker |
| Admin Test | Admin |

Read the current phone numbers from `DevDataSeeder.java`. IDs can change after a reset.

## Pre-demo checklist

### Database and backend

- [ ] Docker Desktop is running.
- [ ] PostgreSQL container is healthy.
- [ ] Backend starts without schema or connection errors.
- [ ] Seed data is in the expected state.
- [ ] The backend responds on port `8081`.
- [ ] `/api/users` returns the expected accounts.
- [ ] `/api/jobs` returns the expected demo jobs.

### Android

- [ ] Correct Retrofit base URL is configured.
- [ ] The debug application builds successfully.
- [ ] The app opens to the intended login screen.
- [ ] The legacy `MainActivity` test screen is not part of the public demo path.
- [ ] All visible demo buttons work.
- [ ] Secondary unfinished buttons display a meaningful development message.
- [ ] Bottom navigation works on every screen used in the demo.

### Demo data

- [ ] At least one open job is available for Worker A.
- [ ] Worker A has not already applied for that exact job.
- [ ] The job still has open slots.
- [ ] Boss Chan owns the job used in the demo.
- [ ] At least one historical attendance record exists for the schedule screen.
- [ ] Performance seed data is loaded once that feature is implemented.

## Manual happy-flow test

### Part 1: Coordinator creates a job

1. Log in as Boss Chan.
2. Open the create-job screen.
3. Enter the location, title, date/time and slots.
4. Save.
5. Return to the posted-job list.
6. Confirm the job appears.

Expected result:

- Backend creates a persisted job.
- The coordinator can see it after refresh.
- The job begins in an open state.

### Part 2: Worker applies

1. Log in as Worker A.
2. Select a region.
3. Open the work list.
4. Select the newly created job.
5. Review the details.
6. Submit the application.
7. Open My Applications.

Expected result:

- The backend returns a new signup ID.
- Signup status is `PENDING`.
- The application remains visible after leaving and reopening the page.

### Part 3: Coordinator approves

1. Log back in as Boss Chan.
2. Open the relevant posted job.
3. Open its applicant list.
4. Approve Worker A.

Expected result:

- Status becomes `APPROVED`.
- Filled slots are updated.
- The worker sees the new status when the page reloads.

### Part 4: Record attendance

1. Open the attendance flow.
2. Select Worker A's approved signup.
3. Record `COMPLETED`, `LATE` or `NO_SHOW`.
4. For `LATE`, enter late minutes.

Expected result:

- One attendance record exists for the worker/job combination.
- The record includes the acting coordinator and recording time.
- The worker schedule moves the shift into completed history when appropriate.

### Part 5: Worker schedule

1. Log in as Worker A.
2. Open My Jobs.
3. Open My Schedule.
4. Navigate the monthly calendar.

Expected result:

- Shift dates are highlighted.
- Upcoming and completed entries are separated.
- Data comes from the worker schedule endpoint.

## Negative tests

- [ ] Worker attempts a duplicate signup and receives a meaningful conflict.
- [ ] Worker attempts to apply to a full job.
- [ ] Coordinator attempts to manage another coordinator's job.
- [ ] Attendance is attempted for a non-approved signup.
- [ ] Attendance is recorded twice for the same worker and job.
- [ ] Backend is unavailable and Android displays a clear network error.
- [ ] Empty application and schedule lists display an empty state.
- [ ] Database reset does not break the flow through hard-coded IDs.

## Regression checklist after backend changes

- [ ] Login still returns the correct role.
- [ ] Jobs load.
- [ ] Create job still works.
- [ ] Signup returns its actual ID.
- [ ] Approval updates signup and slots.
- [ ] Rejection updates the signup.
- [ ] Attendance persists.
- [ ] Schedule endpoint still serializes.
- [ ] Android Retrofit models match backend DTO fields.
- [ ] All relevant screens refresh in `onResume`.

## Demo presentation guidance

- Use predictable seed data instead of creating every record live.
- Demonstrate one live creation/application path, then use historical seed records for richer tables.
- Keep a backup screen recording or screenshots available.
- Avoid discussing unfinished production security work unless the client asks.
- Describe phone-only login as a prototype authentication flow.
- Do not call unfinished placeholders part of the completed feature set.
