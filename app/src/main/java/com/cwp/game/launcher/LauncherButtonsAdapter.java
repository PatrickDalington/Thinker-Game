package com.cwp.game.launcher;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cwp.game.R;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class LauncherButtonsAdapter
        extends RecyclerView.Adapter<LauncherButtonsAdapter.ViewHolder>
        implements View.OnClickListener {

    private static class LauncherItem {
        private final Class<? extends Activity> mActivityClass;
        @StringRes
        private final int mTextRes;

        public LauncherItem(Class<? extends Activity> activityClass, @StringRes int textRes) {
            mActivityClass = activityClass;
            mTextRes = textRes;
        }
    }

    private final Fragment mFragment;
    private final List<LauncherItem> mItems;

    public LauncherButtonsAdapter(Fragment fragment) {
        mFragment = fragment;
        mItems = new ArrayList<>();
    }

    public void put(Class<? extends Activity> activityClass, @StringRes int textRes) {
        mItems.add(new LauncherItem(activityClass, textRes));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_launcher_button, parent, false);
        ViewHolder holder = new ViewHolder(v);
        holder.layout.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LauncherItem item = mItems.get(position);
        holder.mTextView.setText(item.mTextRes); // Set the text

        if (holder.mTextView.getText().equals("ALPHABETS")){
            holder.mImageView.setImageResource(R.drawable.alphabet_game);
        }  else if (holder.mTextView.getText().equals("NUMBERS")){
            holder.mImageView.setImageResource(R.drawable.number_game);
        }else if (holder.mTextView.getText().equals("EMOJI")){
            holder.mImageView.setImageResource(R.drawable.emoji_game);
        } else {
            holder.mImageView.setImageResource(R.drawable.puzzle_game);
        }

        applyZoomAnimation(holder.itemView);

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onClick(@NonNull View v) {
        ViewHolder viewHolder = (ViewHolder) RecyclerViewAdapterUtils.getViewHolder(v);
        int position = viewHolder != null ? viewHolder.getAdapterPosition() : 0;

        Intent intent = new Intent(v.getContext(), mItems.get(position).mActivityClass);
        if (Objects.equals(v.getContext().getResources().getString(mItems.get(position).mTextRes),
                "ALPHABETS")) {
            intent.putExtra("timer", "visible");
        }
        if (Objects.equals(v.getContext().getResources().getString(mItems.get(position).mTextRes), "NUMBERS")) {
            intent.putExtra("timer", "visible");
        }
        mFragment.startActivity(intent);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTextView;
        RelativeLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.gameImage);  // ImageView
            mTextView = itemView.findViewById(R.id.gameText);
            layout = itemView.findViewById(R.id.relative_layout);

        }
    }

    private void applyZoomAnimation(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.05f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.05f);

        scaleX.setDuration(800);
        scaleY.setDuration(800);

        scaleX.setRepeatMode(ObjectAnimator.REVERSE);
        scaleY.setRepeatMode(ObjectAnimator.REVERSE);

        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);

        // Add a fade-in effect
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1000);

        // Use AnimatorSet to combine animations
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        //animatorSet.start();
    }
}
