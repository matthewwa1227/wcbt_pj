# db.md — Core Database Design (v1)

## 1. Scope / Decisions Locked
- **One shift = one job** (no multiple roles per job; create separate jobs per role).
- **Pay**: fixed per job (still supports `pay_unit` like `per_shift` or `hourly` if needed).
- **Capacity**: `total_slots` is the number of slots for the job.
- **Reserved capacity**: `reserved_slots` exist and are **fillable only by coordinator manual add** (R1).
- **Signup behavior**:
  - Workers can create a signup only if job is not full by **approved normal** count.
  - When full, **signup is blocked** (no large pending list).
- **Upcoming jobs** for worker: **APPROVED only**.
- **Overlap conflicts (C2)**:
  - A worker cannot sign up if they already have an overlapping signup with state **PENDING or APPROVED**.
  - Conflict checks ignore **REJECTED/CANCELLED/REMOVED**.

---

## 2. Entity Overview
**users**  
Login identity; can be WORKER/COORDINATOR/ADMIN.

**worker_profile**  
Worker-only profile data (1:1 with users when role is worker).

**venues**  
Client/venue where job happens.

**roles**  
Controlled vocabulary of job roles (e.g., "Banquet Waiter").

**jobs**  
One row per shift; contains time window and work requirements (dress code, language requirement, offers, special requests, description).

**job_signups**  
Worker applications/assignments to jobs; also used for coordinator manual add into reserved quota.

**job_attendance**  
Post-job outcome/attendance per worker per job.

---

## 3. Tables (3NF Core)

### 3.1 users
**PK**: `user_id`  
**Unique**: `phone_number`

Columns:
- `user_id` (PK)
- `phone_number` (UNIQUE, NOT NULL)
- `password_hash` (NOT NULL)
- `role` (NOT NULL) — WORKER | COORDINATOR | ADMIN
- `status` (NOT NULL)
- `created_at` (NOT NULL)

---

### 3.2 worker_profile
**PK/FK**: `user_id` → `users.user_id`

Columns:
- `user_id` (PK, FK)
- `real_name`
- `hkid` (UNIQUE if required)
- `level_id` (optional FK if levels exist)
- `created_at`

---

### 3.3 venues
**PK**: `venue_id`  
**Unique**: `name`

Columns:
- `venue_id` (PK)
- `name` (UNIQUE, NOT NULL)
- `district_id` (FK, NOT NULL)
- `address_text` (NULL)

---

### 3.4 roles
**PK**: `role_id`  
**Unique**: `role_name`

Columns:
- `role_id` (PK)
- `role_name` (UNIQUE, NOT NULL)

---

### 3.5 jobs
**PK**: `job_id`  
**FKs**: `venue_id` → venues, `role_id` → roles, `posted_by_user_id` → users

Canonical time columns:
- `start_at` (NOT NULL)
- `end_at` (NOT NULL)

Columns:
- `job_id` (PK)
- `venue_id` (FK, NOT NULL)
- `role_id` (FK, NOT NULL)
- `posted_by_user_id` (FK, NOT NULL)

Time/rules:
- `start_at` (NOT NULL)
- `end_at` (NOT NULL)
- `cutoff_at` (NOT NULL)
- `lock_at` (NOT NULL)
- `status` (NOT NULL) — draft/published/locked/completed/cancelled

Capacity:
- `total_slots` (NOT NULL)
- `reserved_slots` (NOT NULL)

Pay:
- `pay_amount` (NOT NULL)
- `pay_unit` (NOT NULL) — per_shift/hourly
- `payment_method` (NOT NULL) — FPS/Transfer/Cash

Work info:
- `job_type` (NOT NULL or NULL depending on your UI)
- `job_description` (TEXT)
- `dress_code_text` (TEXT)
- `special_requests_text` (TEXT)
- `offers_text` (TEXT)
- `language_requirements_text` (TEXT)

Audit:
- `created_at` (NOT NULL)
- `published_at` (NULL)

**Constraints (recommended)**
- `start_at < end_at`
- `total_slots >= 1`
- `reserved_slots >= 0`
- `reserved_slots <= total_slots`
- `cutoff_at <= start_at`
- `lock_at <= start_at`
- (optional) `cutoff_at <= lock_at`

---

