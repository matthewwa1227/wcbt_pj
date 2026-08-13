package com.casualapp.android;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.casualapp.android.model.AttendanceResponse;
import com.casualapp.android.model.SignupResponse;
import com.casualapp.android.model.User;
import com.casualapp.android.network.RetrofitClient;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoordinatorApplicantsActivity
        extends AppCompatActivity {

    private TextView btnBack;
    private TextView tvJobTitle;
    private TextView tvEmpty;
    private ProgressBar progressBar;
    private LinearLayout applicantsContainer;

    private Long jobId;
    private Long coordinatorId;

    private final Set<Long> locallyRecordedAttendance =
            new HashSet<>();

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_coordinator_applicants
        );

        btnBack = findViewById(R.id.btnBack);
        tvJobTitle = findViewById(R.id.tvJobTitle);
        tvEmpty = findViewById(R.id.tvEmpty);
        progressBar = findViewById(R.id.progressBar);

        applicantsContainer =
                findViewById(R.id.applicantsContainer);

        btnBack.setOnClickListener(v -> finish());

        if (!readScreenData()) {
            return;
        }

        loadApplicants();
    }

    private boolean readScreenData() {

        User currentUser =
                UserSession.getCurrentUser();

        if (currentUser == null
                || !currentUser.isCoordinator()
                || currentUser.getId() == null) {

            Toast.makeText(
                    this,
                    "Coordinator login is required",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return false;
        }

        coordinatorId =
                currentUser.getId();

        long receivedJobId =
                getIntent().getLongExtra(
                        "jobId",
                        -1L
                );

        if (receivedJobId <= 0) {

            Toast.makeText(
                    this,
                    "Invalid job ID",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return false;
        }

        jobId = receivedJobId;

        String jobTitle =
                getIntent()
                        .getStringExtra("jobTitle");

        if (jobTitle == null
                || jobTitle.trim().isEmpty()) {

            jobTitle = "Job applicants";
        }

        tvJobTitle.setText(jobTitle);

        return true;
    }

    private void loadApplicants() {

        if (isLoading) {
            return;
        }

        isLoading = true;

        progressBar.setVisibility(
                View.VISIBLE
        );

        tvEmpty.setVisibility(
                View.GONE
        );

        applicantsContainer.removeAllViews();

        RetrofitClient.getApiService()
                .getJobSignups(
                        jobId,
                        coordinatorId
                )
                .enqueue(
                        new Callback<List<SignupResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<List<SignupResponse>> call,
                                    Response<List<SignupResponse>> response
                            ) {

                                isLoading = false;

                                progressBar.setVisibility(
                                        View.GONE
                                );

                                if (!response.isSuccessful()) {
                                    showErrorResponse(response);
                                    return;
                                }

                                List<SignupResponse> signups =
                                        response.body();

                                if (signups == null
                                        || signups.isEmpty()) {

                                    tvEmpty.setVisibility(
                                            View.VISIBLE
                                    );

                                    return;
                                }

                                for (SignupResponse signup : signups) {

                                    if (signup != null) {
                                        addApplicantCard(signup);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<List<SignupResponse>> call,
                                    Throwable throwable
                            ) {

                                isLoading = false;

                                progressBar.setVisibility(
                                        View.GONE
                                );

                                Toast.makeText(
                                        CoordinatorApplicantsActivity.this,
                                        "Failed to load applicants: "
                                                + getFailureMessage(
                                                        throwable
                                                ),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void addApplicantCard(
            SignupResponse signup
    ) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                dp(16),
                dp(16),
                dp(16),
                dp(16)
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.WHITE
        );

        background.setCornerRadius(
                dp(12)
        );

        background.setStroke(
                dp(1),
                Color.LTGRAY
        );

        card.setBackground(
                background
        );

        card.setElevation(
                dp(2)
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                dp(14)
        );

        card.setLayoutParams(
                cardParams
        );

        TextView tvWorker =
                createTextView(
                        getWorkerName(signup),
                        18,
                        true
                );

        TextView tvPhone =
                createTextView(
                        "Phone: "
                                + getWorkerPhone(signup),
                        14,
                        false
                );

        TextView tvStatus =
                createTextView(
                        "Application status: "
                                + safeText(
                                        signup.getStatus(),
                                        "UNKNOWN"
                                ),
                        15,
                        true
                );

        TextView tvSignupTime =
                createTextView(
                        "Applied: "
                                + formatDateTime(
                                        signup.getSignupTime()
                                ),
                        13,
                        false
                );

        card.addView(tvWorker);
        card.addView(tvPhone);
        card.addView(tvStatus);
        card.addView(tvSignupTime);

        if (signup.getActionReason() != null
                && !signup
                        .getActionReason()
                        .trim()
                        .isEmpty()) {

            TextView tvReason =
                    createTextView(
                            "Reason: "
                                    + signup.getActionReason(),
                            13,
                            false
                    );

            card.addView(tvReason);
        }

        if (signup.isPending()) {

            addPendingButtons(
                    card,
                    signup
            );

        } else if (signup.isApproved()) {

            addAttendanceButtons(
                    card,
                    signup
            );

        } else if (signup.isRejected()) {

            TextView message =
                    createTextView(
                            "This application has been rejected.",
                            14,
                            false
                    );

            card.addView(message);

        } else {

            TextView message =
                    createTextView(
                            "No actions are available for this application.",
                            14,
                            false
                    );

            card.addView(message);
        }

        applicantsContainer.addView(
                card
        );
    }

    private void addPendingButtons(
            LinearLayout card,
            SignupResponse signup
    ) {

        LinearLayout actions =
                createButtonRow();

        Button btnApprove =
                createButton("Approve");

        Button btnReject =
                createButton("Reject");

        btnApprove.setOnClickListener(
                v -> approveSignup(signup)
        );

        btnReject.setOnClickListener(
                v -> showRejectDialog(signup)
        );

        actions.addView(btnApprove);
        actions.addView(btnReject);

        card.addView(actions);
    }

    private void addAttendanceButtons(
            LinearLayout card,
            SignupResponse signup
    ) {

        if (signup.getId() != null
                && locallyRecordedAttendance.contains(
                        signup.getId()
                )) {

            TextView recorded =
                    createTextView(
                            "Attendance was recorded during this session.",
                            14,
                            true
                    );

            card.addView(recorded);

            return;
        }

        TextView label =
                createTextView(
                        "Record attendance:",
                        14,
                        true
                );

        card.addView(label);

        LinearLayout actions =
                createButtonRow();

        Button btnCompleted =
                createButton("Completed");

        Button btnLate =
                createButton("Late");

        Button btnNoShow =
                createButton("No show");

        btnCompleted.setOnClickListener(
                v -> recordAttendance(
                        signup,
                        "COMPLETED",
                        0,
                        "Shift completed"
                )
        );

        btnLate.setOnClickListener(
                v -> showLateDialog(signup)
        );

        btnNoShow.setOnClickListener(
                v -> confirmNoShow(signup)
        );

        actions.addView(btnCompleted);
        actions.addView(btnLate);
        actions.addView(btnNoShow);

        card.addView(actions);
    }

    private void approveSignup(
            SignupResponse signup
    ) {

        if (signup.getId() == null) {
            showInvalidSignup();
            return;
        }

        setActionLoading(true);

        RetrofitClient.getApiService()
                .approveSignup(
                        signup.getId(),
                        coordinatorId,
                        "Approved by coordinator"
                )
                .enqueue(
                        new Callback<SignupResponse>() {

                            @Override
                            public void onResponse(
                                    Call<SignupResponse> call,
                                    Response<SignupResponse> response
                            ) {

                                setActionLoading(false);

                                if (!response.isSuccessful()) {
                                    showErrorResponse(response);
                                    return;
                                }

                                Toast.makeText(
                                        CoordinatorApplicantsActivity.this,
                                        "Application approved",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadApplicants();
                            }

                            @Override
                            public void onFailure(
                                    Call<SignupResponse> call,
                                    Throwable throwable
                            ) {

                                setActionLoading(false);

                                Toast.makeText(
                                        CoordinatorApplicantsActivity.this,
                                        "Approval failed: "
                                                + getFailureMessage(
                                                        throwable
                                                ),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void showRejectDialog(
            SignupResponse signup
    ) {

        EditText input =
                new EditText(this);

        input.setHint(
                "Reason for rejection"
        );

        input.setSingleLine(false);
        input.setMinLines(2);

        int padding =
                dp(20);

        LinearLayout wrapper =
                new LinearLayout(this);

        wrapper.setPadding(
                padding,
                0,
                padding,
                0
        );

        wrapper.addView(
                input,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        new AlertDialog.Builder(this)
                .setTitle(
                        "Reject application"
                )
                .setView(wrapper)
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Reject",
                        (dialog, which) -> {

                            String reason =
                                    input.getText()
                                            .toString()
                                            .trim();

                            if (reason.isEmpty()) {
                                reason =
                                        "Rejected by coordinator";
                            }

                            rejectSignup(
                                    signup,
                                    reason
                            );
                        }
                )
                .show();
    }

    private void rejectSignup(
            SignupResponse signup,
            String reason
    ) {

        if (signup.getId() == null) {
            showInvalidSignup();
            return;
        }

        setActionLoading(true);

        RetrofitClient.getApiService()
                .rejectSignup(
                        signup.getId(),
                        coordinatorId,
                        reason
                )
                .enqueue(
                        new Callback<SignupResponse>() {

                            @Override
                            public void onResponse(
                                    Call<SignupResponse> call,
                                    Response<SignupResponse> response
                            ) {

                                setActionLoading(false);

                                if (!response.isSuccessful()) {
                                    showErrorResponse(response);
                                    return;
                                }

                                Toast.makeText(
                                        CoordinatorApplicantsActivity.this,
                                        "Application rejected",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadApplicants();
                            }

                            @Override
                            public void onFailure(
                                    Call<SignupResponse> call,
                                    Throwable throwable
                            ) {

                                setActionLoading(false);

                                Toast.makeText(
                                        CoordinatorApplicantsActivity.this,
                                        "Rejection failed: "
                                                + getFailureMessage(
                                                        throwable
                                                ),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void showLateDialog(
            SignupResponse signup
    ) {

        EditText input =
                new EditText(this);

        input.setHint(
                "Late minutes"
        );

        input.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );

        int padding =
                dp(20);

        LinearLayout wrapper =
                new LinearLayout(this);

        wrapper.setPadding(
                padding,
                0,
                padding,
                0
        );

        wrapper.addView(
                input,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle(
                                "Record late attendance"
                        )
                        .setView(wrapper)
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Record",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                unused ->
                        dialog.getButton(
                                AlertDialog.BUTTON_POSITIVE
                        ).setOnClickListener(
                                v -> {

                                    String text =
                                            input.getText()
                                                    .toString()
                                                    .trim();

                                    if (text.isEmpty()) {

                                        input.setError(
                                                "Enter the number of minutes"
                                        );

                                        return;
                                    }

                                    try {

                                        int lateMinutes =
                                                Integer.parseInt(
                                                        text
                                                );

                                        if (lateMinutes <= 0) {

                                            input.setError(
                                                    "Minutes must be greater than zero"
                                            );

                                            return;
                                        }

                                        dialog.dismiss();

                                        recordAttendance(
                                                signup,
                                                "LATE",
                                                lateMinutes,
                                                "Worker arrived "
                                                        + lateMinutes
                                                        + " minutes late"
                                        );

                                    } catch (
                                            NumberFormatException exception
                                    ) {

                                        input.setError(
                                                "Enter a valid number"
                                        );
                                    }
                                }
                        )
        );

        dialog.show();
    }

    private void confirmNoShow(
            SignupResponse signup
    ) {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Record no-show"
                )
                .setMessage(
                        "Confirm that this worker did not attend?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Confirm",
                        (dialog, which) ->
                                recordAttendance(
                                        signup,
                                        "NO_SHOW",
                                        0,
                                        "Worker did not attend"
                                )
                )
                .show();
    }

    private void recordAttendance(
            SignupResponse signup,
            String status,
            int lateMinutes,
            String reason
    ) {

        if (signup.getId() == null) {
            showInvalidSignup();
            return;
        }

        setActionLoading(true);

        RetrofitClient.getApiService()
                .markAttendance(
                        signup.getId(),
                        coordinatorId,
                        status,
                        lateMinutes,
                        reason
                )
                .enqueue(
                        new Callback<AttendanceResponse>() {

                        @Override
                        public void onResponse(
                                Call<AttendanceResponse> call,
                                Response<AttendanceResponse> response
                        ) {

                                setActionLoading(false);

                                if (!response.isSuccessful()) {
                                showErrorResponse(response);
                                return;
                                }

                                locallyRecordedAttendance.add(
                                        signup.getId()
                                );

                                Toast.makeText(
                                        CoordinatorApplicantsActivity.this,
                                        "Attendance recorded: "
                                                + status,
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadApplicants();
                        }

                        @Override
                        public void onFailure(
                                Call<AttendanceResponse> call,
                                Throwable throwable
                        ) {

                                setActionLoading(false);

                                Toast.makeText(
                                        CoordinatorApplicantsActivity.this,
                                        "Attendance failed: "
                                                + getFailureMessage(
                                                        throwable
                                                ),
                                        Toast.LENGTH_LONG
                                ).show();
                        }
                        }
                );
    }

    private LinearLayout createButtonRow() {

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setPadding(
                0,
                dp(12),
                0,
                0
        );

        row.setWeightSum(1f);

        return row;
    }

    private Button createButton(
            String text
    ) {

        Button button =
                new Button(this);

        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(12f);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        params.setMargins(
                dp(3),
                0,
                dp(3),
                0
        );

        button.setLayoutParams(
                params
        );

        return button;
    }

    private TextView createTextView(
            String text,
            int textSize,
            boolean bold
    ) {

        TextView textView =
                new TextView(this);

        textView.setText(text);
        textView.setTextSize(textSize);
        textView.setTextColor(
                Color.BLACK
        );

        if (bold) {

            textView.setTypeface(
                    textView.getTypeface(),
                    android.graphics.Typeface.BOLD
            );
        }

        textView.setPadding(
                0,
                0,
                0,
                dp(5)
        );

        return textView;
    }

    private void setActionLoading(
            boolean loading
    ) {

        isLoading = loading;

        progressBar.setVisibility(
                loading
                        ? View.VISIBLE
                        : View.GONE
        );
    }

    private void showInvalidSignup() {

        Toast.makeText(
                this,
                "Invalid signup ID",
                Toast.LENGTH_LONG
        ).show();
    }

    private String getWorkerName(
            SignupResponse signup
    ) {

        return safeText(
                signup.getWorkerName(),
                "Unknown worker"
        );
    }

    private String getWorkerPhone(
            SignupResponse signup
    ) {

        return safeText(
                signup.getWorkerPhoneNumber(),
                "-"
        );
    }

    private String formatDateTime(
            String value
    ) {

        if (value == null
                || value.trim().isEmpty()) {

            return "-";
        }

        return value.replace(
                "T",
                " "
        );
    }

    private String safeText(
            String value,
            String fallback
    ) {

        if (value == null
                || value.trim().isEmpty()) {

            return fallback;
        }

        return value;
    }

    private String getFailureMessage(
            Throwable throwable
    ) {

        if (throwable == null) {
            return "Unknown network error";
        }

        if (throwable.getMessage() == null
                || throwable
                        .getMessage()
                        .trim()
                        .isEmpty()) {

            return throwable
                    .getClass()
                    .getSimpleName();
        }

        return throwable.getMessage();
    }

    private void showErrorResponse(
            Response<?> response
    ) {

        try {

            String errorBody =
                    response.errorBody() != null
                            ? response
                                    .errorBody()
                                    .string()
                            : "Unknown server error";

            Toast.makeText(
                    this,
                    "Error "
                            + response.code()
                            + ":\n"
                            + errorBody,
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception exception) {

            Toast.makeText(
                    this,
                    "Error "
                            + response.code(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private int dp(
            int value
    ) {

        return Math.round(
                value
                        * getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}