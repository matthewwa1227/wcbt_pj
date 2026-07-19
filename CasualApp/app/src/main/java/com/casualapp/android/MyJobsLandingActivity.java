package com.casualapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.casualapp.android.model.JobSignup;
import com.casualapp.android.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class MyJobsLandingActivity extends AppCompatActivity {

    private TextView tvBadgeCount;
    private LinearLayout rowApplications, rowSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_jobs_landing);

        tvBadgeCount = findViewById(R.id.tvBadgeCount);
        rowApplications = findViewById(R.id.rowApplications);
        rowSchedule = findViewById(R.id.rowSchedule);
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        rowApplications.setOnClickListener(v -> {
            startActivity(new Intent(this, MyJobsActivity.class));
        });

        rowSchedule.setOnClickListener(v -> {
            Toast.makeText(this, "我的行程 coming soon", Toast.LENGTH_SHORT).show();
        });

        // Bottom nav
        findViewById(R.id.tabWorkList).setOnClickListener(v -> {
            startActivity(new Intent(this, JobListActivity.class));
        });

        loadBadgeCount();
    }

    private void loadBadgeCount() {
        Long workerId = UserSession.getCurrentUser() != null ? UserSession.getCurrentUser().getId() : 2L;

        RetrofitClient.getApiService().getAllSignups().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<JobSignup>> call, Response<List<JobSignup>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    long count = response.body().stream()
                            .filter(s -> s.getWorker() != null && s.getWorker().getId().equals(workerId))
                            .count();
                    tvBadgeCount.setText(String.valueOf(count));
                }
            }

            @Override
            public void onFailure(Call<List<JobSignup>> call, Throwable t) {
                tvBadgeCount.setText("0");
            }
        });
    }
}