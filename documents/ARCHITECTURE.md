# Architecture

## System overview

```text
Android application
        │
        │ Retrofit / JSON over HTTP
        ▼
Spring Boot REST API
        │
        │ Spring Data JPA
        ▼
PostgreSQL in Docker
```

The current repository contains a native Android client and a Spring Boot backend. The backend owns validation, authorization checks, persistence and aggregation. Android is responsible for session state, navigation and presentation.

## Simplified repository structure

```text
wcbt_pj/
├── backend/
│   ├── src/main/java/com/casualapp/backend/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── model/
│   │   ├── persistence/
│   │   │   ├── filters/
│   │   │   └── ports/
│   │   ├── repository/
│   │   ├── service/
│   │   └── BackendApplication.java
│   └── src/main/resources/
│
└── CasualApp/
    └── app/src/main/
        ├── AndroidManifest.xml
        ├── java/com/casualapp/android/
        │   ├── activities
        │   ├── adapters
        │   ├── model/
        │   └── network/
        └── res/
            ├── drawable/
            ├── layout/
            ├── values/
            └── xml/
```

IDE metadata, generated sources, compiled classes, Gradle caches, Maven targets and APK outputs are not part of the logical architecture.

## Backend package responsibilities

### `config`

Current responsibilities:

- CORS configuration
- Development seed data

Key files include:

```text
CorsConfig.java
DevDataSeeder.java
```

### `controller`

REST entry points and API error handling.

Current controllers include:

```text
AuthController
UserController
JobController
JobSignupController
WorkerScheduleController
```

Error-related classes currently live in the same package:

```text
ApiErrorResponse
ApiException
GlobalExceptionHandler
```

`ApiErrorResponse` should later move into `dto/common`.

### `dto`

Data Transfer Objects define stable API request and response shapes.

Current schedule DTOs:

```text
WorkerScheduleItemResponse
WorkerScheduleResponse
```

The long-term organization is documented in `DTO_REFACTORING.md`.

### `model`

JPA entities and status enums.

Current notable models:

```text
User
Role
Job
JobStatus
JobSignup
SignupStatus
JobAttendance
AttendanceStatus
EventLog
Venue
WorkerProfile
JobRole
LoginRequest
```

`LoginRequest` is transport data and should eventually move from `model` into `dto/auth`.

### `repository`

Spring Data JPA repositories for entity access.

Current repository coverage includes users, jobs, signups, attendance, events, venues, worker profiles and job roles.

### `service`

Business and aggregation logic.

Current service:

```text
WorkerScheduleService
```

Future performance calculations and Excel-report preparation should also be placed in services rather than controllers.

### `persistence`

The project also includes persistence ports, filters and a factory scaffold.

This layer can support a cleaner separation between application logic and Spring Data implementations, but it should either be integrated consistently or kept clearly marked as an architectural work in progress.

## Android package responsibilities

### Activities

The Android project currently uses an Activity-based screen structure. Important screens are documented in `UI_SCREEN_MAP.md`.

### Adapters

Current RecyclerView adapters include job and application list rendering.

### `model`

Retrofit response models mirror the backend JSON required by the mobile screens.

The Android project has models for users, jobs, signups, attendance and worker schedules.

### `network`

```text
ApiService.java
RetrofitClient.java
```

`ApiService` defines Retrofit endpoints. `RetrofitClient` configures the base URL and JSON conversion.

### `res/layout`

Native Android XML screens and list rows.

### `res/drawable`

Reusable backgrounds, state shapes, calendar-day states, cards and navigation styling.

## Current data flow examples

### Apply for a job

```text
JobListActivity
→ JobDetailActivity
→ ConfirmApplyActivity
→ POST /api/signups
→ ApplySuccessActivity
→ MyJobsActivity
```

### Approve and record attendance

```text
CoordinatorJobsActivity
→ CoordinatorApplicantsActivity
→ approve/reject endpoint
→ attendance endpoint
→ PostgreSQL
```

### Build worker schedule

```text
MyScheduleActivity
→ GET /api/schedules/worker/{workerId}
→ WorkerScheduleController
→ WorkerScheduleService
→ JobSignupRepository + JobAttendanceRepository
→ WorkerScheduleResponse
```

## Architectural rules going forward

- Controllers should remain thin.
- Business calculations belong in services.
- Repositories should only handle persistence queries.
- API contracts should use DTOs instead of complete JPA entities.
- Android should not calculate authoritative attendance or earnings totals.
- The backend should be the single source of truth for performance and exports.
- IDs returned by API responses must be used instead of fixed development IDs.
- Generated and IDE-specific folders should remain excluded from documentation and version control where appropriate.
