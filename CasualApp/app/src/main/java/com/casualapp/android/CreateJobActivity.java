package com.casualapp.android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.casualapp.android.model.CreateJobRequest;
import com.casualapp.android.model.Job;
import com.casualapp.android.model.User;
import com.casualapp.android.network.RetrofitClient;

import java.math.BigDecimal;

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

    private CheckBox cbCantonese;
    private CheckBox cbMandarin;
    private CheckBox cbEnglish;

    private Button btnSave;
    private TextView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job);

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

        cbCantonese = findViewById(R.id.cbCantonese);
        cbMandarin = findViewById(R.id.cbMandarin);
        cbEnglish = findViewById(R.id.cbEnglish);

        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> createJob());
    }

    private void createJob() {

        /*
         * Coordinator comes from the authenticated session.
         * The user should never need to manually enter an
         * internal database ID.
         */
        User currentUser = UserSession.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(
                    this,
                    "登入資料已失效，請重新登入",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        if (!currentUser.isCoordinator()) {
            Toast.makeText(
                    this,
                    "只有 Coordinator 可以建立工作",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        if (currentUser.getId() == null) {
            Toast.makeText(
                    this,
                    "無法取得 Coordinator ID",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        String workplace = getText(etWorkplace);
        String department = getText(etDepartment);
        String position = getText(etPosition);
        String hourlyRateText = getText(etHourlyRate);
        String totalSlotsText = getText(etTotalSlots);
        String date = getText(etDate);
        String startTime = getText(etStartTime);
        String endTime = getText(etEndTime);
        String meal = getText(etMeal);
        String notes = getText(etNotes);

        if (workplace.isEmpty()) {
            etWorkplace.setError("請輸入工作地點");
            return;
        }

        if (position.isEmpty()) {
            etPosition.setError("請輸入職位");
            return;
        }

        if (hourlyRateText.isEmpty()) {
            etHourlyRate.setError("請輸入時薪");
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

        if (endTime.isEmpty()) {
            etEndTime.setError("請輸入結束時間");
            return;
        }

        BigDecimal hourlyRate;
        int totalSlots;

        try {
            hourlyRate = new BigDecimal(hourlyRateText);
        } catch (NumberFormatException e) {
            etHourlyRate.setError("時薪必須是有效數字");
            return;
        }

        try {
            totalSlots = Integer.parseInt(totalSlotsText);
        } catch (NumberFormatException e) {
            etTotalSlots.setError("人數必須是整數");
            return;
        }

        if (hourlyRate.compareTo(BigDecimal.ZERO) <= 0) {
            etHourlyRate.setError("時薪必須大於 0");
            return;
        }

        if (totalSlots <= 0) {
            etTotalSlots.setError("人數必須大於 0");
            return;
        }

        String startDateTime =
                toIsoDateTime(date, startTime);

        String endDateTime =
                toIsoDateTime(date, endTime);

        /*
         * Rate and shift times are no longer stored inside
         * description. They now have proper structured fields.
         */
        String description = buildDescription(
                department,
                meal,
                notes
        );

        CreateJobRequest request =
                new CreateJobRequest(
                        currentUser.getId(),
                        position,
                        description,
                        workplace,
                        startDateTime,
                        endDateTime,
                        hourlyRate,
                        totalSlots
                );

        setSavingState(true);

        RetrofitClient
                .getApiService()
                .createJob(request)
                .enqueue(new Callback<>() {

                    @Override
                    public void onResponse(
                            Call<Job> call,
                            Response<Job> response
                    ) {

                        setSavingState(false);

                        if (response.isSuccessful()
                                && response.body() != null) {

                            Toast.makeText(
                                    CreateJobActivity.this,
                                    "工作已建立："
                                            + response.body().getTitle(),
                                    Toast.LENGTH_SHORT
                            ).show();

                            finish();

                        } else {

                            Toast.makeText(
                                    CreateJobActivity.this,
                                    "建立失敗：HTTP "
                                            + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Job> call,
                            Throwable t
                    ) {

                        setSavingState(false);

                        Toast.makeText(
                                CreateJobActivity.this,
                                "連線失敗："
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private String buildDescription(
            String department,
            String meal,
            String notes
    ) {

        StringBuilder sb = new StringBuilder();

        if (!department.isEmpty()) {
            sb.append("部門：")
                    .append(department)
                    .append("\n");
        }

        String languages = getSelectedLanguages();

        if (!languages.isEmpty()) {
            sb.append("語言：")
                    .append(languages)
                    .append("\n");
        }

        if (!meal.isEmpty()) {
            sb.append("膳食：")
                    .append(meal)
                    .append("\n");
        }

        if (!notes.isEmpty()) {
            sb.append("備注：")
                    .append(notes);
        }

        return sb.toString().trim();
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

    private String toIsoDateTime(
            String date,
            String time
    ) {

        /*
         * Android input normally gives HH:mm.
         * Spring LocalDateTime expects an ISO value such as:
         *
         * 2026-09-05T18:00:00
         */
        if (time.length() == 5) {
            return date + "T" + time + ":00";
        }

        return date + "T" + time;
    }

    private void setSavingState(
            boolean saving
    ) {

        btnSave.setEnabled(!saving);
        btnSave.setText(
                saving
                        ? "保存中..."
                        : "保存"
        );
    }

    private String getText(
            EditText editText
    ) {
        return editText
                .getText()
                .toString()
                .trim();
    }
}