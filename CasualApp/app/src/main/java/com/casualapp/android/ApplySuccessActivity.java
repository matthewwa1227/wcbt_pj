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

        bindViews();
        readApplicationResult();
        configureButtons();
    }

    private void bindViews() {
        tvJobDetail = findViewById(R.id.tvJobDetail);
        btnMyJobs = findViewById(R.id.btnMyJobs);
        btnBackHome = findViewById(R.id.btnBackHome);
    }

    private void readApplicationResult() {
        Job job = (Job) getIntent()
                .getSerializableExtra("job");

        long signupId = getIntent().getLongExtra(
                "signupId",
                -1L
        );

        String signupStatus = getIntent()
                .getStringExtra("signupStatus");

        bindResult(
                job,
                signupId,
                signupStatus
        );
    }

    private void configureButtons() {
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v ->
                openJobList()
        );

        btnBackHome.setOnClickListener(v ->
                openJobList()
        );

        btnMyJobs.setOnClickListener(v ->
                openMyJobs()
        );
    }

        private void bindResult(
                Job job,
                long signupId,
                String signupStatus
        ) {
        StringBuilder text = new StringBuilder();

        if (job != null) {

                text.append(
                        safeText(
                                job.getLocation(),
                                "地點待定"
                        )
                );

                text.append(" - ");

                text.append(
                        safeText(
                                job.getTitle(),
                                "未命名職位"
                        )
                );

                text.append("\n");

                String startDateTime =
                        job.getStartDateTime();

                String endDateTime =
                        job.getEndDateTime();

                text.append(
                        JobDateFormatter.formatFullDate(
                                startDateTime
                        )
                );

                text.append(" ");

                text.append(
                        formatTimeRange(
                                startDateTime,
                                endDateTime
                        )
                );

                if (job.getHourlyRate() != null) {

                text.append("\n時薪：HK$");

                text.append(
                        job.getHourlyRate()
                                .stripTrailingZeros()
                                .toPlainString()
                );

                text.append("/hr");
                }

        } else {

                text.append("申請已提交");
        }

        if (signupId >= 0) {
                text.append("\n申請編號：#");
                text.append(signupId);
        }

        text.append("\n狀態：");

        text.append(
                translateStatus(signupStatus)
        );

        tvJobDetail.setText(
                text.toString()
        );
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

    private void openMyJobs() {
        Intent intent = new Intent(
                ApplySuccessActivity.this,
                MyJobsLandingActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        startActivity(intent);
        finish();
    }

    private void openJobList() {
        Intent intent = new Intent(
                ApplySuccessActivity.this,
                JobListActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        startActivity(intent);
        finish();
    }

        private String formatTimeRange(
                String startDateTime,
                String endDateTime
        ) {

        String startTime =
                extractTime(startDateTime);

        String endTime =
                extractTime(endDateTime);

        if (startTime == null
                && endTime == null) {

                return "時間待定";
        }

        if (startTime != null
                && endTime == null) {

                return startTime + " 開始";
        }

        if (startTime == null) {

                return endTime + " 結束";
        }

        return startTime
                + " - "
                + endTime;
        }

        private String extractTime(
                String dateTime
        ) {

        if (dateTime == null
                || dateTime.length() < 16) {

                return null;
        }

        try {

                return dateTime.substring(
                        11,
                        16
                );

        } catch (IndexOutOfBoundsException e) {

                return null;
        }
        }

    private String safeText(
            String value,
            String fallback
    ) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        return value;
    }
}