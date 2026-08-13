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

import com.casualapp.android.model.SignupResponse;

import java.util.ArrayList;
import java.util.List;

public class ApplicationAdapter
        extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    private final List<SignupResponse> signups =
            new ArrayList<>();

    public ApplicationAdapter(
            List<SignupResponse> initialSignups
    ) {
        replaceData(initialSignups);
    }

    public void replaceData(
            List<SignupResponse> newSignups
    ) {
        signups.clear();

        if (newSignups != null) {
            signups.addAll(newSignups);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_application_card,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        SignupResponse signup =
                signups.get(position);

        String title = safeText(
                signup.getJobTitle(),
                "Unknown Job"
        );

        String location = safeText(
                signup.getJobLocation(),
                "Location TBD"
        );

        String applicationId =
                signup.getId() != null
                        ? "  •  #" + signup.getId()
                        : "";

        holder.tvJobTitle.setText(
                location
                        + " - "
                        + title
                        + applicationId
        );

        String jobDate =
                signup.getJobDate();

        holder.tvDate.setText(
                JobDateFormatter.formatFullDate(
                        jobDate
                )
        );

        holder.tvTime.setText(
                JobDateFormatter.formatStartTime(
                        jobDate
                )
        );

        bindStatus(
                holder,
                signup.getStatus()
        );
    }

    private void bindStatus(
            ViewHolder holder,
            String rawStatus
    ) {

        String status =
                rawStatus == null
                        ? "PENDING"
                        : rawStatus;

        switch (status) {

            case "APPROVED":

                holder.tvStatusText.setText(
                        "已接受"
                );

                holder.tvStatusText.setTextColor(
                        ContextCompat.getColor(
                                holder.itemView.getContext(),
                                R.color.primary
                        )
                );

                holder.ivStatusIcon.setImageResource(
                        android.R.drawable.ic_menu_save
                );

                holder.ivStatusIcon.setColorFilter(
                        ContextCompat.getColor(
                                holder.itemView.getContext(),
                                R.color.primary
                        )
                );

                holder.statusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                                holder.itemView.getContext(),
                                R.color.primary_fixed
                        )
                );

                break;

            case "REJECTED":

                holder.tvStatusText.setText(
                        "已拒絕"
                );

                holder.tvStatusText.setTextColor(
                        ContextCompat.getColor(
                                holder.itemView.getContext(),
                                R.color.error
                        )
                );

                holder.ivStatusIcon.setImageResource(
                        android.R.drawable
                                .ic_menu_close_clear_cancel
                );

                holder.ivStatusIcon.setColorFilter(
                        ContextCompat.getColor(
                                holder.itemView.getContext(),
                                R.color.error
                        )
                );

                holder.statusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                                holder.itemView.getContext(),
                                R.color.tertiary_fixed
                        )
                );

                break;

            case "CANCELLED":

                holder.tvStatusText.setText(
                        "已取消"
                );

                holder.tvStatusText.setTextColor(
                        ContextCompat.getColor(
                                holder.itemView.getContext(),
                                R.color.secondary
                        )
                );

                holder.ivStatusIcon.setImageResource(
                        android.R.drawable
                                .ic_menu_close_clear_cancel
                );

                holder.ivStatusIcon.setColorFilter(
                        ContextCompat.getColor(
                                holder.itemView.getContext(),
                                R.color.secondary
                        )
                );

                holder.statusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                                holder.itemView.getContext(),
                                R.color.surface_container_high
                        )
                );

                break;

            case "PENDING":
            default:

                holder.tvStatusText.setText(
                        "受理中"
                );

                holder.tvStatusText.setTextColor(
                        ContextCompat.getColor(
                                holder.itemView.getContext(),
                                R.color.secondary
                        )
                );

                holder.ivStatusIcon.setImageResource(
                        android.R.drawable
                                .ic_menu_recent_history
                );

                holder.ivStatusIcon.setColorFilter(
                        ContextCompat.getColor(
                                holder.itemView.getContext(),
                                R.color.secondary
                        )
                );

                holder.statusContainer.setBackgroundColor(
                        ContextCompat.getColor(
                                holder.itemView.getContext(),
                                R.color.surface_container_high
                        )
                );

                break;
        }
    }

    private static String safeText(
            String value,
            String fallback
    ) {

        return value == null
                || value.isBlank()
                ? fallback
                : value;
    }

    @Override
    public int getItemCount() {
        return signups.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvJobTitle;
        TextView tvDate;
        TextView tvTime;
        TextView tvStatusText;

        ImageView ivStatusIcon;
        LinearLayout statusContainer;

        ViewHolder(
                View itemView
        ) {
            super(itemView);

            tvJobTitle =
                    itemView.findViewById(
                            R.id.tvJobTitle
                    );

            tvDate =
                    itemView.findViewById(
                            R.id.tvDate
                    );

            tvTime =
                    itemView.findViewById(
                            R.id.tvTime
                    );

            tvStatusText =
                    itemView.findViewById(
                            R.id.tvStatusText
                    );

            ivStatusIcon =
                    itemView.findViewById(
                            R.id.ivStatusIcon
                    );

            statusContainer =
                    itemView.findViewById(
                            R.id.statusContainer
                    );
        }
    }
}