# Android UI Screen Map

The Android project currently uses native Java Activities and XML layouts.

## Worker screens

### `LoginActivity`

Purpose:

- Prototype phone-number login
- Select worker or coordinator flow based on the backend user role

Layout:

```text
activity_login.xml
```

### `RegionSelectionActivity`

Purpose:

- Select Kowloon, New Territories or Hong Kong Island
- Continue into the job list
- Provide worker bottom navigation

Layout:

```text
activity_region_selection.xml
```

### `JobListActivity`

Purpose:

- Load jobs from the backend
- Display jobs through `JobAdapter`
- Open job details
- Provide worker bottom navigation

Layout and row:

```text
activity_job_list.xml
item_job_card.xml
```

### `JobDetailActivity`

Purpose:

- Show job information
- Select available shift options
- Continue to confirmation

Layout:

```text
activity_job_detail.xml
```

### `ConfirmApplyActivity`

Purpose:

- Review selected job
- Submit the signup request

Layout:

```text
activity_confirm_apply.xml
```

### `ApplySuccessActivity`

Purpose:

- Display signup result, ID and status
- Return to the job list
- Open My Jobs

Layout:

```text
activity_apply_success.xml
```

### `MyJobsLandingActivity`

Purpose:

- Entry page for applications and schedule
- Worker bottom navigation

Layout:

```text
activity_my_jobs_landing.xml
```

### `MyJobsActivity`

Purpose:

- Load and display the current worker's applications
- Open My Schedule

Layout and row:

```text
activity_my_jobs.xml
item_application_card.xml
```

### `MyScheduleActivity`

Purpose:

- Display the current date
- Render a monthly calendar
- Highlight shift dates
- Show upcoming and completed work
- Navigate between months

Layout and row:

```text
activity_my_schedule.xml
item_schedule_row.xml
```

## Coordinator screens

### `CoordinatorHomeActivity`

Purpose:

- Coordinator navigation hub
- Access job creation, posted jobs and attendance flow

Layout:

```text
activity_coordinator_home.xml
```

### `CreateJobActivity`

Purpose:

- Enter and submit new job information

Layout:

```text
activity_create_job.xml
```

### `CoordinatorJobsActivity`

Purpose:

- List jobs owned by the current coordinator
- Reuse the list for normal management and attendance mode

Layout:

```text
activity_coordinator_jobs.xml
```

### `CoordinatorApplicantsActivity`

Purpose:

- Display applicants for a selected job
- Approve or reject applications
- Enter attendance actions where appropriate

Layout:

```text
activity_coordinator_applicants.xml
```

## Shared and support classes

```text
ApplicationAdapter
JobAdapter
JobDateFormatter
UserSession
ApiService
RetrofitClient
```

## Legacy test screen

### `MainActivity`

`MainActivity` remains a backend smoke-test harness from early development.

It should not be part of the customer-facing demo navigation. Keep it only while it still provides useful endpoint debugging, then remove or move the test functionality.

## Bottom-navigation targets

Current worker navigation design:

```text
工作列表
工作記錄
我的工作
個人檔案
```

Current state:

- Work list: implemented
- My Jobs: implemented
- Schedule: accessible from My Jobs
- Work history: partly represented by schedule/attendance data; dedicated screen unfinished
- Profile: unfinished

Every layout that contains this navigation must give each tab an ID and attach a listener. Copying the visual bar without listeners creates a screen where the buttons appear active but do nothing.

## UI priorities before the demo

- Apply the official CasualApp logo and app icon.
- Standardize top-bar spacing, text and back-button behavior.
- Standardize bottom-navigation IDs and behavior.
- Ensure coordinator controls are hidden from workers.
- Ensure worker application actions are hidden from coordinators.
- Add consistent loading, empty and error views.
- Replace English `coming soon` text with final Traditional Chinese wording.
- Test the calendar on multiple screen sizes.
- Keep performance tables readable on a phone.
