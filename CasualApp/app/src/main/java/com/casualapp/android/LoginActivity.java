package com.casualapp.android;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import com.casualapp.android.model.LoginRequest;
import com.casualapp.android.model.User;
import com.casualapp.android.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        setRole(true);
    }

    private void setRole(boolean worker) {
        isWorker = worker;

        if (worker) {
            btnWorker.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_segment_selected));
            btnWorker.setTextColor(getColor(R.color.primary));
            btnWorker.setTypeface(null, Typeface.BOLD);
            btnWorker.setElevation(4f);

            btnEmployer.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_segment_unselected));
            btnEmployer.setTextColor(getColor(R.color.on_surface_variant));
            btnEmployer.setTypeface(null, Typeface.NORMAL);
            btnEmployer.setElevation(0f);
        } else {
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

        LoginRequest request = new LoginRequest(phone, password);

        RetrofitClient.getApiService().login(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    UserSession.setCurrentUser(user);

                    String selectedRole = isWorker ? "WORKER" : "COORDINATOR";
                    if (!selectedRole.equals(user.getRole().name())) {
                        Toast.makeText(LoginActivity.this,
                            "身份不匹配：此帳戶為 " + user.getRole().name(),
                            Toast.LENGTH_LONG).show();
                        return;
                    }

                    Toast.makeText(LoginActivity.this,
                        "歡迎 " + user.getName(),
                        Toast.LENGTH_SHORT).show();

                    if (user.isCoordinator()) {
                        startActivity(new Intent(LoginActivity.this, CreateJobActivity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this, RegionSelectionActivity.class));
                    }
                    finish();

                } else {
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "Login failed";
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Login error: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}