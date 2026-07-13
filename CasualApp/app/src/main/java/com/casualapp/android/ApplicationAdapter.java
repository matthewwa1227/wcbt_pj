package com.casualapp.android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.casualapp.android.model.JobSignup;
import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    private List<JobSignup> signups;

    public ApplicationAdapter(List<JobSignup> signups) {
        this.signups = signups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_application_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobSignup signup = signups.get(position);
        
        String jobTitle = signup.getJob() != null ? signup.getJob().getTitle() : "Unknown Job";
        String location = signup.getJob() != null ? signup.getJob().getLocation() : "";
        holder.tvJobTitle.setText(location + " - " + jobTitle);
        
        String jobDate = signup.getJob() != null && signup.getJob().getJobDate() != null 
                ? signup.getJob().getJobDate().substring(0, 10) : "TBD";
        holder.tvDate.setText(jobDate);
        holder.tvTime.setText("13:00 - 00:00"); // TODO: parse from jobDate

        // Status styling
        String status = signup.getStatus();
        switch (status) {
            case "APPROVED":
                holder.tvStatusText.setText("接受");
                holder.tvStatusText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary));
                holder.ivStatusIcon.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                holder.ivStatusIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary));
                holder.statusContainer.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_fixed));
                break;
            case "REJECTED":
                holder.tvStatusText.setText("拒絕");
                holder.tvStatusText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.error));
                holder.ivStatusIcon.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                holder.ivStatusIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.error));
                holder.statusContainer.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.tertiary_fixed));
                break;
            default: // PENDING
                holder.tvStatusText.setText("受理中");
                holder.tvStatusText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.secondary));
                holder.ivStatusIcon.setImageResource(android.R.drawable.ic_menu_recent_history);
                holder.ivStatusIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.secondary));
                holder.statusContainer.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.surface_container_high));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return signups.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJobTitle, tvDate, tvTime, tvStatusText;
        ImageView ivStatusIcon;
        LinearLayout statusContainer;

        ViewHolder(View itemView) {
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatusText = itemView.findViewById(R.id.tvStatusText);
            ivStatusIcon = itemView.findViewById(R.id.ivStatusIcon);
            statusContainer = itemView.findViewById(R.id.statusContainer);
        }
    }
}