### 3.6 job_signups
**PK**: `signup_id`  
**FKs**: `job_id` → jobs, `worker_user_id` → users, `actioned_by_user_id` → users  
**Unique**: `(job_id, worker_user_id)`

Columns:
- `signup_id` (PK)
- `job_id` (FK, NOT NULL)
- `worker_user_id` (FK, NOT NULL)
- `state` (NOT NULL) — PENDING/APPROVED/REJECTED/CANCELLED/REMOVED
- `fill_type` (NOT NULL) — NORMAL/RESERVED
- `created_at` (NOT NULL)
- `updated_at` (NOT NULL)
- `actioned_by_user_id` (FK, NULL)
- `action_reason` (NULL)

**Key Rule**
- Worker self-signup: create `state=PENDING`, `fill_type=NORMAL`.
- Coordinator reserved manual add: create `state=APPROVED`, `fill_type=RESERVED` (counts toward reserved quota).

---

### 3.7 job_attendance
**PK**: `attendance_id`  
**FKs**: `job_id` → jobs, `worker_user_id` → users, `recorded_by_user_id` → users  
**Unique**: `(job_id, worker_user_id)`

Columns:
- `attendance_id` (PK)
- `job_id` (FK, NOT NULL)
- `worker_user_id` (FK, NOT NULL)
- `status` (NOT NULL) — COMPLETED/LATE/NOSHOW/etc.
- `late_minutes` (NULL)
- `notes` (NULL)
- `recorded_by_user_id` (FK, NOT NULL)
- `recorded_at` (NOT NULL)

---

## 4. Core Business Rules (Implementation Notes)

### 4.1 Capacity definitions
- `normal_capacity = total_slots - reserved_slots`

Counts:
- `approved_normal_count` = count of `job_signups` where:
  - `job_id = ?`
  - `state = 'APPROVED'`
  - `fill_type = 'NORMAL'`

- `approved_reserved_count` = count of `job_signups` where:
  - `job_id = ?`
  - `state = 'APPROVED'`
  - `fill_type = 'RESERVED'`

### 4.2 Worker signup allowed only if
- Not past `cutoff_at`, not locked, job is publishable/open
- No overlap conflicts with worker’s existing signups where `state IN ('PENDING','APPROVED')`
- `approved_normal_count < normal_capacity`
- Also enforced by `UNIQUE(job_id, worker_user_id)` to prevent duplicates

### 4.3 Coordinator approve (hard block)
- Must re-check conflicts: block if worker has another **APPROVED** overlap
- Must re-check capacity: block if `approved_normal_count >= normal_capacity`

### 4.4 Reserved manual add (R1)
- Allowed only if `approved_reserved_count < reserved_slots`
- Recommended: also enforce overlap check (PENDING/APPROVED) to avoid double-booking

### 4.5 Overlap definition (strict)
Jobs overlap if:
- `existing.start_at < new.end_at` AND `existing.end_at > new.start_at`

Conflict states:
- Only `PENDING` and `APPROVED` block conflicts.
- `REJECTED/CANCELLED/REMOVED` do not block.

### 4.6 Upcoming jobs
- Worker upcoming list includes only signups with `state='APPROVED'`.

---

## 5. Indexes (Recommended)
- `job_signups`: `UNIQUE(job_id, worker_user_id)`
- `job_signups`: index `(worker_user_id, state, job_id)` for conflict checks
- `job_signups`: index `(job_id, state, fill_type)` for capacity counting
- `jobs`: index `(start_at, end_at)` (optional; depends on DB planner)# db.md — Core Database Design (v1)

## 1. Scope / Decisions Locked
- **One shift = one job** (no multiple roles per job; create separate jobs per role).
- **Pay**: fixed per job (still supports `pay_unit` like `per_shift` or `hourly` if needed).
- **Capacity**: `total_slots` is the number of slots for the job.
- **Reserved capacity**: `reserved_slots` exist and are **fillable only by coordinator manual add** (R1).
- **Signup behavior**:
  - Workers can create a signup only if job is not full by **approved normal** count.
  - When full, **signup is blocked** (no large pending list).
- **Upcoming jobs** for worker: **APPROVED only**.
- **Overlap conflicts (C2)**:
  - A worker cannot sign up if they already have an overlapping signup with state **PENDING or APPROVED**.
  - Conflict checks ignore **REJECTED/CANCELLED/REMOVED**.
