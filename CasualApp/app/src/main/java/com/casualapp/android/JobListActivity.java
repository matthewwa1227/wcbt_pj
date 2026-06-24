package com.casualapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.casualapp.android.model.Job;
import com.casualapp.android.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class JobListActivity extends AppCompatActivity {

    private RecyclerView rvJobs;
    private JobAdapter jobAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_list);

        rvJobs = findViewById(R.id.rvJobs);
        rvJobs.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        loadJobs();
    }

    private void loadJobs() {
        RetrofitClient.getApiService().getAllJobs().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Job> jobs = response.body();
                    jobAdapter = new JobAdapter(jobs, job -> {
                        Toast.makeText(JobListActivity.this,
                                "Clicked: " + job.getTitle(), Toast.LENGTH_SHORT).show();
                        // TODO: Open JobDetailActivity
                    });
                    rvJobs.setAdapter(jobAdapter);
                } else {
                    Toast.makeText(JobListActivity.this,
                            "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Job>> call, Throwable t) {
                Toast.makeText(JobListActivity.this,
                        "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}