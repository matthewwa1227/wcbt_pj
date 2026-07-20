# CasualApp

CasualApp is a full-stack casual-worker coordination platform for hotels and other workplaces that rely on temporary staff.

The product is intended to connect the complete operational workflow:

> A coordinator publishes a shift, a worker applies, the coordinator approves the application, attendance is recorded, and the resulting work history can be turned into schedule and performance information.

The current client applications are:

- **Backend:** Spring Boot, PostgreSQL, Spring Data JPA and Docker
- **Mobile client:** Native Android using Java, XML, Retrofit and Gson

## Current Progress

The core development flow is functioning in the local development environment.

### Worker flow

- Phone-number prototype login
- Region-selection screen
- Backend-connected job list
- Job-detail and shift-selection screens
- Application confirmation and submission
- Application-success screen
- My Jobs landing screen
- Worker application list
- My Schedule screen with a monthly calendar
- Upcoming and completed shift sections
- Bottom-navigation handling on the main worker screens
- Meaningful placeholder messages for unfinished sections

### Coordinator flow

- Coordinator prototype login
- Coordinator home screen
- Job creation
- Posted-job list
- Applicant list
- Approve and reject actions
- Attendance recording for completed, late and no-show cases
- Coordinator ownership and role checks in the backend

### Backend

- PostgreSQL database through Docker
- User, role, job, signup and attendance models
- Additional EventLog, Venue, WorkerProfile and JobRole models
- JPA repositories
- REST controllers for authentication, users, jobs, signups and worker schedules
- Structured API-error handling
- Duplicate-signup conflict handling
- Development seed data
- Worker Schedule service and response DTOs
- Persistence ports, filters and factory scaffolding

### Current end-to-end demo flow

```text
Coordinator creates a job
→ Worker views and applies for the job
→ Coordinator views the applicant
→ Coordinator approves or rejects the application
→ Coordinator records attendance
→ Worker views the updated application and schedule information
```

### Known prototype limitations

- Login currently uses a phone number without a password.
- Public self-registration is not implemented yet.
- The current job model does not fully separate shift start and end time.
- Performance dashboards and Excel exports are the next major demo feature.
- Some secondary UI tabs intentionally show a development placeholder.
- `MainActivity` remains a legacy backend smoke-test screen and should be hidden from the final demo path.

## Current Test Users

The development seed data currently includes:

| User | Role | Purpose |
|---|---|---|
| Boss Chan | Coordinator | Create jobs, manage applicants and record attendance |
| Worker A | Worker | Primary worker-flow testing |
| Worker B | Worker | Secondary worker-flow testing |
| Admin Test | Admin | Administrative-role testing |

The exact phone numbers and database IDs are defined by `backend/src/main/java/com/casualapp/backend/config/DevDataSeeder.java`. Do not document hard-coded IDs because they can change after a database reset.

## Simplified Project Structure

Generated folders, IDE caches, build outputs and dependency metadata are intentionally omitted.

```text
wcbt_pj/
├── README.md
├── structure.txt
├── documents/
│   ├── README.md
│   ├── DEVELOPMENT_COMMANDS.md
│   ├── API_REFERENCE.md
│   ├── ARCHITECTURE.md
│   ├── DEMO_AND_TESTING.md
│   ├── ROADMAP.md
│   ├── DTO_REFACTORING.md
│   ├── DATA_MODEL_AND_SEEDING.md
│   └── UI_SCREEN_MAP.md
│
├── backend/
│   ├── docker-compose.yml
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   └── src/
│       ├── main/java/com/casualapp/backend/
│       │   ├── config/
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── model/
│       │   ├── persistence/
│       │   ├── repository/
│       │   ├── service/
│       │   └── BackendApplication.java
│       ├── main/resources/
│       └── test/
│
└── CasualApp/
    ├── build.gradle
    ├── settings.gradle
    ├── gradlew
    ├── gradlew.bat
    └── app/src/main/
        ├── AndroidManifest.xml
        ├── java/com/casualapp/android/
        │   ├── activities and adapters
        │   ├── model/
        │   └── network/
        └── res/
            ├── layout/
            ├── drawable/
            ├── mipmap-*/
            ├── values/
            └── xml/
```

## Project Documentation

Detailed documentation is kept in [`documents/`](documents/README.md):

- [Development commands](documents/DEVELOPMENT_COMMANDS.md)
- [API reference](documents/API_REFERENCE.md)
- [Architecture](documents/ARCHITECTURE.md)
- [Demo and testing guide](documents/DEMO_AND_TESTING.md)
- [Roadmap](documents/ROADMAP.md)
- [DTO refactoring plan](documents/DTO_REFACTORING.md)
- [Data model and seed-data notes](documents/DATA_MODEL_AND_SEEDING.md)
- [Android UI screen map](documents/UI_SCREEN_MAP.md)

`structure.txt` is retained only as a raw reference snapshot and is not duplicated inside the documentation.
