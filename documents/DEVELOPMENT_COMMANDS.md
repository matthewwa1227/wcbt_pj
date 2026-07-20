# Development Commands

## Prerequisites

- Java development kit compatible with the backend and Android build
- Docker Desktop
- Android Studio and Android SDK
- PowerShell on Windows
- Git

The repository includes Maven and Gradle wrappers. Prefer the wrappers over globally installed build tools.

## Start the database

From the backend directory:

```powershell
cd backend
docker compose up -d
docker compose ps
```

The PostgreSQL host port is defined in `backend/docker-compose.yml` and the backend properties. Use `docker compose ps` rather than relying on an old documented port.

View database-container logs:

```powershell
docker compose logs -f
```

Stop the database without removing stored data:

```powershell
docker compose stop
```

Stop and remove the containers:

```powershell
docker compose down
```

### Destructive database reset

This removes the PostgreSQL volume and causes development seed data to be recreated on the next backend start:

```powershell
docker compose down -v
docker compose up -d
```

Do not run this immediately before a demo unless a clean seed-data reset is intentional.

## Backend commands

From `backend/`:

```powershell
.\mvnw.cmd clean compile
```

Run backend tests:

```powershell
.\mvnw.cmd test
```

Start Spring Boot:

```powershell
.\mvnw.cmd spring-boot:run
```

The development backend is configured to run on:

```text
http://localhost:8081
```

Create a packaged backend build:

```powershell
.\mvnw.cmd clean package
```

## Android commands

From `CasualApp/`:

Check the Gradle wrapper:

```powershell
.\gradlew.bat --version
```

Current expected wrapper version:

```text
Gradle 9.4.1
```

Build the debug application:

```powershell
.\gradlew.bat assembleDebug
```

Clean and rebuild:

```powershell
.\gradlew.bat clean assembleDebug
```

Run Android unit tests:

```powershell
.\gradlew.bat test
```

Install the debug APK on a connected emulator or device:

```powershell
.\gradlew.bat installDebug
```

The debug APK is normally generated under:

```text
CasualApp/app/build/outputs/apk/debug/app-debug.apk
```

## Android-to-backend networking

### Emulator

Use this host address in Retrofit:

```text
http://10.0.2.2:8081/
```

`localhost` inside the emulator refers to the emulator itself, not the development computer.

### Physical device

The computer and phone must be on the same local network.

Find the computer address:

```powershell
ipconfig
```

Then use:

```text
http://<computer-local-ip>:8081/
```

The local IP may change between networks or router restarts.

## Endpoint smoke tests

List users:

```powershell
Invoke-RestMethod http://localhost:8081/api/users
```

List jobs:

```powershell
Invoke-RestMethod http://localhost:8081/api/jobs
```

Read a worker schedule:

```powershell
Invoke-RestMethod http://localhost:8081/api/schedules/worker/2
```

Do not assume worker ID `2` is permanent. Check the current user response after resetting the database.

## VS Code notes

VS Code may show false Android import errors because its Java/Gradle support does not fully model Android projects. Android Studio and the Gradle build are the source of truth.

Useful VS Code commands:

```text
Java: Clean Java Language Server Workspace
Developer: Reload Window
```

Suggested workspace Java settings:

```json
{
    "java.configuration.updateBuildConfiguration": "automatic",
    "java.errors.incompleteClasspath.severity": "ignore",
    "java.server.launchMode": "Standard",
    "java.import.gradle.enabled": true,
    "java.compile.nullAnalysis.mode": "disabled"
}
```

## Common troubleshooting

### Backend cannot connect to PostgreSQL

1. Run `docker compose ps`.
2. Check `docker compose logs`.
3. Compare the mapped port with `application.properties` or `application-local.properties`.
4. Confirm the expected Spring profile is active.

### Android cannot reach backend

1. Confirm Spring Boot is running on port `8081`.
2. Use `10.0.2.2` for an emulator.
3. Use the computer's LAN address for a physical device.
4. Check Android cleartext/network-security configuration.
5. Check Windows Firewall if physical-device access fails.

### Changes compile in Android Studio but appear red in VS Code

Run:

```powershell
.\gradlew.bat clean assembleDebug
```

If the build succeeds, treat VS Code highlighting as an editor-model issue.

### IDs change after database reset

Never hard-code signup, user or job IDs across the workflow. Save the IDs returned by the API response and pass those values into later approval and attendance requests.
