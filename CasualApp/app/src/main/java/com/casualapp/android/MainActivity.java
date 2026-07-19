package com.casualapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.casualapp.android.model.Job;
import com.casualapp.android.model.JobAttendance;
import com.casualapp.android.model.JobSignup;
import com.casualapp.android.model.User;
import com.casualapp.android.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView tvResult;

    private Long selectedCoordinatorId;
    private Long selectedWorkerId;
    private Long selectedJobId;
    private Long lastSignupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
    }

    private void bindViews() {
        tvResult = findViewById(R.id.tvResult);

        Button btnLoadUsers = findViewById(R.id.btnLoadUsers);
        Button btnLoadJobs = findViewById(R.id.btnLoadJobs);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        Button btnApprove = findViewById(R.id.btnApprove);
        Button btnAttend = findViewById(R.id.btnAttend);
        Button btnGoLogin = findViewById(R.id.btnGoLogin);
        Button btnGoJobList = findViewById(R.id.btnGoJobList);

        tvResult.setText("Press buttons in order 1 → 5");

        btnLoadUsers.setOnClickListener(v -> loadUsers());
        btnLoadJobs.setOnClickListener(v -> loadJobs());
        btnSignUp.setOnClickListener(v -> signUp());
        btnApprove.setOnClickListener(v -> approveSignup());
        btnAttend.setOnClickListener(v -> markAttend());

        btnGoLogin.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
        });

        btnGoJobList.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    JobListActivity.class
            );

            startActivity(intent);
        });
    }

    private void showLoading() {
        showResult("Loading...");
    }

    private void showResult(String text) {
        runOnUiThread(() -> {
            if (tvResult == null) {
                return;
            }

            if (text == null || text.trim().isEmpty()) {
                tvResult.setText("No result");
            } else {
                tvResult.setText(text);
            }
        });
    }

    // 1. Load users and select the first worker and coordinator.
    private void loadUsers() {
        showLoading();

        RetrofitClient.getApiService()
                .getAllUsers()
                .enqueue(new Callback<List<User>>() {

                    @Override
                    public void onResponse(
                            Call<List<User>> call,
                            Response<List<User>> response
                    ) {
                        if (!response.isSuccessful()) {
                            showErrorResponse(response);
                            return;
                        }

                        List<User> users = response.body();

                        if (users == null) {
                            showResult(
                                    "The server returned an empty user response."
                            );
                            return;
                        }

                        selectedWorkerId = null;
                        selectedCoordinatorId = null;

                        StringBuilder result =
                                new StringBuilder("=== USERS ===\n");

                        for (User user : users) {
                            if (user == null) {
                                continue;
                            }

                            result.append("ID: ")
                                    .append(user.getId())
                                    .append(" | ")
                                    .append(
                                            safeText(
                                                    user.getName(),
                                                    "Unknown user"
                                            )
                                    )
                                    .append(" | ")
                                    .append(
                                            user.getRole() != null
                                                    ? user.getRole()
                                                    : "NO ROLE"
                                    );

                            if (user.isCoordinator()) {
                                result.append(" [BOSS]");

                                if (selectedCoordinatorId == null) {
                                    selectedCoordinatorId =
                                            user.getId();
                                }
                            }

                            if (user.isWorker()) {
                                result.append(" [WORKER]");

                                if (selectedWorkerId == null) {
                                    selectedWorkerId =
                                            user.getId();
                                }
                            }

                            result.append("\n");
                        }

                        result.append("\nSelected workerId: ")
                                .append(selectedWorkerId);

                        result.append("\nSelected coordinatorId: ")
                                .append(selectedCoordinatorId);

                        if (selectedWorkerId == null) {
                            result.append(
                                    "\nWarning: no worker was found."
                            );
                        }

                        if (selectedCoordinatorId == null) {
                            result.append(
                                    "\nWarning: no coordinator was found."
                            );
                        }

                        showResult(result.toString());
                    }

                    @Override
                    public void onFailure(
                            Call<List<User>> call,
                            Throwable throwable
                    ) {
                        showResult(
                                "Load users failed: "
                                        + getFailureMessage(throwable)
                        );
                    }
                });
    }

    // 2. Load jobs and select the first open job with space.
    private void loadJobs() {
        showLoading();

        RetrofitClient.getApiService()
                .getAllJobs()
                .enqueue(new Callback<List<Job>>() {

                    @Override
                    public void onResponse(
                            Call<List<Job>> call,
                            Response<List<Job>> response
                    ) {
                        if (!response.isSuccessful()) {
                            showErrorResponse(response);
                            return;
                        }

                        List<Job> jobs = response.body();

                        if (jobs == null) {
                            showResult(
                                    "The server returned an empty job response."
                            );
                            return;
                        }

                        selectedJobId = null;

                        StringBuilder result =
                                new StringBuilder("=== JOBS ===\n");

                        for (Job job : jobs) {
                            if (job == null) {
                                continue;
                            }

                            boolean usableJob =
                                    job.isOpen()
                                            && job.hasAvailableSlots();

                            if (selectedJobId == null && usableJob) {
                                selectedJobId = job.getId();
                            }

                            result.append("ID: ")
                                    .append(job.getId())
                                    .append(" | ")
                                    .append(
                                            safeText(
                                                    job.getTitle(),
                                                    "Untitled job"
                                            )
                                    )
                                    .append(" | ")
                                    .append(
                                            safeText(
                                                    job.getLocation(),
                                                    "No location"
                                            )
                                    )
                                    .append(" | Slots: ")
                                    .append(job.getFilledSlots())
                                    .append("/")
                                    .append(job.getTotalSlots());

                            if (job.isOpen()) {
                                result.append(" [OPEN]");
                            }

                            if (job.isFull()) {
                                result.append(" [FULL]");
                            }

                            if (job.hasAvailableSlots()) {
                                result.append(" [AVAILABLE]");
                            }

                            result.append("\n");
                        }

                        result.append("\nSelected jobId: ")
                                .append(selectedJobId);

                        if (selectedJobId == null) {
                            result.append(
                                    "\nNo open job with available slots was found."
                            );
                        }

                        showResult(result.toString());
                    }

                    @Override
                    public void onFailure(
                            Call<List<Job>> call,
                            Throwable throwable
                    ) {
                        showResult(
                                "Load jobs failed: "
                                        + getFailureMessage(throwable)
                        );
                    }
                });
    }

    // 3. Create one PENDING signup.
    private void signUp() {
        if (selectedWorkerId == null) {
            showResult(
                    "No worker selected. Press Load Users first."
            );
            return;
        }

        if (selectedJobId == null) {
            showResult(
                    "No job selected. Press Load Jobs first."
            );
            return;
        }

        showLoading();

        RetrofitClient.getApiService()
                .signUp(
                        selectedWorkerId,
                        selectedJobId
                )
                .enqueue(new Callback<JobSignup>() {

                    @Override
                    public void onResponse(
                            Call<JobSignup> call,
                            Response<JobSignup> response
                    ) {
                        if (!response.isSuccessful()) {
                            showErrorResponse(response);
                            return;
                        }

                        JobSignup signup = response.body();

                        if (signup == null) {
                            showResult(
                                    "Signup request succeeded, "
                                            + "but the response body was empty."
                            );
                            return;
                        }

                        lastSignupId = signup.getId();

                        StringBuilder result =
                                new StringBuilder(
                                        "=== SIGNUP CREATED ===\n"
                                );

                        result.append("Signup ID: ")
                                .append(signup.getId());

                        result.append("\nStatus: ")
                                .append(
                                        signup.getStatus() != null
                                                ? signup.getStatus()
                                                : "UNKNOWN"
                                );

                        if (signup.isPending()) {
                            result.append(" [PENDING]");
                        }

                        if (signup.isApproved()) {
                            result.append(" [APPROVED]");
                        }

                        if (signup.getWorker() != null) {
                            result.append("\nWorker: ")
                                    .append(
                                            safeText(
                                                    signup
                                                            .getWorker()
                                                            .getName(),
                                                    "Unknown worker"
                                            )
                                    );
                        }

                        if (signup.getJob() != null) {
                            result.append("\nJob: ")
                                    .append(
                                            safeText(
                                                    signup
                                                            .getJob()
                                                            .getTitle(),
                                                    "Unknown job"
                                            )
                                    );
                        }

                        result.append("\n\nSaved lastSignupId: ")
                                .append(lastSignupId);

                        showResult(result.toString());
                    }

                    @Override
                    public void onFailure(
                            Call<JobSignup> call,
                            Throwable throwable
                    ) {
                        showResult(
                                "Signup request failed: "
                                        + getFailureMessage(throwable)
                        );
                    }
                });
    }

    // 4. Approve the most recently created signup.
    private void approveSignup() {
        if (lastSignupId == null) {
            showResult(
                    "No signup selected. Create a signup first."
            );
            return;
        }

        if (selectedCoordinatorId == null) {
            showResult(
                    "No coordinator selected. Press Load Users first."
            );
            return;
        }

        showLoading();

        RetrofitClient.getApiService()
                .approveSignup(
                        lastSignupId,
                        selectedCoordinatorId,
                        "Approved from Android test screen"
                )
                .enqueue(new Callback<JobSignup>() {

                    @Override
                    public void onResponse(
                            Call<JobSignup> call,
                            Response<JobSignup> response
                    ) {
                        if (!response.isSuccessful()) {
                            showErrorResponse(response);
                            return;
                        }

                        JobSignup signup = response.body();

                        if (signup == null) {
                            showResult(
                                    "Approval succeeded, "
                                            + "but the response body was empty."
                            );
                            return;
                        }

                        StringBuilder result =
                                new StringBuilder(
                                        "=== SIGNUP APPROVED ===\n"
                                );

                        result.append("Signup ID: ")
                                .append(signup.getId());

                        result.append("\nStatus: ")
                                .append(
                                        signup.getStatus() != null
                                                ? signup.getStatus()
                                                : "UNKNOWN"
                                );

                        if (signup.isApproved()) {
                            result.append(" [APPROVED]");
                        }

                        if (signup.getActionedBy() != null) {
                            result.append("\nApproved by: ")
                                    .append(
                                            safeText(
                                                    signup
                                                            .getActionedBy()
                                                            .getName(),
                                                    "Coordinator"
                                            )
                                    );
                        }

                        if (signup.getActionReason() != null
                                && !signup
                                        .getActionReason()
                                        .trim()
                                        .isEmpty()) {

                            result.append("\nReason: ")
                                    .append(
                                            signup.getActionReason()
                                    );
                        }

                        if (signup.getUpdatedAt() != null) {
                            result.append("\nUpdated at: ")
                                    .append(signup.getUpdatedAt());
                        }

                        showResult(result.toString());
                    }

                    @Override
                    public void onFailure(
                            Call<JobSignup> call,
                            Throwable throwable
                    ) {
                        showResult(
                                "Approval request failed: "
                                        + getFailureMessage(throwable)
                        );
                    }
                });
    }

    // 5. Record COMPLETED attendance for the approved signup.
    private void markAttend() {
        if (lastSignupId == null) {
            showResult(
                    "No signup selected. Create and approve a signup first."
            );
            return;
        }

        if (selectedCoordinatorId == null) {
            showResult(
                    "No coordinator selected. Press Load Users first."
            );
            return;
        }

        showLoading();

        RetrofitClient.getApiService()
                .markAttendance(
                        lastSignupId,
                        selectedCoordinatorId,
                        "COMPLETED",
                        0,
                        "Recorded from Android test screen"
                )
                .enqueue(new Callback<JobAttendance>() {

                    @Override
                    public void onResponse(
                            Call<JobAttendance> call,
                            Response<JobAttendance> response
                    ) {
                        if (!response.isSuccessful()) {
                            showErrorResponse(response);
                            return;
                        }

                        JobAttendance attendance =
                                response.body();

                        if (attendance == null) {
                            showResult(
                                    "Attendance request succeeded, "
                                            + "but the response body was empty."
                            );
                            return;
                        }

                        StringBuilder result =
                                new StringBuilder(
                                        "=== ATTENDANCE RECORDED ===\n"
                                );

                        result.append("Attendance ID: ")
                                .append(attendance.getId());

                        result.append("\nStatus: ")
                                .append(
                                        attendance.getStatus() != null
                                                ? attendance.getStatus()
                                                : "UNKNOWN"
                                );

                        if (attendance.isCompleted()) {
                            result.append(" [COMPLETED]");
                        }

                        if (attendance.isLate()) {
                            result.append(" [LATE]");
                        }

                        if (attendance.isNoShow()) {
                            result.append(" [NO_SHOW]");
                        }

                        if (attendance.hasLateMinutes()) {
                            result.append(" (Late: ")
                                    .append(
                                            attendance.getLateMinutes()
                                    )
                                    .append(" min)");
                        }

                        if (attendance.getRecordedBy() != null) {
                            result.append("\nRecorded by: ")
                                    .append(
                                            safeText(
                                                    attendance
                                                            .getRecordedBy()
                                                            .getName(),
                                                    "Coordinator"
                                            )
                                    );
                        }

                        if (attendance.getWorker() != null) {
                            result.append("\nWorker: ")
                                    .append(
                                            safeText(
                                                    attendance
                                                            .getWorker()
                                                            .getName(),
                                                    "Unknown worker"
                                            )
                                    );
                        }

                        if (attendance.getJob() != null) {
                            result.append("\nJob: ")
                                    .append(
                                            safeText(
                                                    attendance
                                                            .getJob()
                                                            .getTitle(),
                                                    "Unknown job"
                                            )
                                    );
                        }

                        if (attendance.getRecordedAt() != null) {
                            result.append("\nRecorded at: ")
                                    .append(
                                            attendance.getRecordedAt()
                                    );
                        }

                        showResult(result.toString());
                    }

                    @Override
                    public void onFailure(
                            Call<JobAttendance> call,
                            Throwable throwable
                    ) {
                        showResult(
                                "Attendance request failed: "
                                        + getFailureMessage(throwable)
                        );
                    }
                });
    }

    private void showErrorResponse(Response<?> response) {
        try {
            String errorBody = response.errorBody() != null
                    ? response.errorBody().string()
                    : "Unknown server error";

            showResult(
                    "Error "
                            + response.code()
                            + ":\n"
                            + errorBody
            );

        } catch (Exception exception) {
            showResult(
                    "Error "
                            + response.code()
                            + ": unable to read the error response."
            );
        }
    }

    private String getFailureMessage(Throwable throwable) {
        if (throwable == null) {
            return "Unknown network failure";
        }

        if (throwable.getMessage() == null
                || throwable.getMessage().trim().isEmpty()) {

            return throwable.getClass().getSimpleName();
        }

        return throwable.getMessage();
    }

    private String safeText(
            String value,
            String fallback
    ) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        return value;
    }
}