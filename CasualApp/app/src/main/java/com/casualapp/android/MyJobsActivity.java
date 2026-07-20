package com.casualapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.casualapp.android.model.JobSignup;
import com.casualapp.android.model.User;
import com.casualapp.android.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyJobsActivity extends AppCompatActivity {

    private RecyclerView rvApplications;
    private ApplicationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_jobs);

        rvApplications = findViewById(R.id.rvApplications);
        rvApplications.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new ApplicationAdapter(
                new ArrayList<>()
        );

        rvApplications.setAdapter(adapter);

        ImageButton btnBack = findViewById(R.id.btnBack);
        MaterialButton btnViewSchedule =
                findViewById(R.id.btnViewSchedule);

        btnBack.setOnClickListener(v -> finish());

        btnViewSchedule.setOnClickListener(v -> {
        Intent intent = new Intent(
                MyJobsActivity.this,
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

        findViewById(R.id.tabMyJobs).setOnClickListener(v -> {
            Intent intent = new Intent(
                    this,
                    MyJobsLandingActivity.class
            );

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMyApplications();
    }

    private void loadMyApplications() {
        User currentWorker = UserSession.getCurrentUser();

        if (!isWorker(currentWorker)) {
            Toast.makeText(
                    this,
                    "請先以員工帳戶登入",
                    Toast.LENGTH_LONG
            ).show();

            returnToLogin();
            return;
        }

        setLoading(true);

        RetrofitClient.getApiService()
                .getWorkerSignups(currentWorker.getId())
                .enqueue(new Callback<>() {

                    @Override
                    public void onResponse(
                            Call<List<JobSignup>> call,
                            Response<List<JobSignup>> response
                    ) {
                        setLoading(false);

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            showError(response);
                            return;
                        }

                        List<JobSignup> applications =
                                new ArrayList<>(response.body());

                        applications.sort((first, second) -> {
                            long firstId = first.getId() == null
                                    ? 0L
                                    : first.getId();

                            long secondId = second.getId() == null
                                    ? 0L
                                    : second.getId();

                            return Long.compare(
                                    secondId,
                                    firstId
                            );
                        });

                        adapter.replaceData(applications);

                        if (applications.isEmpty()) {
                            Toast.makeText(
                                    MyJobsActivity.this,
                                    "暫時沒有申請記錄",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<JobSignup>> call,
                            Throwable throwable
                    ) {
                        setLoading(false);

                        Toast.makeText(
                                MyJobsActivity.this,
                                "Failed: "
                                        + throwable.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        rvApplications.setAlpha(
                loading ? 0.45f : 1f
        );
    }

    private void showError(Response<?> response) {
        try {
            String message = response.errorBody() != null
                    ? response.errorBody().string()
                    : "Unable to load applications";

            Toast.makeText(
                    this,
                    message,
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