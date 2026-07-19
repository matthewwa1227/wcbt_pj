package com.casualapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.casualapp.android.model.Job;
import com.casualapp.android.model.User;
import com.casualapp.android.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoordinatorJobsActivity extends AppCompatActivity {

    private LinearLayout jobsContainer;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private TextView btnBack;

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_jobs);

        jobsContainer = findViewById(R.id.jobsContainer);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCoordinatorJobs();
    }

    private void loadCoordinatorJobs() {
        if (isLoading) {
            return;
        }

        User currentUser = UserSession.getCurrentUser();

        if (currentUser == null || !currentUser.isCoordinator()) {
            Toast.makeText(
                    this,
                    "Coordinator login is required",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        isLoading = true;

        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        jobsContainer.removeAllViews();

        RetrofitClient.getApiService()
                .getJobsByCoordinator(currentUser.getId())
                .enqueue(new Callback<List<Job>>() {

                    @Override
                    public void onResponse(
                            Call<List<Job>> call,
                            Response<List<Job>> response
                    ) {
                        isLoading = false;
                        progressBar.setVisibility(View.GONE);

                        if (!response.isSuccessful()) {
                            showError(response);
                            return;
                        }

                        List<Job> jobs = response.body();

                        if (jobs == null || jobs.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                            return;
                        }

                        for (Job job : jobs) {
                            if (job != null) {
                                addJobView(job);
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Job>> call,
                            Throwable throwable
                    ) {
                        isLoading = false;
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(
                                CoordinatorJobsActivity.this,
                                "Network failed: "
                                        + getFailureMessage(throwable),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void addJobView(Job job) {
        TextView jobView = new TextView(this);

        String text =
                safeText(job.getTitle(), "Untitled job")
                        + "\n"
                        + safeText(job.getLocation(), "No location")
                        + "\n"
                        + formatDate(job.getJobDate())
                        + "\nFilled: "
                        + job.getFilledSlots()
                        + " / "
                        + job.getTotalSlots()
                        + "\nStatus: "
                        + safeText(job.getStatus(), "UNKNOWN")
                        + "\n\nTap to manage applicants";

        jobView.setText(text);
        jobView.setTextSize(17f);

        jobView.setTextColor(
                getResources().getColor(
                        android.R.color.black,
                        getTheme()
                )
        );

        jobView.setPadding(32, 28, 32, 28);

        jobView.setBackgroundResource(
                android.R.drawable.dialog_holo_light_frame
        );

        jobView.setClickable(true);
        jobView.setFocusable(true);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 0, 0, 24);
        jobView.setLayoutParams(params);

        jobView.setOnClickListener(v -> openApplicants(job));

        jobsContainer.addView(jobView);
    }

    private void openApplicants(Job job) {
        if (job.getId() == null) {
            Toast.makeText(
                    this,
                    "This job has no valid ID",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        Intent intent = new Intent(
                CoordinatorJobsActivity.this,
                CoordinatorApplicantsActivity.class
        );

        intent.putExtra("jobId", job.getId());
        intent.putExtra(
                "jobTitle",
                safeText(job.getTitle(), "Job applicants")
        );

        startActivity(intent);
    }

    private String formatDate(String rawDate) {
        if (rawDate == null || rawDate.trim().isEmpty()) {
            return "Date: not specified";
        }

        if (rawDate.contains("T")) {
            return "Date: "
                    + rawDate.substring(
                            0,
                            rawDate.indexOf("T")
                    );
        }

        return "Date: " + rawDate;
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

    private String getFailureMessage(Throwable throwable) {
        if (throwable == null) {
            return "Unknown network error";
        }

        if (throwable.getMessage() == null
                || throwable.getMessage().trim().isEmpty()) {

            return throwable.getClass().getSimpleName();
        }

        return throwable.getMessage();
    }

    private void showError(Response<?> response) {
        try {
            String error = response.errorBody() != null
                    ? response.errorBody().string()
                    : "Unable to load jobs";

            Toast.makeText(
                    this,
                    "Error " + response.code() + ":\n" + error,
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception exception) {
            Toast.makeText(
                    this,
                    "Error: " + response.code(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}