package com.casualapp.android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.casualapp.android.model.Job;
import com.casualapp.android.model.JobAttendance;
import com.casualapp.android.model.JobSignup;
import com.casualapp.android.model.User;
import com.casualapp.android.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    private TextView tvResult;
    private Long selectedCoordinatorId = null;
    private Long selectedWorkerId = null;
    private Long selectedJobId = null;
    private Long lastSignupId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);

        Button btnLoadUsers = findViewById(R.id.btnLoadUsers);
        Button btnLoadJobs = findViewById(R.id.btnLoadJobs);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        Button btnApprove = findViewById(R.id.btnApprove);
        Button btnAttend = findViewById(R.id.btnAttend);

        btnLoadUsers.setOnClickListener(v -> loadUsers());
        btnLoadJobs.setOnClickListener(v -> loadJobs());
        btnSignUp.setOnClickListener(v -> signUp());
        btnApprove.setOnClickListener(v -> approveSignup());
        btnAttend.setOnClickListener(v -> markAttend());

        Button btnGoLogin = findViewById(R.id.btnGoLogin);
        btnGoLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        Button btnGoJobList = findViewById(R.id.btnGoJobList);
        btnGoJobList.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, JobListActivity.class));
        });
    }

    private void showLoading() {
        tvResult.setText("Loading...");
    }

    private void showResult(String text) {
        runOnUiThread(() -> tvResult.setText(text));
    }

    // 1. Load all users
    private void loadUsers() {
        showLoading();

        RetrofitClient.getApiService().getAllUsers().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    selectedWorkerId = null;
                    selectedCoordinatorId = null;
                    lastSignupId = null;
                    StringBuilder sb = new StringBuilder("=== USERS ===\n");

                    for (User u : response.body()) {
                        sb.append("ID: ").append(u.getId())
                                .append(" | ").append(u.getName())
                                .append(" | ").append(u.getRole());

                        if (u.isCoordinator()) {
                            sb.append(" [BOSS]");
                            if (selectedCoordinatorId == null) {
                                selectedCoordinatorId = u.getId();
                            }
                        }

                        if (u.isWorker()) {
                            sb.append(" [WORKER]");
                            if (selectedWorkerId == null) {
                                selectedWorkerId = u.getId();
                            }
                        }

                        sb.append("\n");
                    }

                    sb.append("\nSelected workerId: ").append(selectedWorkerId);
                    sb.append("\nSelected coordinatorId: ").append(selectedCoordinatorId);

                    showResult(sb.toString());
                } else {
                    showResult("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                showResult("Failed: " + t.getMessage());
            }
        });
    }

    // 2. Load all jobs
    private void loadJobs() {
        showLoading();

        RetrofitClient.getApiService().getAllJobs().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    selectedJobId = null;
                    lastSignupId = null;
                    StringBuilder sb = new StringBuilder("=== JOBS ===\n");

                    for (Job j : response.body()) {
                        if (selectedJobId == null && j.hasAvailableSlots()) {
                            selectedJobId = j.getId();
                        }

                        sb.append("ID: ").append(j.getId())
                                .append(" | ").append(j.getTitle())
                                .append(" | ").append(j.getLocation())
                                .append(" | Slots: ").append(j.getFilledSlots()).append("/").append(j.getTotalSlots());

                        if (j.isOpen()) sb.append(" [OPEN]");
                        if (j.isFull()) sb.append(" [FULL]");
                        if (j.hasAvailableSlots()) sb.append(" [AVAILABLE]");

                        sb.append("\n");
                    }

                    sb.append("\nSelected jobId: ").append(selectedJobId);

                    showResult(sb.toString());
                } else {
                    showResult("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Job>> call, Throwable t) {
                showResult("Failed: " + t.getMessage());
            }
        });
    }

    // 3. Worker signs up for a job
    private void signUp() {
        if (selectedWorkerId == null || selectedJobId == null) {
            showResult("Load Users and Load Jobs first. Missing workerId or jobId.");
            return;
        }

        showLoading();

        RetrofitClient.getApiService().signUp(selectedWorkerId, selectedJobId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<JobSignup> call, Response<JobSignup> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JobSignup s = response.body();
                    lastSignupId = s.getId();

                    StringBuilder sb = new StringBuilder("=== SIGNUP CREATED ===\n");
                    sb.append("Signup ID: ").append(s.getId())
                            .append("\nStatus: ").append(s.getStatus());

                    if (s.isPending()) sb.append(" [PENDING]");
                    if (s.isApproved()) sb.append(" [APPROVED]");

                    sb.append("\nWorker: ").append(s.getWorker().getName())
                            .append("\nJob: ").append(s.getJob().getTitle())
                            .append("\n\nSaved lastSignupId: ").append(lastSignupId);

                    showResult(sb.toString());
                } else {
                    showErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<JobSignup> call, Throwable t) {
                showResult("Failed: " + t.getMessage());
            }
        });
    }

    private void showErrorResponse(Response<?> response) {
        try {
            String errorBody = response.errorBody() != null
                    ? response.errorBody().string()
                    : "Unknown error";

            showResult("Error " + response.code() + ":\n" + errorBody);
        } catch (Exception e) {
            showResult("Error: " + response.code());
        }
    }

    // 4. Coordinator approves signup
    private void approveSignup() {
        if (lastSignupId == null || selectedCoordinatorId == null) {
            showResult("Create a signup first and make sure users are loaded.");
            return;
        }

        showLoading();

        RetrofitClient.getApiService().approveSignup(lastSignupId, selectedCoordinatorId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<JobSignup> call, Response<JobSignup> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JobSignup s = response.body();

                    StringBuilder sb = new StringBuilder("=== APPROVED ===\n");
                    sb.append("Signup ID: ").append(s.getId())
                            .append("\nStatus: ").append(s.getStatus());

                    if (s.isApproved()) sb.append(" [APPROVED]");

                    if (s.getActionedBy() != null) {
                        sb.append("\nApproved by: ").append(s.getActionedBy().getName());
                    }

                    sb.append("\nUpdated at: ").append(s.getUpdatedAt());

                    showResult(sb.toString());
                } else {
                    showErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<JobSignup> call, Throwable t) {
                showResult("Failed: " + t.getMessage());
            }
        });
    }

    // 5. Coordinator marks attendance
// 5. Coordinator marks attendance
    private void markAttend() {
        if (lastSignupId == null || selectedCoordinatorId == null) {
            showResult("Approve a signup first and make sure users are loaded.");
            return;
        }

        showLoading();

        RetrofitClient.getApiService().markAttended(lastSignupId, selectedCoordinatorId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<JobAttendance> call, Response<JobAttendance> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JobAttendance a = response.body();

                    StringBuilder sb = new StringBuilder("=== ATTENDANCE ===\n");
                    sb.append("Attendance ID: ").append(a.getId())
                            .append("\nStatus: ").append(a.getStatus());

                    if (a.isCompleted()) sb.append(" [COMPLETED]");
                    if (a.isLate()) sb.append(" [LATE]");
                    if (a.isNoShow()) sb.append(" [NO_SHOW]");
                    if (a.hasLateMinutes()) sb.append(" (Late: ").append(a.getLateMinutes()).append(" min)");

                    if (a.getRecordedBy() != null) {
                        sb.append("\nRecorded by: ").append(a.getRecordedBy().getName());
                    }

                    sb.append("\nRecorded at: ").append(a.getRecordedAt());

                    showResult(sb.toString());
                } else {
                    showErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<JobAttendance> call, Throwable t) {
                showResult("Failed: " + t.getMessage());
            }
        });
    }
}