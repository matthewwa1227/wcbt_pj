package com.casualapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.casualapp.android.model.JobSignup;
import com.casualapp.android.model.User;
import com.casualapp.android.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyJobsLandingActivity extends AppCompatActivity {

    private TextView tvBadgeCount;
    private LinearLayout rowApplications;
    private LinearLayout rowSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_jobs_landing);

        tvBadgeCount = findViewById(R.id.tvBadgeCount);
        rowApplications = findViewById(R.id.rowApplications);
        rowSchedule = findViewById(R.id.rowSchedule);

        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        rowApplications.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                this,
                                MyJobsActivity.class
                        )
                )
        );

        findViewById(R.id.rowSchedule).setOnClickListener(v -> {
        Intent intent = new Intent(
                MyJobsLandingActivity.this,
                MyScheduleActivity.class
        );

        startActivity(intent);
        });

        findViewById(R.id.tabWorkList).setOnClickListener(v -> {
            Intent intent = new Intent(
                    this,
                    JobListActivity.class
            );

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBadgeCount();
    }

    private void loadBadgeCount() {
        User currentWorker = UserSession.getCurrentUser();

        if (!isWorker(currentWorker)) {
            returnToLogin();
            return;
        }

        RetrofitClient.getApiService()
                .getWorkerSignups(currentWorker.getId())
                .enqueue(new Callback<>() {

                    @Override
                    public void onResponse(
                            Call<List<JobSignup>> call,
                            Response<List<JobSignup>> response
                    ) {
                        if (!response.isSuccessful()
                                || response.body() == null) {

                            setBadgeCount(0);
                            return;
                        }

                        setBadgeCount(response.body().size());
                    }

                    @Override
                    public void onFailure(
                            Call<List<JobSignup>> call,
                            Throwable throwable
                    ) {
                        setBadgeCount(0);
                    }
                });
    }

    private void setBadgeCount(int count) {
        tvBadgeCount.setText(String.valueOf(count));

        tvBadgeCount.setVisibility(
                count > 0 ? View.VISIBLE : View.GONE
        );
    }

    private boolean isWorker(User user) {
        return user != null
                && user.getRole() != null
                && "WORKER".equals(user.getRole().name());
    }

    private void returnToLogin() {
        UserSession.clear();

        Intent intent = new Intent(
                this,
                LoginActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}