- **No venue masking in v1** (no `masked_name`).

---

## 2. Entity Overview
**users**  
Login identity; can be WORKER/COORDINATOR/ADMIN.

**worker_profile**  
Worker-only profile data (1:1 with users when role is worker).

**venues**  
Client/venue where job happens.

**roles**  
Controlled vocabulary of job roles (e.g., "Banquet Waiter").

**jobs**  
One row per shift; contains time window and work requirements (dress code, language requirement, offers, special requests, description).

**job_signups**  
Worker applications/assignments to jobs; also used for coordinator manual add into reserved quota.

**job_attendance**  
Post-job outcome/attendance per worker per job.

---

## 3. Tables (3NF Core)

### 3.1 users
**PK**: `user_id`  
**Unique**: `phone_number`

Columns:
- `user_id` (PK)
- `phone_number` (UNIQUE, NOT NULL)
- `password_hash` (NOT NULL)
- `role` (NOT NULL) — WORKER | COORDINATOR | ADMIN
- `status` (NOT NULL)
- `created_at` (NOT NULL)

---

### 3.2 worker_profile
**PK/FK**: `user_id` → `users.user_id`

Columns:
- `user_id` (PK, FK)
- `real_name`
- `hkid` (UNIQUE if required)
- `level_id` (optional FK if levels exist)
- `created_at`

---

### 3.3 venues
**PK**: `venue_id`  
**Unique**: `name`

Columns:
- `venue_id` (PK)
- `name` (UNIQUE, NOT NULL)
- `district_id` (FK, NOT NULL)
- `address_text` (NULL)

---

### 3.4 roles
**PK**: `role_id`  
**Unique**: `role_name`

Columns:
- `role_id` (PK)
- `role_name` (UNIQUE, NOT NULL)

---

### 3.5 jobs
**PK**: `job_id`  
**FKs**: `venue_id` → venues, `role_id` → roles, `posted_by_user_id` → users

Canonical time columns:
- `start_at` (NOT NULL)
- `end_at` (NOT NULL)

Columns:
- `job_id` (PK)
- `venue_id` (FK, NOT NULL)
- `role_id` (FK, NOT NULL)
- `posted_by_user_id` (FK, NOT NULL)

Time/rules:
- `start_at` (NOT NULL)
- `end_at` (NOT NULL)
- `cutoff_at` (NOT NULL)
- `lock_at` (NOT NULL)
- `status` (NOT NULL) — draft/published/locked/completed/cancelled

Capacity:
- `total_slots` (NOT NULL)
- `reserved_slots` (NOT NULL)

Pay:
- `pay_amount` (NOT NULL)
- `pay_unit` (NOT NULL) — per_shift/hourly
- `payment_method` (NOT NULL) — FPS/Transfer/Cash

Work info:
- `job_type` (NOT NULL or NULL depending on your UI)
- `job_description` (TEXT)
- `dress_code_text` (TEXT)
- `special_requests_text` (TEXT)
- `offers_text` (TEXT)
- `language_requirements_text` (TEXT)

Audit:
- `created_at` (NOT NULL)
- `published_at` (NULL)

**Constraints (recommended)**
- `start_at < end_at`
- `total_slots >= 1`
- `reserved_slots >= 0`
- `reserved_slots <= total_slots`
- `cutoff_at <= start_at`
- `lock_at <= start_at`
- (optional) `cutoff_at <= lock_at`

---

### 3.6 job_signups
**PK**: `signup_id`  
**FKs**: `job_id` → jobs, `worker_user_id` → users, `actioned_by_user_id` → users  
**Unique**: `(job_id, worker_user_id)`

Columns:
- `signup_id` (PK)
- `job_id` (FK, NOT NULL)
- `worker_user_id` (FK, NOT NULL)
- `state` (NOT NULL) — PENDING/APPROVED/REJECTED/CANCELLED/REMOVED
- `fill_type` (NOT NULL) — NORMAL/RESERVED
- `created_at` (NOT NULL)
- `updated_at` (NOT NULL)
- `actioned_by_user_id` (FK, NULL)
- `action_reason` (NULL)

