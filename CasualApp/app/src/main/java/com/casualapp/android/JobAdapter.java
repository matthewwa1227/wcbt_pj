package com.casualapp.android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.casualapp.android.model.Job;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    private List<Job> jobs;
    private OnJobClickListener listener;

    public interface OnJobClickListener {
        void onJobClick(Job job);
    }

    public JobAdapter(List<Job> jobs, OnJobClickListener listener) {
        this.jobs = jobs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job_card, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobs.get(position);
        holder.tvDepartment.setText("九龍酒店1 • 部門" + (position + 1));
        holder.tvJobTitle.setText(job.getTitle());
        holder.tvUpdateDate.setText("更新日期：2023-11-" + (19 - position));

        // Status badge logic
        if (job.isFull() || job.getFilledSlots() >= job.getTotalSlots() - 1) {
            holder.tvStatusBadge.setText("即將滿額");
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_badge_red);
            holder.tvStatusBadge.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvStatusBadge.setText("熱烈招聘");
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_badge_green);
            holder.tvStatusBadge.setTextColor(holder.itemView.getContext().getColor(R.color.primary));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onJobClick(job);
        });
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        ImageView ivJobImage;
        TextView tvDepartment, tvJobTitle, tvUpdateDate, tvStatusBadge;

        JobViewHolder(View itemView) {
            super(itemView);
            ivJobImage = itemView.findViewById(R.id.ivJobImage);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
            tvUpdateDate = itemView.findViewById(R.id.tvUpdateDate);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
        }
    }
}