package com.casualapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.casualapp.android.model.WorkerScheduleItem;
import com.casualapp.android.model.WorkerScheduleResponse;
import com.casualapp.android.network.RetrofitClient;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyScheduleActivity extends AppCompatActivity {

    private static final long FALLBACK_WORKER_ID = 2L;

    private TextView tvTodayHeader;
    private TextView tvMonthTitle;
    private TextView tvUpcomingEmpty;
    private TextView tvCompletedEmpty;

    private GridLayout calendarGrid;
    private LinearLayout containerUpcoming;
    private LinearLayout containerCompleted;
    private ProgressBar progressBar;

    private YearMonth displayedMonth = YearMonth.now();

    private final List<WorkerScheduleItem> upcomingItems = new ArrayList<>();
    private final List<WorkerScheduleItem> completedItems = new ArrayList<>();
    private final Set<LocalDate> scheduledDates = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_schedule);

        bindViews();
        configureTopBar();
        configureCalendarControls();
        configureBottomNavigation();

        renderTodayHeader();
        renderCalendar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSchedule();
    }

    private void bindViews() {
        tvTodayHeader = findViewById(R.id.tvTodayHeader);
        tvMonthTitle = findViewById(R.id.tvMonthTitle);
        tvUpcomingEmpty = findViewById(R.id.tvUpcomingEmpty);
        tvCompletedEmpty = findViewById(R.id.tvCompletedEmpty);

        calendarGrid = findViewById(R.id.calendarGrid);
        containerUpcoming = findViewById(R.id.containerUpcoming);
        containerCompleted = findViewById(R.id.containerCompleted);
        progressBar = findViewById(R.id.progressBar);
    }

    private void configureTopBar() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton btnNotifications = findViewById(R.id.btnNotifications);

        btnBack.setOnClickListener(v -> finish());

        btnNotifications.setOnClickListener(v ->
                Toast.makeText(
                        MyScheduleActivity.this,
                        "通知功能開發中",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    private void configureCalendarControls() {
        findViewById(R.id.btnPreviousMonth).setOnClickListener(v -> {
            displayedMonth = displayedMonth.minusMonths(1);
            renderCalendar();
        });

        findViewById(R.id.btnNextMonth).setOnClickListener(v -> {
            displayedMonth = displayedMonth.plusMonths(1);
            renderCalendar();
        });
    }

    private void configureBottomNavigation() {
        findViewById(R.id.tabWorkList).setOnClickListener(v -> {
            Intent intent = new Intent(
                    MyScheduleActivity.this,
                    JobListActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
        });

        findViewById(R.id.tabHistory).setOnClickListener(v ->
                Toast.makeText(
                        MyScheduleActivity.this,
                        "工作記錄功能開發中",
                        Toast.LENGTH_SHORT
                ).show()
        );

        findViewById(R.id.tabMyJobs).setOnClickListener(v -> {
            Intent intent = new Intent(
                    MyScheduleActivity.this,
                    MyJobsLandingActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
        });

        findViewById(R.id.tabProfile).setOnClickListener(v ->
                Toast.makeText(
                        MyScheduleActivity.this,
                        "個人檔案功能開發中",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    private void renderTodayHeader() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "yyyy年M月d日（今天）",
                Locale.TAIWAN
        );

        tvTodayHeader.setText(LocalDate.now().format(formatter));
    }

    private void loadSchedule() {
        setLoading(true);

        long workerId = UserSession.getCurrentUser() != null
                ? UserSession.getCurrentUser().getId()
                : FALLBACK_WORKER_ID;

        RetrofitClient.getApiService()
                .getWorkerSchedule(workerId)
                .enqueue(new Callback<WorkerScheduleResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<WorkerScheduleResponse> call,
                            @NonNull Response<WorkerScheduleResponse> response
                    ) {
                        setLoading(false);

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(
                                    MyScheduleActivity.this,
                                    "無法載入行程，錯誤代碼：" + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();

                            showEmptyLists();
                            return;
                        }

                        WorkerScheduleResponse body = response.body();

                        upcomingItems.clear();
                        completedItems.clear();

                        if (body.getUpcoming() != null) {
                            upcomingItems.addAll(body.getUpcoming());
                        }

                        if (body.getCompleted() != null) {
                            completedItems.addAll(body.getCompleted());
                        }

                        rebuildScheduledDates();
                        renderCalendar();
                        renderScheduleLists();
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<WorkerScheduleResponse> call,
                            @NonNull Throwable throwable
                    ) {
                        setLoading(false);

                        Toast.makeText(
                                MyScheduleActivity.this,
                                "無法連接伺服器：" + safeMessage(throwable),
                                Toast.LENGTH_LONG
                        ).show();

                        showEmptyLists();
                    }
                });
    }

    private void renderScheduleLists() {
        containerUpcoming.removeAllViews();
        containerCompleted.removeAllViews();

        tvUpcomingEmpty.setVisibility(
                upcomingItems.isEmpty() ? View.VISIBLE : View.GONE
        );

        tvCompletedEmpty.setVisibility(
                completedItems.isEmpty() ? View.VISIBLE : View.GONE
        );

        for (WorkerScheduleItem item : upcomingItems) {
            containerUpcoming.addView(createScheduleRow(item, true));
        }

        for (WorkerScheduleItem item : completedItems) {
            containerCompleted.addView(createScheduleRow(item, false));
        }
    }

    private View createScheduleRow(
            WorkerScheduleItem item,
            boolean upcoming
    ) {
        View row = getLayoutInflater().inflate(
                R.layout.item_schedule_row,
                upcoming ? containerUpcoming : containerCompleted,
                false
        );

        TextView tvJobTitle = row.findViewById(R.id.tvScheduleJobTitle);
        TextView tvDate = row.findViewById(R.id.tvScheduleDate);
        TextView tvTime = row.findViewById(R.id.tvScheduleTime);
        TextView tvStatus = row.findViewById(R.id.tvScheduleStatus);

        String title = combineLocationAndTitle(item);

        tvJobTitle.setText(title);
        tvDate.setText(formatDisplayDate(item.getDate()));
        tvTime.setText(formatTimeRange(item.getStartTime(), item.getEndTime()));
        tvStatus.setText(upcoming
                ? translateSignupStatus(item.getSignupStatus())
                : translateAttendanceStatus(item)
        );

        row.setOnClickListener(v ->
                Toast.makeText(
                        MyScheduleActivity.this,
                        title,
                        Toast.LENGTH_SHORT
                ).show()
        );

        return row;
    }

    private void showEmptyLists() {
        upcomingItems.clear();
        completedItems.clear();
        scheduledDates.clear();
        renderCalendar();
        renderScheduleLists();
    }

    private void rebuildScheduledDates() {
        scheduledDates.clear();

        addDatesFromItems(upcomingItems);
        addDatesFromItems(completedItems);
    }

    private void addDatesFromItems(List<WorkerScheduleItem> items) {
        for (WorkerScheduleItem item : items) {
            LocalDate date = parseDate(item.getDate());

            if (date != null) {
                scheduledDates.add(date);
            }
        }
    }

    private void renderCalendar() {
        calendarGrid.removeAllViews();

        tvMonthTitle.setText(
                displayedMonth.getMonthValue()
                        + "月 ("
                        + displayedMonth.getYear()
                        + ")"
        );

        LocalDate firstDay = displayedMonth.atDay(1);
        int leadingBlankCells = mondayBasedIndex(firstDay.getDayOfWeek());
        int daysInMonth = displayedMonth.lengthOfMonth();

        int totalCells = leadingBlankCells + daysInMonth;
        int trailingBlankCells = (7 - (totalCells % 7)) % 7;

        for (int i = 0; i < leadingBlankCells; i++) {
            calendarGrid.addView(createCalendarCell(null));
        }

        for (int day = 1; day <= daysInMonth; day++) {
            calendarGrid.addView(
                    createCalendarCell(displayedMonth.atDay(day))
            );
        }

        for (int i = 0; i < trailingBlankCells; i++) {
            calendarGrid.addView(createCalendarCell(null));
        }
    }

    private TextView createCalendarCell(LocalDate date) {
        TextView cell = new TextView(this);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = dpToPx(46);
        params.columnSpec = GridLayout.spec(
                GridLayout.UNDEFINED,
                1f
        );
        params.setMargins(
                dpToPx(1),
                dpToPx(1),
                dpToPx(1),
                dpToPx(1)
        );

        cell.setLayoutParams(params);
        cell.setGravity(Gravity.CENTER);
        cell.setTextSize(12f);

        if (date == null) {
            cell.setText("");
            cell.setBackgroundResource(R.drawable.bg_calendar_empty_cell);
            return cell;
        }

        cell.setText(String.valueOf(date.getDayOfMonth()));

        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            cell.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        } else {
            cell.setTextColor(ContextCompat.getColor(this, R.color.on_surface));
        }

        if (date.equals(LocalDate.now())) {
            cell.setBackgroundResource(R.drawable.bg_calendar_day_selected);
            cell.setTypeface(cell.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (scheduledDates.contains(date)) {
            cell.setBackgroundResource(R.drawable.bg_calendar_day_has_shift);
        } else {
            cell.setBackgroundResource(R.drawable.bg_calendar_day_normal);
        }

        cell.setOnClickListener(v ->
                Toast.makeText(
                        MyScheduleActivity.this,
                        formatDisplayDate(date.toString()),
                        Toast.LENGTH_SHORT
                ).show()
        );

        return cell;
    }

    private int mondayBasedIndex(DayOfWeek dayOfWeek) {
        return dayOfWeek.getValue() - 1;
    }

    private String combineLocationAndTitle(WorkerScheduleItem item) {
        String location = safeText(item.getLocation(), "地點待定");
        String title = safeText(item.getTitle(), "未命名職位");

        return location + " - " + title;
    }

    private String translateSignupStatus(String status) {
        if ("APPROVED".equals(status)) {
            return "已批准";
        }

        if ("PENDING".equals(status)) {
            return "待處理";
        }

        if ("REJECTED".equals(status)) {
            return "已拒絕";
        }

        if ("CANCELLED".equals(status)) {
            return "已取消";
        }

        return "行程";
    }

    private String translateAttendanceStatus(WorkerScheduleItem item) {
        String status = item.getAttendanceStatus();

        if ("LATE".equals(status)) {
            int lateMinutes = item.getLateMinutes() == null
                    ? 0
                    : item.getLateMinutes();

            return lateMinutes > 0
                    ? "遲到 " + lateMinutes + " 分鐘"
                    : "遲到";
        }

        if ("NO_SHOW".equals(status)) {
            return "缺席";
        }

        if ("COMPLETED".equals(status)) {
            return "出席";
        }

        return "已記錄";
    }

    private String formatDisplayDate(String rawDate) {
        LocalDate date = parseDate(rawDate);

        if (date == null) {
            return "日期待定";
        }

        return date.format(
                DateTimeFormatter.ofPattern(
                        "d/M/yyyy",
                        Locale.TAIWAN
                )
        );
    }

    private String formatTimeRange(
            String startTime,
            String endTime
    ) {
        String start = safeText(startTime, "時間待定");

        if (endTime == null || endTime.trim().isEmpty()) {
            return start;
        }

        return start + " - " + endTime;
    }

    private LocalDate parseDate(String rawDate) {
        if (rawDate == null || rawDate.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(rawDate);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private String safeText(String value, String fallback) {
        return value == null || value.trim().isEmpty()
                ? fallback
                : value;
    }

    private String safeMessage(Throwable throwable) {
        if (throwable == null
                || throwable.getMessage() == null
                || throwable.getMessage().trim().isEmpty()) {
            return "未知網絡錯誤";
        }

        return throwable.getMessage();
    }

    private int dpToPx(int dp) {
        return Math.round(
                dp * getResources().getDisplayMetrics().density
        );
    }
}
