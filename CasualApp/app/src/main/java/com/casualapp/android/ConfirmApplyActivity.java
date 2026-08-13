package com.casualapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.casualapp.android.model.Job;
import com.casualapp.android.model.SignupResponse;
import com.casualapp.android.model.User;
import com.casualapp.android.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmApplyActivity extends AppCompatActivity {

    private TextView tvHotelName;
    private TextView tvJobTitle;
    private TextView tvSlotMonth;
    private TextView tvSlotDay;
    private TextView tvSlotDate;
    private TextView tvSlotTime;
    private TextView tvSlotPrice;
    private TextView tvSlotCount;

    private AppCompatButton btnConfirm;

    private Job job;
    private User currentWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_apply);

        bindViews();

        job = (Job) getIntent().getSerializableExtra("job");
        currentWorker = UserSession.getCurrentUser();

        if (job == null) {
            Toast.makeText(
                    this,
                    "Job not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        if (!isWorker(currentWorker)) {
            Toast.makeText(
                    this,
                    "請先以員工帳戶登入",
                    Toast.LENGTH_LONG
            ).show();

            returnToLogin();
            return;
        }

        bindData();

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> submitApplication());
    }

    private void bindViews() {
        tvHotelName = findViewById(R.id.tvHotelName);
        tvJobTitle = findViewById(R.id.tvJobTitle);
        tvSlotMonth = findViewById(R.id.tvSlotMonth);
        tvSlotDay = findViewById(R.id.tvSlotDay);
        tvSlotDate = findViewById(R.id.tvSlotDate);
        tvSlotTime = findViewById(R.id.tvSlotTime);
        tvSlotPrice = findViewById(R.id.tvSlotPrice);
        tvSlotCount = findViewById(R.id.tvSlotCount);
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    private void bindData() {
        tvHotelName.setText(
                safeText(job.getLocation(), "地點待定")
        );

        tvJobTitle.setText(
                safeText(job.getTitle(), "未命名職位")
        );

        String jobDate = job.getJobDate();

        tvSlotMonth.setText(
                JobDateFormatter.formatMonth(jobDate)
        );

        tvSlotDay.setText(
                JobDateFormatter.formatDay(jobDate)
        );

        tvSlotDate.setText(
                JobDateFormatter.formatFullDate(jobDate)
        );

        tvSlotTime.setText(
                JobDateFormatter.formatStartTime(jobDate)
        );

        // No wage field exists in the current backend model.
        tvSlotPrice.setVisibility(View.GONE);

        tvSlotCount.setText("共 1 個工作時段");
    }

    private void submitApplication() {
        currentWorker = UserSession.getCurrentUser();

        if (!isWorker(currentWorker)) {
            Toast.makeText(
                    this,
                    "登入已失效，請重新登入",
                    Toast.LENGTH_LONG
            ).show();

            returnToLogin();
            return;
        }

        if (job.getId() == null) {
            Toast.makeText(
                    this,
                    "Invalid job ID",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        if (!job.isOpen() || !job.hasAvailableSlots()) {
            Toast.makeText(
                    this,
                    "此職位目前不可申請",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        setSubmitting(true);

        RetrofitClient.getApiService()
                .signUp(
                        currentWorker.getId(),
                        job.getId()
                )
                .enqueue(new Callback<>() {

                    @Override
                    public void onResponse(
                            Call<SignupResponse> call,
                            Response<SignupResponse> response
                    ) {
                        setSubmitting(false);

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            showError(response);
                            return;
                        }

                        SignupResponse createdSignup = response.body();

                        Intent intent = new Intent(
                                ConfirmApplyActivity.this,
                                ApplySuccessActivity.class
                        );

                        intent.putExtra("job", job);

                        if (createdSignup.getId() != null) {
                            intent.putExtra(
                                    "signupId",
                                    createdSignup.getId()
                            );
                        }

                        intent.putExtra(
                                "signupStatus",
                                createdSignup.getStatus()
                        );

                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(
                            Call<SignupResponse> call,
                            Throwable throwable
                    ) {
                        setSubmitting(false);

                        Toast.makeText(
                                ConfirmApplyActivity.this,
                                "Network failed: "
                                        + throwable.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void setSubmitting(boolean submitting) {
        btnConfirm.setEnabled(!submitting);
        btnConfirm.setText(
                submitting ? "提交中..." : "確認"
        );
    }

    private void showError(Response<?> response) {
        try {
            String message = response.errorBody() != null
                    ? response.errorBody().string()
                    : "Apply failed";

            Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception exception) {
            Toast.makeText(
                    this,
                    "Apply error: " + response.code(),
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

    private String safeText(String value, String fallback) {
        return value == null || value.isBlank()
                ? fallback
                : value;
    }
}