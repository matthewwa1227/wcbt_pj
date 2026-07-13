package com.casualapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.casualapp.android.model.Job;
import com.casualapp.android.model.JobSignup;
import com.casualapp.android.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmApplyActivity extends AppCompatActivity {

    private TextView tvHotelName, tvJobTitle, tvSlotDay, tvSlotDate, tvSlotTime, tvSlotPrice, tvSlotCount;
    private AppCompatButton btnConfirm;
    private Job job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_apply);

        tvHotelName = findViewById(R.id.tvHotelName);
        tvJobTitle = findViewById(R.id.tvJobTitle);
        tvSlotDay = findViewById(R.id.tvSlotDay);
        tvSlotDate = findViewById(R.id.tvSlotDate);
        tvSlotTime = findViewById(R.id.tvSlotTime);
        tvSlotPrice = findViewById(R.id.tvSlotPrice);
        tvSlotCount = findViewById(R.id.tvSlotCount);
        btnConfirm = findViewById(R.id.btnConfirm);
        ImageButton btnBack = findViewById(R.id.btnBack);

        job = (Job) getIntent().getSerializableExtra("job");
        if (job == null) {
            Toast.makeText(this, "Job not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindData();

        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> submitApplication());
    }

    private void bindData() {
        tvHotelName.setText(job.getLocation());
        tvJobTitle.setText(job.getTitle());
        tvSlotDate.setText("2026年6月25日 (星期四)"); // TODO: format from jobDate
        tvSlotTime.setText("18:00 - 23:00"); // TODO: parse from jobDate
        tvSlotPrice.setText("$120"); // TODO: add price field to Job
        tvSlotCount.setText("共 1 個時段");
    }

    private void submitApplication() {
        Long workerId = UserSession.getCurrentUser() != null ? UserSession.getCurrentUser().getId() : 2L;

        btnConfirm.setEnabled(false);
        btnConfirm.setText("提交中...");

        RetrofitClient.getApiService().signUp(workerId, job.getId()).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<JobSignup> call, Response<JobSignup> response) {
                btnConfirm.setEnabled(true);
                btnConfirm.setText("確認");

                if (response.isSuccessful() && response.body() != null) {
                    Intent intent = new Intent(ConfirmApplyActivity.this, ApplySuccessActivity.class);
                    intent.putExtra("job", job);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "Apply failed";
                        Toast.makeText(ConfirmApplyActivity.this, error, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(ConfirmApplyActivity.this, "Error: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JobSignup> call, Throwable t) {
                btnConfirm.setEnabled(true);
                btnConfirm.setText("確認");
                Toast.makeText(ConfirmApplyActivity.this, "Network failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}