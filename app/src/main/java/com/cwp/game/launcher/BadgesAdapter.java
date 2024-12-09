package com.cwp.game.launcher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.cwp.game.R;
import com.cwp.game.common.utils.Badge;

import java.util.List;

public class BadgesAdapter extends RecyclerView.Adapter<BadgesAdapter.BadgeViewHolder> {

    private final List<Badge> badgeList;
    private final Context context;

    // Constructor
    public BadgesAdapter(Context context, List<Badge> badgeList) {
        this.context = context;
        this.badgeList = badgeList;
    }

    @NonNull
    @Override
    public BadgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the custom item layout
        View view = LayoutInflater.from(context).inflate(R.layout.badge_item, parent, false);
        return new BadgeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BadgeViewHolder holder, int position) {
        // Bind badge data to views
        Badge badge = badgeList.get(position);
        holder.badgeImageView.setImageResource(badge.getImageResId());
        holder.badgeNameTextView.setText(badge.getName());
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
