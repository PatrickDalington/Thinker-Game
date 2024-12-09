package com.cwp.game.launcher;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cwp.game.R;
import com.cwp.game.common.utils.AllBadges;
import com.cwp.game.common.utils.Badge;

import java.util.List;

public class AllBadgesAdapter extends RecyclerView.Adapter<AllBadgesAdapter.BadgeViewHolder> {

    private final List<AllBadges> badgeList;
    private final Context context;

    // Constructor
    public AllBadgesAdapter(Context context, List<AllBadges> badgeList) {
        this.context = context;
        this.badgeList = badgeList;
    }

    @NonNull
    @Override
    public BadgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the custom item layout
        View view = LayoutInflater.from(context).inflate(R.layout.all_badge_item, parent, false);
        return new BadgeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BadgeViewHolder holder, int position) {
        // Bind badge data to views
        AllBadges badge = badgeList.get(position);
        holder.badgeImageView.setImageResource(badge.getImageResId());
        holder.badgeNameTextView.setText(badge.getName());

        holder.itemView.setOnClickListener(v -> {
            showBadgeDescriptionDialog(badge.getName(), badge.getDescription());
        });
    }

    private void showBadgeDescriptionDialog(String title, String description) {
        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(description)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public int getItemCount() {
        return badgeList.size();
    }

    // ViewHolder class
    public static class BadgeViewHolder extends RecyclerView.ViewHolder {
        ImageView badgeImageView;
        TextView badgeNameTextView;

        public BadgeViewHolder(@NonNull View itemView) {
            super(itemView);
            badgeImageView = itemView.findViewById(R.id.badge_image);
            badgeNameTextView = itemView.findViewById(R.id.badge_name);
        }
    }
}
