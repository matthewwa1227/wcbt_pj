package com.casualapp.android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.casualapp.android.model.Job;
import com.casualapp.android.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateJobActivity extends AppCompatActivity {

    private EditText etWorkplace;
    private EditText etDepartment;
    private EditText etPosition;
    private EditText etHourlyRate;
    private EditText etTotalSlots;
    private EditText etDate;
    private EditText etStartTime;
    private EditText etEndTime;
    private EditText etMeal;
    private EditText etNotes;
    private EditText etCoordinatorId;

    private CheckBox cbCantonese;
    private CheckBox cbMandarin;
    private CheckBox cbEnglish;

    private Button btnSave;
    private TextView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job);

        Toast.makeText(this, "Create Job screen opened", Toast.LENGTH_SHORT).show();

        etWorkplace = findViewById(R.id.etWorkplace);
        etDepartment = findViewById(R.id.etDepartment);
        etPosition = findViewById(R.id.etPosition);
        etHourlyRate = findViewById(R.id.etHourlyRate);
        etTotalSlots = findViewById(R.id.etTotalSlots);
        etDate = findViewById(R.id.etDate);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        etMeal = findViewById(R.id.etMeal);
        etNotes = findViewById(R.id.etNotes);
        etCoordinatorId = findViewById(R.id.etCoordinatorId);

        cbCantonese = findViewById(R.id.cbCantonese);
        cbMandarin = findViewById(R.id.cbMandarin);
        cbEnglish = findViewById(R.id.cbEnglish);

        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        etCoordinatorId.setText("1");
        etDate.setText("2026-07-10");
        etStartTime.setText("18:00");
        etEndTime.setText("23:00");

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> createJob());
    }

    private void createJob() {
        String workplace = getText(etWorkplace);
        String department = getText(etDepartment);
        String position = getText(etPosition);
        String hourlyRate = getText(etHourlyRate);
        String totalSlotsText = getText(etTotalSlots);
        String date = getText(etDate);
        String startTime = getText(etStartTime);
        String endTime = getText(etEndTime);
        String meal = getText(etMeal);
        String notes = getText(etNotes);
        String coordinatorIdText = getText(etCoordinatorId);

        if (workplace.isEmpty()) {
            etWorkplace.setError("請輸入工作地點");
            return;
        }

        if (position.isEmpty()) {
            etPosition.setError("請輸入職位");
            return;
        }

        if (totalSlotsText.isEmpty()) {
            etTotalSlots.setError("請輸入人數");
            return;
        }

        if (date.isEmpty()) {
            etDate.setError("請輸入日期");
            return;
        }

        if (startTime.isEmpty()) {
            etStartTime.setError("請輸入開始時間");
            return;
        }

        if (coordinatorIdText.isEmpty()) {
            etCoordinatorId.setError("請輸入 Coordinator ID");
            return;
        }

        int totalSlots;
        Long coordinatorId;

        try {
            totalSlots = Integer.parseInt(totalSlotsText);
            coordinatorId = Long.parseLong(coordinatorIdText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "人數和 Coordinator ID 必須是數字", Toast.LENGTH_SHORT).show();
            return;
        }

        if (totalSlots <= 0) {
            etTotalSlots.setError("人數必須大於 0");
            return;
        }

        String jobDate = date + "T" + startTime + ":00";

        String description = buildDescription(
                department,
                hourlyRate,
                startTime,
                endTime,
                meal,
                notes
        );

        Job job = new Job();
        job.setTitle(position);
        job.setDescription(description);
        job.setLocation(workplace);
        job.setJobDate(jobDate);
        job.setTotalSlots(totalSlots);
        job.setFilledSlots(0);
        job.setStatus("OPEN");

        btnSave.setEnabled(false);
        btnSave.setText("保存中...");

        RetrofitClient.getApiService().createJob(job, coordinatorId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Job> call, Response<Job> response) {
                btnSave.setEnabled(true);
                btnSave.setText("保存");

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(
                            CreateJobActivity.this,
                            "工作已建立：" + response.body().getTitle(),
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                } else {
                    Toast.makeText(
                            CreateJobActivity.this,
                            "建立失敗：" + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<Job> call, Throwable t) {
                btnSave.setEnabled(true);
                btnSave.setText("保存");

                Toast.makeText(
                        CreateJobActivity.this,
                        "Failed: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private String buildDescription(
            String department,
            String hourlyRate,
            String startTime,
            String endTime,
            String meal,
            String notes
    ) {
        StringBuilder sb = new StringBuilder();

        if (!department.isEmpty()) {
            sb.append("部門：").append(department).append("\n");
        }

        if (!hourlyRate.isEmpty()) {
            sb.append("時薪：$").append(hourlyRate).append("\n");
        }

        sb.append("時間：").append(startTime);

        if (!endTime.isEmpty()) {
            sb.append(" - ").append(endTime);
        }

        sb.append("\n");

        String languages = getSelectedLanguages();

        if (!languages.isEmpty()) {
            sb.append("語言：").append(languages).append("\n");
        }

        if (!meal.isEmpty()) {
            sb.append("膳食：").append(meal).append("\n");
        }

        if (!notes.isEmpty()) {
            sb.append("備注：").append(notes);
        }

        return sb.toString();
    }

    private String getSelectedLanguages() {
        StringBuilder sb = new StringBuilder();

        if (cbCantonese.isChecked()) {
            sb.append("廣東話 ");
        }

        if (cbMandarin.isChecked()) {
            sb.append("普通話 ");
        }

        if (cbEnglish.isChecked()) {
            sb.append("英文 ");
        }

        return sb.toString().trim();
    }

    private String getText(EditText editText) {
        return editText.getText().toString().trim();
    }
}