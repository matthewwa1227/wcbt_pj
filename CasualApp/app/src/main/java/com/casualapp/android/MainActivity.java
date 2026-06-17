package com.casualapp.android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.casualapp.android.model.User;
import com.casualapp.android.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLoadUsers = findViewById(R.id.btnLoadUsers);
        tvResult = findViewById(R.id.tvResult);

        btnLoadUsers.setOnClickListener(v -> loadUsers());
    }

    private void loadUsers() {
        tvResult.setText("Loading...");

        RetrofitClient.getApiService().getAllUsers().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();
                    StringBuilder sb = new StringBuilder();
                    for (User u : users) {
                        sb.append("ID: ").append(u.getId())
                                .append(", Name: ").append(u.getName())
                                .append(", Role: ").append(u.getRole())
                                .append("\n");
                    }
                    tvResult.setText(sb.toString());
                } else {
                    tvResult.setText("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                tvResult.setText("Failed: " + t.getMessage());
            }
        });
    }
}