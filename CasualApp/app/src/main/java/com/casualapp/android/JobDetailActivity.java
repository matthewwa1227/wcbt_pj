package com.casualapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.casualapp.android.model.Job;
import com.casualapp.android.model.User;

public class JobDetailActivity extends AppCompatActivity {

    private TextView tvJobTitle;
    private TextView tvStatusBadge;
    private TextView tvDescription;
    private TextView tvRequirements;
    private TextView tvLocation;
    private TextView tvSelectedCount;

    private TextView tvSlot1Month;
    private TextView tvSlot1Day;
    private TextView tvSlot1Date;
    private TextView tvSlot1Time;
    private TextView tvSlot1Price;

    private LinearLayout cardSlot1;
    private LinearLayout cardSlot2;

    private CheckBox cbSlot1;
    private CheckBox cbSlot2;

    private AppCompatButton btnConfirm;

    private Job job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        bindViews();

        job = (Job) getIntent().getSerializableExtra("job");

        if (job == null) {
            Toast.makeText(
                    this,
                    "Job not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        configureSingleShiftLayout();
        bindJobData();

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> openConfirmation());
    }

    private void bindViews() {
        tvJobTitle = findViewById(R.id.tvJobTitle);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvDescription = findViewById(R.id.tvDescription);
        tvRequirements = findViewById(R.id.tvRequirements);
        tvLocation = findViewById(R.id.tvLocation);
        tvSelectedCount = findViewById(R.id.tvSelectedCount);

        tvSlot1Month = findViewById(R.id.tvSlot1Month);
        tvSlot1Day = findViewById(R.id.tvSlot1Day);
        tvSlot1Date = findViewById(R.id.tvSlot1Date);
        tvSlot1Time = findViewById(R.id.tvSlot1Time);
        tvSlot1Price = findViewById(R.id.tvSlot1Price);

        cardSlot1 = findViewById(R.id.cardSlot1);
        cardSlot2 = findViewById(R.id.cardSlot2);

        cbSlot1 = findViewById(R.id.cbSlot1);
        cbSlot2 = findViewById(R.id.cbSlot2);

        btnConfirm = findViewById(R.id.btnConfirm);
    }

    private void configureSingleShiftLayout() {
        // One backend Job currently represents one actual shift.
        cardSlot2.setVisibility(View.GONE);
        cbSlot2.setVisibility(View.GONE);

        cbSlot1.setChecked(true);
        cbSlot1.setVisibility(View.GONE);

        tvSlot1Price.setVisibility(View.VISIBLE);

        cardSlot1.setBackgroundResource(
                R.drawable.bg_slot_selected
        );

        tvSelectedCount.setText("1 個工作時段");
    }

    private void bindJobData() {
        tvJobTitle.setText(safeText(job.getTitle(), "未命名職位"));
        tvLocation.setText(safeText(job.getLocation(), "地點待定"));
        tvDescription.setText(
                safeText(job.getDescription(), "未提供工作內容")
        );

        // There is no requirements field in the current Job model.
        tvRequirements.setText("未提供額外要求");

       String startDateTime =
        job.getStartDateTime();

        String endDateTime =
                job.getEndDateTime();

        tvSlot1Month.setText(
                JobDateFormatter.formatMonth(startDateTime)
        );

        tvSlot1Day.setText(
                JobDateFormatter.formatDay(startDateTime)
        );

        tvSlot1Date.setText(
                JobDateFormatter.formatFullDate(startDateTime)
        );

        tvSlot1Time.setText(
                formatTimeRange(
                        startDateTime,
                        endDateTime
                )
        );

        tvSlot1Price.setText(
                formatHourlyRate()
        );

        updateAvailability();
    }

    private void updateAvailability() {
        boolean hasSpace = job.hasAvailableSlots();
        boolean isOpen = job.isOpen();
        boolean available = isOpen && hasSpace;

        if (!available) {
            tvStatusBadge.setText(
                    job.isFull() || !hasSpace
                            ? "已滿額"
                            : "暫停申請"
            );

            tvStatusBadge.setBackgroundResource(
                    R.drawable.bg_status_badge_red
            );

            tvStatusBadge.setTextColor(
                    ContextCompat.getColor(
                            this,
                            android.R.color.holo_red_dark
                    )
            );

            btnConfirm.setEnabled(false);
            btnConfirm.setText("暫不可申請");
            return;
        }

        int remainingSlots =
                job.getTotalSlots() - job.getFilledSlots();

        if (remainingSlots <= 1) {
            tvStatusBadge.setText("即將滿額");
            tvStatusBadge.setBackgroundResource(
                    R.drawable.bg_status_badge_red
            );
            tvStatusBadge.setTextColor(
                    ContextCompat.getColor(
                            this,
                            android.R.color.holo_red_dark
                    )
            );
        } else {
            tvStatusBadge.setText("接受申請");
            tvStatusBadge.setBackgroundResource(
                    R.drawable.bg_status_badge_green
            );
            tvStatusBadge.setTextColor(
                    ContextCompat.getColor(
                            this,
                            R.color.primary
                    )
            );
        }

        btnConfirm.setEnabled(true);
        btnConfirm.setText("下一步");
    }

    private void openConfirmation() {
        User currentUser = UserSession.getCurrentUser();

        if (!isWorker(currentUser)) {
            Toast.makeText(
                    this,
                    "請先以員工帳戶登入",
                    Toast.LENGTH_LONG
            ).show();

            returnToLogin();
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

        Intent intent = new Intent(
                this,
                ConfirmApplyActivity.class
        );

        intent.putExtra("job", job);
        startActivity(intent);
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

        private String formatHourlyRate() {

        if (job.getHourlyRate() == null) {
                return "時薪待定";
        }

        return "HK$"
                + job.getHourlyRate()
                        .stripTrailingZeros()
                        .toPlainString()
                + "/hr";
        }
    private String safeText(String value, String fallback) {
        return value == null || value.isBlank()
                ? fallback
                : value;
    }
}