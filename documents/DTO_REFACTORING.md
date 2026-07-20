# DTO Refactoring Plan

DTO means **Data Transfer Object**.

DTOs define the exact data accepted or returned by an API operation. They keep API contracts separate from JPA entities and prevent the frontend from depending on the database model.

## Target structure

```text
dto/
├── auth/
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── RegisterWorkerRequest.java
│   └── RegisterWorkerResponse.java
│
├── job/
│   ├── CreateJobRequest.java
│   ├── UpdateJobRequest.java
│   ├── JobListItemResponse.java
│   └── JobDetailResponse.java
│
├── signup/
│   ├── SignupRequest.java
│   ├── SignupResponse.java
│   ├── ApproveSignupRequest.java
│   └── RejectSignupRequest.java
│
├── attendance/
│   ├── AttendanceRequest.java
│   ├── AttendanceResponse.java
│   └── AttendanceHistoryResponse.java
│
├── schedule/
│   ├── WorkerScheduleResponse.java
│   └── WorkerScheduleItemResponse.java
│
├── performance/
│   ├── WorkerPerformanceSummaryResponse.java
│   ├── WorkerPerformanceDetailResponse.java
│   ├── MonthlyPerformanceResponse.java
│   └── EarningsSummaryResponse.java
│
└── common/
    ├── ApiErrorResponse.java
    └── PageResponse.java
```

## Current state

The current backend already has:

```text
WorkerScheduleItemResponse
WorkerScheduleResponse
```

They currently sit directly under `dto/`. They can later move into `dto/schedule/` when the rest of the package hierarchy is introduced.

Transport classes currently outside the target location include:

- `LoginRequest` under `model`
- `ApiErrorResponse` under `controller`

These should be moved only when imports and API tests are updated in the same change.

## Migration order

### Phase 1: Schedule

- Keep the existing schedule response stable.
- Move the classes into `dto/schedule`.
- Update imports in `WorkerScheduleController` and `WorkerScheduleService`.
- Confirm Android Gson parsing still works.

### Phase 2: Performance

Add response DTOs before building the Android table:

```text
WorkerPerformanceSummaryResponse
WorkerPerformanceDetailResponse
MonthlyPerformanceResponse
EarningsSummaryResponse
```

Performance DTOs should expose calculated values, not JPA relationships.

### Phase 3: Attendance

Replace multiple attendance query parameters with a request body:

```java
public class AttendanceRequest {
    private Long recordedByUserId;
    private String status;
    private Integer lateMinutes;
    private String reason;
}
```

Return an `AttendanceResponse` rather than a complete `JobAttendance` entity.

### Phase 4: Signup actions

Add focused request and response classes:

```text
SignupRequest
SignupResponse
ApproveSignupRequest
RejectSignupRequest
```

This makes action reasons and acting users explicit and easier to validate.

### Phase 5: Authentication and registration

Move `LoginRequest` out of `model`.

Add:

```text
LoginResponse
RegisterWorkerRequest
RegisterWorkerResponse
```

Do not expose password hashes, internal entity relationships or unrestricted role fields.

### Phase 6: Jobs

Separate list and detail contracts:

```text
JobListItemResponse
JobDetailResponse
```

Use `CreateJobRequest` and `UpdateJobRequest` for writes.

### Phase 7: Common responses

Move structured errors into:

```text
dto/common/ApiErrorResponse.java
```

Add a generic paginated response when list APIs require pagination.

## DTO design rules

A DTO should normally contain:

- Fields
- Constructors
- Getters and setters
- Simple serialization annotations when necessary

A DTO should not contain:

- JPA annotations
- Repository references
- Service calls
- Database save methods
- Lazy-loaded entity collections
- Authoritative business calculations

## Mapping

Initially, mapping can remain explicit inside a service:

```java
WorkerScheduleItemResponse response = new WorkerScheduleItemResponse();
response.setJobId(job.getId());
response.setTitle(job.getTitle());
```

As DTO usage expands, add focused mapper classes, for example:

```text
JobMapper
SignupMapper
AttendanceMapper
PerformanceMapper
```

Avoid introducing a mapping framework until manual mapping becomes repetitive enough to justify it.

## Compatibility strategy

For every DTO migration:

1. Write down the current JSON.
2. Add the DTO and mapper.
3. Keep field names stable when possible.
4. Update the controller.
5. Compile the backend.
6. Test the endpoint directly.
7. Update the Android model only when the JSON changed.
8. Run the end-to-end regression flow.

Do not refactor every API at once immediately before the customer demo.
