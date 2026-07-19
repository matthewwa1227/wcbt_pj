package com.casualapp.android;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_jobs);

        jobsContainer = findViewById(R.id.jobsContainer);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        loadCoordinatorJobs();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (jobsContainer != null) {
            loadCoordinatorJobs();
        }
    }

    private void loadCoordinatorJobs() {
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

        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        jobsContainer.removeAllViews();

        RetrofitClient.getApiService()
                .getJobsByCoordinator(currentUser.getId())
                .enqueue(new Callback<>() {

                    @Override
                    public void onResponse(
                            Call<List<Job>> call,
                            Response<List<Job>> response
                    ) {
                        progressBar.setVisibility(View.GONE);

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            showError(response);
                            return;
                        }

                        List<Job> jobs = response.body();

                        if (jobs.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                            return;
                        }

                        for (Job job : jobs) {
                            addJobView(job);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Job>> call,
                            Throwable throwable
                    ) {
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(
                                CoordinatorJobsActivity.this,
                                "Network failed: "
                                        + throwable.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void addJobView(Job job) {
        TextView jobView = new TextView(this);

        String text =
                job.getTitle()
                        + "\n"
                        + safeText(job.getLocation())
                        + "\n"
                        + safeText(job.getJobDate())
                        + "\nFilled: "
                        + job.getFilledSlots()
                        + " / "
                        + job.getTotalSlots()
                        + "\nStatus: "
                        + safeText(job.getStatus());

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

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 0, 0, 24);
        jobView.setLayoutParams(params);

        jobView.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Applicant management for: "
                                + job.getTitle(),
                        Toast.LENGTH_SHORT
                ).show()
        );

        jobsContainer.addView(jobView);
    }

    private String safeText(Object value) {
        return value == null ? "-" : value.toString();
    }

    private void showError(Response<?> response) {
        try {
            String error = response.errorBody() != null
                    ? response.errorBody().string()
                    : "Unable to load jobs";

            Toast.makeText(
                    this,
                    error,
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