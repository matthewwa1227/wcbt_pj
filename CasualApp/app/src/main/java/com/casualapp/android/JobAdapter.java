package com.casualapp.android;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.casualapp.android.model.Job;

import java.util.ArrayList;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    private final List<Job> jobs;
    private final OnJobClickListener listener;

    public interface OnJobClickListener {
        void onJobClick(Job job);
    }

    public JobAdapter(
            List<Job> jobs,
            OnJobClickListener listener
    ) {
        this.jobs =
                jobs != null
                        ? jobs
                        : new ArrayList<>();

        this.listener = listener;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_job_card,
                                parent,
                                false
                        );

        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull JobViewHolder holder,
            int position
    ) {

        Job job = jobs.get(position);

        holder.tvDepartment.setText(
                buildLocationText(job)
        );

        holder.tvJobTitle.setText(
                safeText(
                        job.getTitle(),
                        "未命名職位"
                )
        );

        holder.tvUpdateDate.setText(
                buildDateAndSlotsText(job)
        );

        bindStatusBadge(
                holder,
                job
        );

        holder.itemView.setOnClickListener(v -> {

            if (listener != null) {
                listener.onJobClick(job);
                return;
            }

            /*
             * Fallback for screens that do not provide
             * their own click listener.
             */
            Intent intent =
                    new Intent(
                            holder.itemView.getContext(),
                            JobDetailActivity.class
                    );

            intent.putExtra(
                    "job",
                    job
            );

            holder.itemView
                    .getContext()
                    .startActivity(intent);
        });
    }

    private String buildLocationText(
            Job job
    ) {

        String location =
                safeText(
                        job.getLocation(),
                        "未知地點"
                );

        String coordinatorName =
                job.getCoordinatorName();

        if (coordinatorName != null
                && !coordinatorName.trim().isEmpty()) {

            return location
                    + " • "
                    + coordinatorName;
        }

        return location;
    }

    private String buildDateAndSlotsText(
            Job job
    ) {

        String date =
                formatDate(
                        job.getStartDateTime()
                );

        return "日期："
                + date
                + "  |  名額："
                + job.getFilledSlots()
                + "/"
                + job.getTotalSlots();
    }

    private void bindStatusBadge(
            JobViewHolder holder,
            Job job
    ) {

        int totalSlots =
                job.getTotalSlots();

        int filledSlots =
                job.getFilledSlots();

        if (totalSlots <= 0) {

            holder.tvStatusBadge.setText(
                    "未開放"
            );

            holder.tvStatusBadge.setBackgroundResource(
                    R.drawable.bg_status_badge_red
            );

            holder.tvStatusBadge.setTextColor(
                    ContextCompat.getColor(
                            holder.itemView.getContext(),
                            android.R.color.holo_red_dark
                    )
            );

            return;
        }

        int remainingSlots =
                totalSlots - filledSlots;

        if (job.isFull()
                || remainingSlots <= 0) {

            holder.tvStatusBadge.setText(
                    "已滿額"
            );

            holder.tvStatusBadge.setBackgroundResource(
                    R.drawable.bg_status_badge_red
            );

            holder.tvStatusBadge.setTextColor(
                    ContextCompat.getColor(
                            holder.itemView.getContext(),
                            android.R.color.holo_red_dark
                    )
            );

        } else if (remainingSlots == 1) {

            holder.tvStatusBadge.setText(
                    "剩餘1位"
            );

            holder.tvStatusBadge.setBackgroundResource(
                    R.drawable.bg_status_badge_red
            );

            holder.tvStatusBadge.setTextColor(
                    ContextCompat.getColor(
                            holder.itemView.getContext(),
                            android.R.color.holo_red_dark
                    )
            );

        } else {

            holder.tvStatusBadge.setText(
                    "熱烈招聘"
            );

            holder.tvStatusBadge.setBackgroundResource(
                    R.drawable.bg_status_badge_green
            );

            holder.tvStatusBadge.setTextColor(
                    ContextCompat.getColor(
                            holder.itemView.getContext(),
                            R.color.primary
                    )
            );
        }
    }

    private String formatDate(
            String rawDate
    ) {

        if (rawDate == null
                || rawDate.trim().isEmpty()) {

            return "未設定";
        }

        /*
         * Backend LocalDateTime:
         *
         * 2026-09-05T18:00:00
         *
         * Display:
         *
         * 2026-09-05
         */
        int separator =
                rawDate.indexOf("T");

        if (separator > 0) {
            return rawDate.substring(
                    0,
                    separator
            );
        }

        return rawDate;
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

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    static class JobViewHolder
            extends RecyclerView.ViewHolder {

        ImageView ivJobImage;

        TextView tvDepartment;
        TextView tvJobTitle;
        TextView tvUpdateDate;
        TextView tvStatusBadge;

        JobViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            ivJobImage =
                    itemView.findViewById(
                            R.id.ivJobImage
                    );

            tvDepartment =
                    itemView.findViewById(
                            R.id.tvDepartment
                    );

            tvJobTitle =
                    itemView.findViewById(
                            R.id.tvJobTitle
                    );

            tvUpdateDate =
                    itemView.findViewById(
                            R.id.tvUpdateDate
                    );

            tvStatusBadge =
                    itemView.findViewById(
                            R.id.tvStatusBadge
                    );
        }
    }
}