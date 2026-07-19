package com.casualapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

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

        bindViews();

        User currentUser = UserSession.getCurrentUser();

        if (!isValidCoordinator(currentUser)) {
            returnToLogin();
            return;
        }

        tvWelcome.setText(
                "歡迎，" + currentUser.getName()
        );

        configureButtons();
    }

    private void bindViews() {
        tvWelcome = findViewById(R.id.tvWelcome);

        btnCreateJob = findViewById(R.id.btnCreateJob);
        btnMyPostedJobs = findViewById(R.id.btnMyPostedJobs);
        btnAttendance = findViewById(R.id.btnAttendance);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void configureButtons() {
        btnCreateJob.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                CoordinatorHomeActivity.this,
                                CreateJobActivity.class
                        )
                )
        );

        btnMyPostedJobs.setOnClickListener(v ->
                openCoordinatorJobs(false)
        );

        /*
         * Attendance currently uses the same posted-jobs screen.
         *
         * The coordinator selects a job, opens its applicant list,
         * and records attendance for an approved worker.
         */
        btnAttendance.setOnClickListener(v ->
                openCoordinatorJobs(true)
        );

        btnLogout.setOnClickListener(v -> logout());
    }

    private void openCoordinatorJobs(
            boolean attendanceMode
    ) {
        Intent intent = new Intent(
                CoordinatorHomeActivity.this,
                CoordinatorJobsActivity.class
        );

        intent.putExtra(
                "attendanceMode",
                attendanceMode
        );

        startActivity(intent);
    }

    private boolean isValidCoordinator(User user) {
        return user != null
                && user.getId() != null
                && user.isCoordinator();
    }

    private void returnToLogin() {
        UserSession.clear();

        Intent intent = new Intent(
                CoordinatorHomeActivity.this,
                LoginActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }

    private void logout() {
        UserSession.clear();

        Intent intent = new Intent(
                CoordinatorHomeActivity.this,
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