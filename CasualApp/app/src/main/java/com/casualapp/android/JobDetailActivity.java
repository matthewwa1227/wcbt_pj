package com.casualapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import com.casualapp.android.model.Job;

public class JobDetailActivity extends AppCompatActivity {

    private TextView tvJobTitle, tvStatusBadge, tvDescription, tvLocation, tvSelectedCount;
    private LinearLayout cardSlot1, cardSlot2;
    private CheckBox cbSlot1, cbSlot2;
    private AppCompatButton btnConfirm;
    private Job job;
    private int selectedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        tvJobTitle = findViewById(R.id.tvJobTitle);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvDescription = findViewById(R.id.tvDescription);
        tvLocation = findViewById(R.id.tvLocation);
        tvSelectedCount = findViewById(R.id.tvSelectedCount);
        cardSlot1 = findViewById(R.id.cardSlot1);
        cardSlot2 = findViewById(R.id.cardSlot2);
        cbSlot1 = findViewById(R.id.cbSlot1);
        cbSlot2 = findViewById(R.id.cbSlot2);
        btnConfirm = findViewById(R.id.btnConfirm);
        ImageButton btnBack = findViewById(R.id.btnBack);

        job = (Job) getIntent().getSerializableExtra("job");
        if (job == null) {
            Toast.makeText(this, "Job not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindJobData();

        btnBack.setOnClickListener(v -> finish());

        // Slot selection logic
        cbSlot1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateSlotVisual(cardSlot1, cbSlot1, isChecked);
            updateSelectedCount();
        });

        cbSlot2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateSlotVisual(cardSlot2, cbSlot2, isChecked);
            updateSelectedCount();
        });

        // Click card to toggle checkbox
        cardSlot1.setOnClickListener(v -> cbSlot1.toggle());
        cardSlot2.setOnClickListener(v -> cbSlot2.toggle());

        btnConfirm.setOnClickListener(v -> {
            if (selectedCount == 0) {
                Toast.makeText(this, "請選擇至少一個時段", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ConfirmApplyActivity.class);
            intent.putExtra("job", job);
            startActivity(intent);
        });

        // Initial state
        updateSlotVisual(cardSlot1, cbSlot1, cbSlot1.isChecked());
        updateSlotVisual(cardSlot2, cbSlot2, cbSlot2.isChecked());
        updateSelectedCount();
    }

    private void bindJobData() {
        tvJobTitle.setText(job.getTitle());
        tvLocation.setText(job.getLocation());
        tvDescription.setText(job.getDescription());

        if (job.isFull() || job.getFilledSlots() >= job.getTotalSlots() - 1) {
            tvStatusBadge.setText("即將滿額");
            tvStatusBadge.setBackgroundResource(R.drawable.bg_status_badge_red);
            tvStatusBadge.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        } else {
            tvStatusBadge.setText("熱烈招聘");
            tvStatusBadge.setBackgroundResource(R.drawable.bg_status_badge_green);
            tvStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.primary));
        }
    }

    private void updateSlotVisual(LinearLayout card, CheckBox cb, boolean checked) {
        if (checked) {
            card.setBackgroundResource(R.drawable.bg_slot_selected);
        } else {
            card.setBackgroundResource(R.drawable.bg_slot_unselected);
        }
    }

    private void updateSelectedCount() {
        selectedCount = 0;
        if (cbSlot1.isChecked()) selectedCount++;
        if (cbSlot2.isChecked()) selectedCount++;
        tvSelectedCount.setText("已選 " + selectedCount + " 個時段");
    }
}