package com.casualapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.casualapp.android.model.Job;

public class ApplySuccessActivity extends AppCompatActivity {

    private TextView tvJobDetail;
    private AppCompatButton btnMyJobs;
    private AppCompatButton btnBackHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_success);

        tvJobDetail = findViewById(R.id.tvJobDetail);
        btnMyJobs = findViewById(R.id.btnMyJobs);
        btnBackHome = findViewById(R.id.btnBackHome);

        ImageButton btnBack = findViewById(R.id.btnBack);

        Job job = (Job) getIntent().getSerializableExtra("job");

        long signupId = getIntent().getLongExtra(
                "signupId",
                -1L
        );

        String signupStatus = getIntent().getStringExtra(
                "signupStatus"
        );

        bindResult(job, signupId, signupStatus);

        btnBack.setOnClickListener(v -> openJobList());
        btnBackHome.setOnClickListener(v -> openJobList());
        btnMyJobs.setOnClickListener(v -> openMyApplications());
    }

    private void bindResult(
            Job job,
            long signupId,
            String signupStatus
    ) {
        StringBuilder text = new StringBuilder();

        if (job != null) {
            text.append(
                    safeText(job.getLocation(), "地點待定")
            );

            text.append(" - ");

            text.append(
                    safeText(job.getTitle(), "未命名職位")
            );

            text.append("\n");
            text.append(
                    JobDateFormatter.formatFullDate(
                            job.getJobDate()
                    )
            );

            text.append(" ");
            text.append(
                    JobDateFormatter.formatStartTime(
                            job.getJobDate()
                    )
            );
        }

        if (signupId >= 0) {
            text.append("\n申請編號：#");
            text.append(signupId);
        }

        text.append("\n狀態：");
        text.append(translateStatus(signupStatus));

        tvJobDetail.setText(text.toString());
    }

    private String translateStatus(String status) {
        if (status == null) {
            return "受理中";
        }

        switch (status) {
            case "APPROVED":
                return "已接受";

            case "REJECTED":
                return "已拒絕";

            case "CANCELLED":
                return "已取消";

            case "PENDING":
            default:
                return "受理中";
        }
    }

    private void openMyApplications() {
        Intent intent = new Intent(
                this,
                MyJobsActivity.class
        );

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        finish();
    }

    private void openJobList() {
        Intent intent = new Intent(
                this,
                JobListActivity.class
        );

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        finish();
    }

    private String safeText(String value, String fallback) {
        return value == null || value.isBlank()
                ? fallback
                : value;
    }
}