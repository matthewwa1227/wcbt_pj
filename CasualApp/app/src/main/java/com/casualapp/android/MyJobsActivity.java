package com.casualapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.casualapp.android.model.JobSignup;
import com.casualapp.android.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class MyJobsActivity extends AppCompatActivity {

    private RecyclerView rvApplications;
    private ApplicationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_jobs);

        rvApplications = findViewById(R.id.rvApplications);
        rvApplications.setLayoutManager(new LinearLayoutManager(this));
        ImageButton btnBack = findViewById(R.id.btnBack);
        MaterialButton btnViewSchedule = findViewById(R.id.btnViewSchedule);

        btnBack.setOnClickListener(v -> finish());

        btnViewSchedule.setOnClickListener(v -> {
            Toast.makeText(this, "Schedule coming soon", Toast.LENGTH_SHORT).show();
        });

        // Bottom nav
        findViewById(R.id.tabWorkList).setOnClickListener(v -> {
            startActivity(new Intent(this, JobListActivity.class));
        });
        findViewById(R.id.tabMyJobs).setOnClickListener(v -> {
            startActivity(new Intent(this, MyJobsLandingActivity.class));
        });
        loadMyApplications();
    }

    private void loadMyApplications() {
        Long workerId = UserSession.getCurrentUser() != null ? UserSession.getCurrentUser().getId() : 2L;

        RetrofitClient.getApiService().getAllSignups().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<JobSignup>> call, Response<List<JobSignup>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Filter signups for current worker
                    List<JobSignup> mySignups = response.body().stream()
                            .filter(s -> s.getWorker() != null && s.getWorker().getId().equals(workerId))
                            .collect(java.util.stream.Collectors.toList());

                    adapter = new ApplicationAdapter(mySignups);
                    rvApplications.setAdapter(adapter);
                } else {
                    Toast.makeText(MyJobsActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<JobSignup>> call, Throwable t) {
                Toast.makeText(MyJobsActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}