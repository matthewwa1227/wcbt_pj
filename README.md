# CasualApp

Full-stack casual worker coordination platform.
Backend: Spring Boot 4.1.0 + PostgreSQL (Docker)
Frontend: Android (Java + Retrofit)

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Spring Boot 4.1.0, Java 17, Spring Data JPA |
| Database | PostgreSQL 16 (Docker) |
| API | REST (JSON) |
| Android | Java, Retrofit 2.9.0, Gson |
| Build | Maven (backend), Gradle (Android) |

## Project Structure

```
wcbt_pj/
├── backend/                 # Spring Boot application
│   ├── src/main/java/...
│   │   ├── model/          # User, Job, JobSignup, Role, JobStatus, SignupStatus
│   │   ├── repository/       # JPA Repositories
│   │   ├── controller/       # REST Controllers
│   │   └── config/           # CORS configuration
│   └── docker-compose.yml    # Local PostgreSQL
└── android/                 # Android Studio project
    ├── app/src/main/java/...
    │   ├── model/            # Retrofit data models
    │   └── network/          # ApiService, RetrofitClient
    └── res/layout/           # XML layouts
```

## Backend Setup

```bash
cd backend
docker-compose up -d
./mvnw spring-boot:run
```

- Backend runs on `http://localhost:8081`
- PostgreSQL runs on `localhost:5433` (Docker, avoids Windows native Postgres conflict)

## Android Setup

1. Open `android/` folder in Android Studio
2. Sync Gradle
3. Run on emulator (uses `10.0.2.2` to reach backend)

## Verified Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | List all users |
| POST | `/api/users` | Create user |
| GET | `/api/jobs` | List all jobs |
| POST | `/api/jobs?coordinatorId={id}` | Create job |
| GET | `/api/signups` | List all signups |
| POST | `/api/signups?workerId={id}&jobId={id}` | Worker signs up |
| PUT | `/api/signups/{id}/attend` | Mark as attended |

## Current Status

- [x] Spring Boot backend with PostgreSQL
- [x] User, Job, JobSignup entities with JPA
- [x] REST API with CORS enabled
- [x] Android Retrofit client connected
- [x] End-to-end test: Android fetches users from backend

## Next Steps

- [ ] RecyclerView to list jobs
- [ ] Sign-up flow (worker applies to job)
- [ ] Coordinator dashboard (post jobs, view applicants)
- [ ] Authentication (phone + password)
- [ ] Deploy backend to cloud
