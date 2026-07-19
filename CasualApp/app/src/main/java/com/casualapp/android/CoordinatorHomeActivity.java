package com.casualapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.casualapp.android.model.User;

public class CoordinatorHomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private AppCompatButton btnCreateJob;
    private AppCompatButton btnMyPostedJobs;
    private AppCompatButton btnAttendance;
    private AppCompatButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_home);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnCreateJob = findViewById(R.id.btnCreateJob);
        btnMyPostedJobs = findViewById(R.id.btnMyPostedJobs);
        btnAttendance = findViewById(R.id.btnAttendance);
        btnLogout = findViewById(R.id.btnLogout);

        User currentUser = UserSession.getCurrentUser();

        if (currentUser == null || !currentUser.isCoordinator()) {
            UserSession.clear();

            Intent intent = new Intent(
                    this,
                    LoginActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
            return;
        }

        tvWelcome.setText(
                "歡迎，" + currentUser.getName()
        );

        btnCreateJob.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                this,
                                CreateJobActivity.class
                        )
                )
        );

        btnMyPostedJobs.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                this,
                                CoordinatorJobsActivity.class
                        )
                )
        );

        /*
         * Attendance UI will be connected after the applicant-list
         * screen is uploaded and inspected.
         */
        btnAttendance.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Open a job and select an approved worker to record attendance",
                        Toast.LENGTH_LONG
                ).show()
        );

        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
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