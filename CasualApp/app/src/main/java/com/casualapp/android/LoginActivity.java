package com.casualapp.android;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity {

    private TextView btnWorker, btnEmployer;
    private EditText etPhone, etPassword;
    private AppCompatButton btnLogin;
    private boolean isWorker = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnWorker = findViewById(R.id.btnWorker);
        btnEmployer = findViewById(R.id.btnEmployer);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnWorker.setOnClickListener(v -> setRole(true));
        btnEmployer.setOnClickListener(v -> setRole(false));

        btnLogin.setOnClickListener(v -> attemptLogin());

        findViewById(R.id.tvForgotPassword).setOnClickListener(v -> {
            Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show();
        });

        // Set initial state
        setRole(true);
    }

    private void setRole(boolean worker) {
        isWorker = worker;

        if (worker) {
            // Worker selected
            btnWorker.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_segment_selected));
            btnWorker.setTextColor(getColor(R.color.primary));
            btnWorker.setTypeface(null, Typeface.BOLD);
            btnWorker.setElevation(4f);

            btnEmployer.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_segment_unselected));
            btnEmployer.setTextColor(getColor(R.color.on_surface_variant));
            btnEmployer.setTypeface(null, Typeface.NORMAL);
            btnEmployer.setElevation(0f);
        } else {
            // Employer selected
            btnEmployer.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_segment_selected));
            btnEmployer.setTextColor(getColor(R.color.primary));
            btnEmployer.setTypeface(null, Typeface.BOLD);
            btnEmployer.setElevation(4f);

            btnWorker.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_segment_unselected));
            btnWorker.setTextColor(getColor(R.color.on_surface_variant));
            btnWorker.setTypeface(null, Typeface.NORMAL);
            btnWorker.setElevation(0f);
        }
    }

    private void attemptLogin() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (phone.isEmpty()) {
            etPhone.setError("請輸入電話號碼");
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("請輸入密碼");
            return;
        }

        String role = isWorker ? "WORKER" : "COORDINATOR";
        Toast.makeText(this, "Login as " + role + ": " + phone, Toast.LENGTH_SHORT).show();
    }
}