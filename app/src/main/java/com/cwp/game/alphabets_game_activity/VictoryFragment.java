package com.cwp.game.alphabets_game_activity;

import static android.content.Context.MODE_PRIVATE;

import static com.cwp.game.utils.Constants.ANIMATION_DELAY_MS;
import static com.cwp.game.utils.Constants.ARG_COINS;
import static com.cwp.game.utils.Constants.ARG_LEVEL;
import static com.cwp.game.utils.Constants.DEFAULT_USER_ID;
import static com.cwp.game.utils.Constants.USER_PREFS;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.cwp.game.R;
import com.cwp.game.utils.Constants;

public class VictoryFragment extends Fragment {


    private ImageView homeButton, nextButton;
    private ProgressBar scoreProgress;
    private TextView progressText, diamondText, coinText, heartText;

    private int highestGameLevel, currentLevel;
    private String currentCoins, lv1Coins, lv2Coins, lv3Coins, lv1Diamond, lv2Diamond, lv3Diamond;

    public VictoryFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of VictoryFragment.
     */
    public static VictoryFragment newInstance(String level, String coins) {
        VictoryFragment fragment = new VictoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LEVEL, level);
        args.putString(ARG_COINS, coins);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            currentLevel = parseInteger(getArguments().getString(ARG_LEVEL));
            this.currentCoins = getArguments().getString(ARG_COINS, "0");

//            SharedPreferences preferences = requireActivity().getSharedPreferences("Users", MODE_PRIVATE);
//            String userid = getCurrentUserId();
//            String l = preferences.getString(userid + "_Alphabet_Game_L" + currentLevel, "0");
//
//            showLevelDialog(
//                    "Level " + currentLevel + ": " + l
//            );
        }
        highestGameLevel = Constants.HIGHEST_LEVEL_FOR_ALPHABET_GAME;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_victory, container, false);

        initUI(view);
        setupListeners();
        loadCoinsProgress();
        loadDiamondProgress();
        updateDiamondProgress();
        updateCoinProgress();
        updateUserHeartCount();
        animateCoinsCount(safeParseAndAdd(this.currentCoins));

        return view;
    }

    /**
     * Initializes UI elements.
     */
    private void initUI(View view) {
        homeButton = view.findViewById(R.id.button_home);
        nextButton = view.findViewById(R.id.button_next);
        scoreProgress = view.findViewById(R.id.score_progress);
        scoreProgress.setMax(1500);
        progressText = view.findViewById(R.id.score_progress_text);
        diamondText = view.findViewById(R.id.diamond_text);
        coinText = view.findViewById(R.id.coin_text);
        heartText = view.findViewById(R.id.heart_text);
    }

    /**
     * Sets up click listeners for buttons.
     */
    private void setupListeners() {
        homeButton.setOnClickListener(v -> requireActivity().finish());

        nextButton.setOnClickListener(v -> {

                navigateToNextLevel();

        });
    }

    private void showLevelDialog(String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Level")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void updateUserHeartCount() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(USER_PREFS, MODE_PRIVATE);

        String userId = getCurrentUserId();

        String level = prefs.getString(userId + "_Alphabet_Game_L3", "0");

        if (level.equals("Completed"))
            heartText.setText("1");
    }

    /**
     * Navigates to the next game level.
     */
    private void navigateToNextLevel() {
        currentLevel++;
        if (currentLevel <= highestGameLevel) {
            DraggableAlphabetsFragment nextFragment = new DraggableAlphabetsFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ARG_LEVEL, String.valueOf(currentLevel));
            bundle.putString("time", getLevelTime(currentLevel));
            bundle.putBoolean("refreshOnLoad", true);
            nextFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, nextFragment)
                    .commit();
        }
    }

    /**
     * Returns the time limit for a given level.
     */
    private String getLevelTime(int level) {
        return switch (level) {
            case 1 -> "180";
            case 2 -> "120";
            case 3 -> "60";
            default -> "180"; // Default time for safety
        };
    }




    private String getCurrentUserId() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        return prefs.getString("current_user_id", DEFAULT_USER_ID);
    }

    /**
     * Loads coin progress from SharedPreferences.
     */
    private void loadCoinsProgress() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String userId = getCurrentUserId();

        lv1Coins = prefs.getString(userId + "_Alphabet_Coins_L1", "0");
        lv2Coins = prefs.getString(userId + "_Alphabet_Coins_L2", "0");
        lv3Coins = prefs.getString(userId + "_Alphabet_Coins_L3", "0");


    }


    private void loadDiamondProgress() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String userId = getCurrentUserId();

        lv1Diamond = prefs.getString(userId + "_Alphabet_Diamond_L1", "0");
        lv2Diamond = prefs.getString(userId + "_Alphabet_Diamond_L2", "0");
        lv3Diamond = prefs.getString(userId + "_Alphabet_Diamond_L3", "0");
    }

    /**
     * Updates and animates the progress bar.
     */
    private void updateCoinProgress() {
        int progressCount = calculateProgress();
        animateScoreProgress(progressCount);
    }

    /**
     * Calculates the total progress based on collected coins.
     */
    private int calculateProgress() {
        int totalProgress = 0;

        // Add valid, non-zero numbers to the total
        totalProgress += safeParseAndAdd(lv1Coins);
        totalProgress += safeParseAndAdd(lv2Coins);
        totalProgress += safeParseAndAdd(lv3Coins);

        return totalProgress;
    }

    private int calculateTotalDiamond() {
        int totalDiamond = 0;

        // Add valid, non-zero numbers to the total
        totalDiamond += safeParseAndAdd(lv1Diamond);
        totalDiamond += safeParseAndAdd(lv2Diamond);
        totalDiamond += safeParseAndAdd(lv3Diamond);

        return totalDiamond;
    }

    private void updateDiamondProgress() {
        int diamondCount = calculateTotalDiamond();
        animateDiamondCount(diamondCount);
    }

    private int safeParseAndAdd(String value) {
        try {
            if (value != null) {
                int parsedValue = Integer.parseInt(value);
                if (parsedValue > 0) { // Only add positive, non-zero values
                    return parsedValue;
                }
            }
        } catch (NumberFormatException e) {
            // Ignore invalid numbers
        }
        return 0; // Return 0 for invalid, null, or zero values
    }
    /**
     * Animates the progress bar incrementally.
     */
    private void animateScoreProgress(int targetProgress) {
        final int[] progress = {0};
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (progress[0] <= targetProgress) {
                    scoreProgress.setProgress(progress[0]);
                    progressText.setText(progress[0] + "");
                    progress[0]++;
                    handler.postDelayed(this, ANIMATION_DELAY_MS);
                }
            }
        };
        handler.post(runnable);
    }

    private void animateDiamondCount(int targetProgress) {
        final int[] progress = {0};
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (progress[0] <= targetProgress) {
                    diamondText.setText(progress[0] + "");
                    progress[0]++;
                    handler.postDelayed(this, 100);
                }
            }
        };
        handler.post(runnable);
    }


    private void animateCoinsCount(int targetProgress) {
        final int[] progress = {0};
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (progress[0] <= targetProgress) {
                    coinText.setText(progress[0] + "");
                    progress[0]++;
                    handler.postDelayed(this, ANIMATION_DELAY_MS);
                }
            }
        };
        handler.post(runnable);
    }


    /**
     * Safely parses a String to an integer.
     */
    private int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
