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
    private static final Long COORDINATOR_ID = 1L;
    private static final Long WORKER_ID = 2L;
    private static final Long JOB_ID = 1L;

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
        btnApprove.setOnClickListener(v -> approveSignup(3L));
        btnAttend.setOnClickListener(v -> markAttend(3L));

        Button btnGoLogin = findViewById(R.id.btnGoLogin);
        btnGoLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
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
                    StringBuilder sb = new StringBuilder("=== USERS ===\n");
                    for (User u : response.body()) {
                        sb.append("ID: ").append(u.getId())
                                .append(" | ").append(u.getName())
                                .append(" | ").append(u.getRole());
                        if (u.isCoordinator()) sb.append(" [BOSS]");
                        if (u.isWorker()) sb.append(" [WORKER]");
                        sb.append("\n");
                    }
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
                    StringBuilder sb = new StringBuilder("=== JOBS ===\n");
                    for (Job j : response.body()) {
                        sb.append("ID: ").append(j.getId())
                                .append(" | ").append(j.getTitle())
                                .append(" | ").append(j.getLocation())
                                .append(" | Slots: ").append(j.getFilledSlots()).append("/").append(j.getTotalSlots());
                        if (j.isOpen()) sb.append(" [OPEN]");
                        if (j.isFull()) sb.append(" [FULL]");
                        if (j.hasAvailableSlots()) sb.append(" [AVAILABLE]");
                        sb.append("\n");
                    }
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
        showLoading();
        RetrofitClient.getApiService().signUp(WORKER_ID, JOB_ID).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<JobSignup> call, Response<JobSignup> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JobSignup s = response.body();
                    StringBuilder sb = new StringBuilder("=== SIGNUP CREATED ===\n");
                    sb.append("Signup ID: ").append(s.getId())
                            .append("\nStatus: ").append(s.getStatus());
                    if (s.isPending()) sb.append(" [PENDING]");
                    if (s.isApproved()) sb.append(" [APPROVED]");
                    sb.append("\nWorker: ").append(s.getWorker().getName())
                            .append("\nJob: ").append(s.getJob().getTitle());
                    showResult(sb.toString());
                }  else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        showResult("Error " + response.code() + ":\n" + errorBody);
                    } catch (Exception e) {
                        showResult("Error: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<JobSignup> call, Throwable t) {
                showResult("Failed: " + t.getMessage());
            }
        });
    }

    // 4. Coordinator approves signup
    private void approveSignup(Long signupId) {
        showLoading();
        RetrofitClient.getApiService().approveSignup(signupId, COORDINATOR_ID).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<JobSignup> call, Response<JobSignup> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JobSignup s = response.body();
                    StringBuilder sb = new StringBuilder("=== APPROVED ===\n");
                    sb.append("Signup ID: ").append(s.getId())
                            .append("\nStatus: ").append(s.getStatus());
                    if (s.isApproved()) sb.append(" [APPROVED]");
                    sb.append("\nApproved by: ").append(s.getActionedBy().getName())
                            .append("\nUpdated at: ").append(s.getUpdatedAt());
                    showResult(sb.toString());
                } else {
                    showResult("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JobSignup> call, Throwable t) {
                showResult("Failed: " + t.getMessage());
            }
        });
    }

    // 5. Coordinator marks attendance
    private void markAttend(Long signupId) {
        showLoading();
        RetrofitClient.getApiService().markAttended(signupId, COORDINATOR_ID).enqueue(new Callback<>() {
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
                    sb.append("\nRecorded by: ").append(a.getRecordedBy().getName())
                            .append("\nRecorded at: ").append(a.getRecordedAt());
                    showResult(sb.toString());
                } else {
                    showResult("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JobAttendance> call, Throwable t) {
                showResult("Failed: " + t.getMessage());
            }
        });
    }
}