package com.cwp.game.alphabets_game_activity;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.cwp.game.R;
import android.animation.ValueAnimator;

import java.text.NumberFormat;
import java.util.Locale;

public class GameCompleted extends Fragment implements LevelChangeListener {
    private static final String ARG_LEVEL = "level";
    private String level, myCoins;
    private ImageView levels, next_level_btn;
    private TextView coin;

    public static GameCompleted newInstance(String level) {
        GameCompleted fragment = new GameCompleted();
        Bundle args = new Bundle();
        args.putString(ARG_LEVEL, level);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            level = getArguments().getString(ARG_LEVEL);
            myCoins = getArguments().getString("coins");
            if (level != null) {
                updateUserNextLevel(level);
            }
        }
    }

    private void updateUserNextLevel(String level) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("Users", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String userNextLevel = getCurrentUserId() + "_Alphabet_Game_L" + (Integer.parseInt(level) + 1);
        String userCurrentLevel = getCurrentUserId() + "_Alphabet_Game_L" + (Integer.parseInt(level));
        String userCoin = getCurrentUserId() + "_Alphabet_Coins_L" + (Integer.parseInt(myCoins));
        editor.putString(userCurrentLevel, "Completed");
        editor.putString(userNextLevel, "Ready").apply();
    }

    private String getCurrentUserId() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("Users", MODE_PRIVATE);
        return prefs.getString("current_user_id", "default_user");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_completed, container, false);
        levels = view.findViewById(R.id.levels);
        coin = view.findViewById(R.id.coins);
        next_level_btn = view.findViewById(R.id.next_level_button);

        Toast.makeText(getActivity(), "Level completed: " + level, Toast.LENGTH_SHORT).show();
        if (level != null) {
            if (level.equals("2"))
            {
                levels.setImageResource(R.drawable.level_2);
            }
            else if (level.equals("3"))
            {
                levels.setImageResource(R.drawable.level_3);
            }
        }

        next_level_btn.setOnClickListener(v -> {
            ChooseGameLevel chooseGameLevel = new ChooseGameLevel();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, chooseGameLevel)
                    .commit();
        });

        // Show animated coins gained
        coinsCounter(Integer.parseInt(myCoins));
        return view;
    }

    public void coinsCounter(int targetCoins) {
        ValueAnimator animator = ValueAnimator.ofInt(0, targetCoins);
        animator.setDuration(2000);  // Adjust duration as desired for the animation speed
        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            // Format the number with commas
            String formattedValue = NumberFormat.getNumberInstance(Locale.US).format(animatedValue);
            coin.setText(formattedValue);
        });
        animator.start();
    }


    @Override
    public void onCurrentLevel(String level) {
        // Handle current level change logic if necessary
    }
}
