package com.cwp.game.alphabets_game_activity;

import static android.content.Context.MODE_PRIVATE;

import static com.cwp.game.utils.Constants.ANIMATION_DELAY_MS;
import static com.cwp.game.utils.Constants.DEFAULT_USER_ID;
import static com.cwp.game.utils.Constants.USER_PREFS;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cwp.game.R;


public class GameOverFragment extends Fragment {


    ImageView home, retry;
    String level, coins, time;

    private ProgressBar scoreProgress;

    private String currentCoins, lv1Coins, lv2Coins, lv3Coins, lv1Diamond, lv2Diamond, lv3Diamond;
    private TextView progressText, diamondText, coinText, heartText;





    public GameOverFragment() {
        // Required empty public constructor
    }


    public static GameOverFragment newInstance(String param1, String param2) {
        GameOverFragment fragment = new GameOverFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_over, container, false);

        initUI(view);
        setupListeners();
        loadCoinsProgress();
        loadDiamondProgress();
        updateDiamondProgress();
        updateCoinProgress();
        updateUserHeartCount();



        // Getting the current level and the coins
        assert getArguments() != null;
        level = getArguments().getString("level");
        this.currentCoins = getArguments().getString("coins");


        animateCoinsCount(safeParseAndAdd(this.currentCoins));

        return view;
    }

    private void initUI(View view){
        home = view.findViewById(R.id.button_home);
        retry = view.findViewById(R.id.button_retry);
        scoreProgress = view.findViewById(R.id.score_progress);
        scoreProgress.setMax(1500);
        progressText = view.findViewById(R.id.score_progress_text);
        diamondText = view.findViewById(R.id.diamond_text);
        coinText = view.findViewById(R.id.coin_text);
        heartText = view.findViewById(R.id.heart_text);
    }

    private void setupListeners() {
        home.setOnClickListener(v -> {
            requireActivity().finish();
        });

        retry.setOnClickListener(v -> {
            DraggableAlphabetsFragment draggableAlphabetsFragment = new DraggableAlphabetsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("level", level);
            // Resetting the coins back to zero
            this.coins = "0";
            bundle.putString("coins", coins);

            // Setting time logic
            if (level.equals("1")) time = "180";
            if (level.equals("2")) time = "120";
            if (level.equals("3")) time = "3";
            bundle.putString("time", time);

            draggableAlphabetsFragment.setArguments(bundle);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, draggableAlphabetsFragment)
                    .commit();
        });
    }

    private void updateUserHeartCount() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(USER_PREFS, MODE_PRIVATE);

        String userId = getCurrentUserId();

        String level = prefs.getString(userId + "_Alphabet_Game_L3", "0");

        if (level.equals("Completed"))
            heartText.setText("1");
    }

    private String getCurrentUserId() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        return prefs.getString("current_user_id", DEFAULT_USER_ID);
    }

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

    private void updateCoinProgress() {
        int progressCount = calculateProgress();
        animateScoreProgress(progressCount);
    }

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
}