package com.casualapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.casualapp.android.model.Job;
import com.casualapp.android.model.User;
import com.casualapp.android.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobListActivity extends AppCompatActivity {

    private RecyclerView rvJobs;
    private JobAdapter jobAdapter;

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_list);

        bindViews();
        configureTopBar();
        configureBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadJobs();
    }

    private void bindViews() {
        rvJobs = findViewById(R.id.rvJobs);
        rvJobs.setLayoutManager(
                new LinearLayoutManager(this)
        );
    }

    private void configureTopBar() {
        findViewById(R.id.btnBack).setOnClickListener(v ->
                finish()
        );

        View btnCreateJob = findViewById(R.id.btnCreateJob);

        User currentUser = UserSession.getCurrentUser();

        /*
         * Workers should not be able to create jobs.
         * Only coordinators see and use the plus button.
         */
        if (currentUser != null && currentUser.isCoordinator()) {
            btnCreateJob.setVisibility(View.VISIBLE);

            btnCreateJob.setOnClickListener(v -> {
                Intent intent = new Intent(
                        JobListActivity.this,
                        CreateJobActivity.class
                );

                startActivity(intent);
            });

        } else {
            btnCreateJob.setVisibility(View.GONE);
        }
    }

    private void configureBottomNavigation() {
        findViewById(R.id.tabWorkList).setOnClickListener(v -> {
            // Already on the work-list screen.
        });

        findViewById(R.id.tabHistory).setOnClickListener(v ->
                Toast.makeText(
                        JobListActivity.this,
                        "工作記錄 coming soon",
                        Toast.LENGTH_SHORT
                ).show()
        );

        findViewById(R.id.tabMyJobs).setOnClickListener(v ->
                openMyJobs()
        );

        findViewById(R.id.tabProfile).setOnClickListener(v ->
                Toast.makeText(
                        JobListActivity.this,
                        "個人檔案 coming soon",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    private void openMyJobs() {
        Intent intent = new Intent(
                JobListActivity.this,
                MyJobsLandingActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        startActivity(intent);
    }

    private void loadJobs() {
        if (isLoading) {
            return;
        }

        isLoading = true;

        RetrofitClient.getApiService()
                .getAllJobs()
                .enqueue(new Callback<List<Job>>() {

                    @Override
                    public void onResponse(
                            Call<List<Job>> call,
                            Response<List<Job>> response
                    ) {
                        isLoading = false;

                        if (!response.isSuccessful()) {
                            Toast.makeText(
                                    JobListActivity.this,
                                    "Unable to load jobs. Error: "
                                            + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        List<Job> jobs = response.body();

                        if (jobs == null) {
                            Toast.makeText(
                                    JobListActivity.this,
                                    "The server returned no job data.",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        jobAdapter = new JobAdapter(
                                jobs,
                                JobListActivity.this::openJobDetails
                        );

                        rvJobs.setAdapter(jobAdapter);
                    }

                    @Override
                    public void onFailure(
                            Call<List<Job>> call,
                            Throwable throwable
                    ) {
                        isLoading = false;

                        Toast.makeText(
                                JobListActivity.this,
                                "Failed to load jobs: "
                                        + getFailureMessage(throwable),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void openJobDetails(Job job) {
        if (job == null) {
            Toast.makeText(
                    this,
                    "Unable to open this job.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Intent intent = new Intent(
                JobListActivity.this,
                JobDetailActivity.class
        );

        intent.putExtra("job", job);

        startActivity(intent);
    }

    private String getFailureMessage(Throwable throwable) {
        if (throwable == null) {
            return "Unknown network error";
        }

        String message = throwable.getMessage();

        if (message == null || message.trim().isEmpty()) {
            return throwable.getClass().getSimpleName();
        }

        return message;
    }
}