**Key Rule**
- Worker self-signup: create `state=PENDING`, `fill_type=NORMAL`.
- Coordinator reserved manual add: create `state=APPROVED`, `fill_type=RESERVED` (counts toward reserved quota).

---

### 3.7 job_attendance
**PK**: `attendance_id`  
**FKs**: `job_id` → jobs, `worker_user_id` → users, `recorded_by_user_id` → users  
**Unique**: `(job_id, worker_user_id)`

Columns:
- `attendance_id` (PK)
- `job_id` (FK, NOT NULL)
- `worker_user_id` (FK, NOT NULL)
- `status` (NOT NULL) — COMPLETED/LATE/NOSHOW/etc.
- `late_minutes` (NULL)
- `notes` (NULL)
- `recorded_by_user_id` (FK, NOT NULL)
- `recorded_at` (NOT NULL)

---

### 3.8 event_log (L1 — Application-written domain event log)
**Purpose**: Append-only, human-meaningful history of *what happened* and *why*, written by the application (not triggers).

**Write rule (L1)**
- Insert **exactly one meaningful event per business action**
- Insert the event **inside the same DB transaction** as the business change (so the change and its explanation are atomic)
- Events are **immutable** (no updates; only inserts)

**PK**: `event_id`

Columns (recommended):
- `event_id` (PK)
- `event_type` (NOT NULL) — e.g. WORKER_SIGNUP_CREATED, SIGNUP_APPROVED, SIGNUP_CANCELLED_BY_WORKER
- `actor_user_id` (FK → `users.user_id`, NULL allowed for system actions)
- `target_table` (NOT NULL) — e.g. `job_signups`
- `target_id` (NOT NULL) — row id in target table (e.g. `signup_id`)
- `job_id` (FK → `jobs.job_id`, NULL allowed if not job-related)
- `worker_user_id` (FK → `users.user_id`, NULL allowed if not worker-related)
- `message` (TEXT, NOT NULL) — short human-readable summary
- `metadata_json` (JSON/JSONB, NOT NULL, default `{}`) — structured “why/context”
- `created_at` (NOT NULL)

**Indexes (recommended)**
- `(target_table, target_id, created_at)`
- `(job_id, created_at)`
- `(worker_user_id, created_at)`
- `(event_type, created_at)`

---

## 4. Core Business Rules (Implementation Notes)

### 4.1 Capacity definitions
- `normal_capacity = total_slots - reserved_slots`

Counts:
- `approved_normal_count` = count of `job_signups` where:
  - `job_id = ?`
  - `state = 'APPROVED'`
  - `fill_type = 'NORMAL'`

- `approved_reserved_count` = count of `job_signups` where:
  - `job_id = ?`
  - `state = 'APPROVED'`
  - `fill_type = 'RESERVED'`

### 4.2 Worker signup allowed only if
- Not past `cutoff_at`, not locked, job is publishable/open
- No overlap conflicts with worker’s existing signups where `state IN ('PENDING','APPROVED')`
- `approved_normal_count < normal_capacity`
- Also enforced by `UNIQUE(job_id, worker_user_id)` to prevent duplicates

### 4.3 Coordinator approve (hard block)
- Must re-check conflicts: block if worker has another **APPROVED** overlap
- Must re-check capacity: block if `approved_normal_count >= normal_capacity`

### 4.4 Reserved manual add (R1)
- Allowed only if `approved_reserved_count < reserved_slots`
- Recommended: also enforce overlap check (PENDING/APPROVED) to avoid double-booking

### 4.5 Overlap definition (strict)
Jobs overlap if:
- `existing.start_at < new.end_at` AND `existing.end_at > new.start_at`

Conflict states:
- Only `PENDING` and `APPROVED` block conflicts.
- `REJECTED/CANCELLED/REMOVED` do not block.

### 4.6 Upcoming jobs
- Worker upcoming list includes only signups with `state='APPROVED'`.

---

## 5. Indexes (Recommended)
- `job_signups`: `UNIQUE(job_id, worker_user_id)`
- `job_signups`: index `(worker_user_id, state, job_id)` for conflict checks
- `job_signups`: index `(job_id, state, fill_type)` for capacity counting
- `jobs`: index `(start_at, end_at)` (optional; depends on DB planner)