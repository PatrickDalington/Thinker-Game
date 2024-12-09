package com.cwp.game.alphabets_game_activity;

import static android.content.Context.MODE_PRIVATE;

import static com.cwp.game.utils.Constants.USER_PREFS;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.cwp.game.R;

public class ChooseGameLevel extends Fragment {

    private ProgressBar gameProgress;
    private TextView progressText;
    private CardView level1Layout, level2Layout, level3Layout;
    private ImageView playLevel1, playLevel2, playLevel3;
    private String lv1Status, lv2Status, lv3Status;
    private RelativeLayout r1, r2, r3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_game_level, container, false);
        initializeViews(view);
        loadLevelProgress();
        setPlayButtonListeners();
        startSideToSideAnimation(view.findViewById(R.id.select_level_icon_layout));
        updateProgress();
        return view;
    }

    private void initializeViews(View view) {
        gameProgress = view.findViewById(R.id.game_progress);
        progressText = view.findViewById(R.id.progress_text);
        level1Layout = view.findViewById(R.id.card_level_1);
        level2Layout = view.findViewById(R.id.card_level_2);
        level3Layout = view.findViewById(R.id.card_level_3);
        playLevel1 = view.findViewById(R.id.play_level_1);
        playLevel2 = view.findViewById(R.id.play_level_2);
        playLevel3 = view.findViewById(R.id.play_level_3);
        r1 = view.findViewById(R.id.r1);
        r2 = view.findViewById(R.id.r2);
        r3 = view.findViewById(R.id.r3);
    }

    private void loadLevelProgress() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String userId = getCurrentUserId();
        lv1Status = prefs.getString(userId + "_Alphabet_Game_L1", "Locked");
        lv2Status = prefs.getString(userId + "_Alphabet_Game_L2", "Locked");
        lv3Status = prefs.getString(userId + "_Alphabet_Game_L3", "Locked");

        setLevelStatus(level1Layout, r1, lv1Status, playLevel1);
        setLevelStatus(level2Layout, r2, lv2Status, playLevel2);
        setLevelStatus(level3Layout, r3, lv3Status, playLevel3);
    }

    private void setLevelStatus(CardView levelLayout, RelativeLayout outline, String status, ImageView playButton) {
        if ("Ready".equals(status) || "Completed".equals(status)) {
            setLevelActive(levelLayout, outline, playButton);
        } else {
            setLevelInactive(levelLayout, outline, playButton);
        }
    }

    private void setLevelActive(CardView levelLayout, RelativeLayout outline, ImageView playButton) {
        levelLayout.setEnabled(true);
        outline.setBackgroundResource(R.drawable.game_level_item_holder_active);
        levelLayout.setBackgroundColor(getResources().getColor(R.color.primary));
        playButton.setAlpha(1f);
        startBreathingAnimation(playButton);
    }

    private void setLevelInactive(CardView levelLayout, RelativeLayout outline, ImageView playButton) {
        levelLayout.setEnabled(false);
        outline.setBackgroundResource(R.drawable.game_level_item_holder_not_active);
        levelLayout.setBackgroundColor(Color.parseColor("#F1F1E9"));
        playButton.setAlpha(0.5f);
        playButton.setOnClickListener(v -> showLockedLevelDialog());
    }

    private void setPlayButtonListeners() {
        playLevel1.setOnClickListener(v -> startLevel("1", lv1Status));
        playLevel2.setOnClickListener(v -> startLevel("2", lv2Status));
        playLevel3.setOnClickListener(v -> startLevel("3", lv3Status));
    }

    private void startLevel(String level, String status) {
        if ("Ready".equals(status) ||  "Completed".equals(status)) {
            String time = "0";
            Intent intent = new Intent(requireActivity(), DraggableAlphabetsActivity.class);
            intent.putExtra("level", level);
            if (level.equals("1")) time = "180";
            if (level.equals("2")) time = "120";
            if (level.equals("3")) time = "3";
            intent.putExtra("timer", time);
            startActivity(intent);
            requireActivity().finish();
        } else {
            Toast.makeText(getActivity(), "Level not unlocked yet", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentUserId() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        return prefs.getString("current_user_id", "default_user");
    }

    private void startSideToSideAnimation(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", -30f, 30f);
        animator.setDuration(1500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.start();
    }

    private void startBreathingAnimation(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.05f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.05f);
        scaleX.setDuration(800);
        scaleY.setDuration(800);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        scaleX.setRepeatMode(ObjectAnimator.REVERSE);
        scaleY.setRepeatMode(ObjectAnimator.REVERSE);
        scaleX.start();
        scaleY.start();
    }

    private void updateProgress() {
        int progressCount = calculateProgress();
        animateProgress(progressCount);
    }

    private int calculateProgress() {
        int progress = 0;
        if ("Completed".equals(lv1Status)) progress += 25;
        if ("Completed".equals(lv2Status)) progress += 35;
        if ("Completed".equals(lv3Status)) progress += 40;
        return progress;
    }

    private void animateProgress(int targetProgress) {
        final int[] progress = {0};
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (progress[0] <= targetProgress) {
                    gameProgress.setProgress(progress[0]);
                    progressText.setText(progress[0] + "%");
                    progress[0] += 1;
                    handler.postDelayed(this, 10);
                }
            }
        };
        handler.post(runnable);
    }

    private void showLockedLevelDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Level Locked")
                .setMessage("Complete previous levels to unlock this level.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        setPlayButtonListeners();
        setLevelStatus(level1Layout, r1, lv1Status, playLevel1);
        setLevelStatus(level2Layout, r2, lv2Status, playLevel2);
        setLevelStatus(level3Layout, r3, lv3Status, playLevel3);
        loadLevelProgress();
        updateProgress();
    }
}
