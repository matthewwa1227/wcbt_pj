# DTO Refactoring

DTO means **Data Transfer Object**. DTOs define the data accepted or returned by an API operation and keep the REST contract separate from JPA persistence entities.

## Current state

The DTO refactor for the currently implemented API endpoints is complete on the backend.

Current backend structure:

```text
dto/
├── auth/
│   ├── LoginRequest.java
│   └── LoginResponse.java
├── attendance/
│   ├── AttendanceRequest.java
│   └── AttendanceResponse.java
├── job/
│   ├── CreateJobRequest.java
│   └── JobResponse.java
├── schedule/
│   ├── WorkerScheduleItemResponse.java
│   └── WorkerScheduleResponse.java
├── signup/
│   ├── ApproveSignupRequest.java
│   ├── RejectSignupRequest.java
│   ├── SignupActionResponse.java
│   ├── SignupRequest.java
│   └── SignupResponse.java
└── user/
    └── UserSummaryResponse.java
```

The REST controllers no longer return JPA entities for the implemented user, job, signup, attendance, authentication and schedule flows.

The Android client mirrors the API contract with request/response models. Signup mutations use `SignupActionResponse`, while signup GET endpoints continue to use the larger `SignupResponse` required by application screens.

## Signup response split

Read operations and mutation operations intentionally use different response shapes.

### SignupResponse

Used by signup GET endpoints. It contains the information required to render applications, including worker, job, status and action details.

### SignupActionResponse

Used by:

```text
POST /api/signups
PUT /api/signups/{signupId}/approve
PUT /api/signups/{signupId}/reject
```

It contains only:

```text
id
status
updatedAt
actionReason
```

This keeps action responses focused and prevents mutation endpoints from returning data that the caller does not need.

## Request DTOs

Write operations use request DTOs rather than accepting persistence entities or large collections of query parameters.

Examples:

```text
CreateJobRequest
SignupRequest
ApproveSignupRequest
RejectSignupRequest
AttendanceRequest
LoginRequest
```

## Mapping

Entity-to-DTO conversion is currently handled with focused mapping code, including:

```text
JobMapper
SignupMapper
```

Manual mapping is preferred while the number of mappings is small. A mapping framework should only be introduced if the mapping layer becomes repetitive enough to justify the dependency.

## DTO design rules

A DTO should normally contain:

- fields required by the API contract
- constructors
- getters and setters
- simple serialization annotations when necessary

A DTO should not contain:

- JPA annotations
- repository references
- service calls
- database save methods
- lazy-loaded entity collections
- authoritative business calculations

## Compatibility workflow

When changing an API contract:

1. Write down the current JSON shape.
2. Add or change the backend DTO.
3. Update mapping and controller/service return types.
4. Compile and test the backend endpoint.
5. Update the corresponding Android request/response model.
6. Update Retrofit method return types.
7. Run the affected Android flow end to end.
8. Update `API_REFERENCE.md`.

## Future DTO work

New DTOs should be added when the corresponding features are implemented, rather than creating unused classes in advance. Likely future areas include:

```text
registration
job updates
performance summaries
earnings summaries
pagination
```

`ApiErrorResponse` currently remains under the controller package. Moving it to a `dto/common` package would be an organizational cleanup only; the API already returns a structured error object rather than a persistence entity.
