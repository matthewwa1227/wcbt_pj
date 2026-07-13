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
    private AppCompatButton btnMyJobs, btnBackHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_success);

        tvJobDetail = findViewById(R.id.tvJobDetail);
        btnMyJobs = findViewById(R.id.btnMyJobs);
        btnBackHome = findViewById(R.id.btnBackHome);
        ImageButton btnBack = findViewById(R.id.btnBack);

        Job job = (Job) getIntent().getSerializableExtra("job");
        if (job != null) {
            tvJobDetail.setText(job.getLocation() + " - " + job.getTitle());
        }

        btnBack.setOnClickListener(v -> finish());

        btnMyJobs.setOnClickListener(v -> {
            // TODO: Open My Jobs screen
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }
}