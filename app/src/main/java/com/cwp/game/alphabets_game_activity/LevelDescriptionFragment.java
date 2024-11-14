package com.cwp.game.alphabets_game_activity;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cwp.game.R;

public class LevelDescriptionFragment extends Fragment {

    private static final String ARG_LEVEL = "level";

    public static LevelDescriptionFragment newInstance(String level) {
        LevelDescriptionFragment fragment = new LevelDescriptionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LEVEL, level);
        fragment.setArguments(args);
        return fragment;
    }

    private String level;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            level = getArguments().getString(ARG_LEVEL);
        }

        // Register a callback for when the back button is pressed
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                CreateSheet createSheet = new CreateSheet();
                createSheet.show(requireActivity().getSupportFragmentManager(), "Viewing game level");
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level_description, container, false);

        ImageView levelGif = view.findViewById(R.id.levelGif);
        TextView levelDescription = view.findViewById(R.id.levelDescription);
        Button startGameButton = view.findViewById(R.id.startGameButton);

        switch (level) {
            case "level1":
                levelGif.setImageResource(R.drawable.cartoon_female); // Replace with your easy mode GIF
                levelDescription.setText("This is easy mode. Enjoy a relaxed pace!");
                break;
            case "level2":
                levelGif.setImageResource(R.drawable.cartoon);
                levelDescription.setText("This is normal mode. Get ready for a challenge!");
                break;
            case "level3":
                levelGif.setImageResource(R.drawable.cartoon_female);
                levelDescription.setText("This is hard mode. Only the brave survive!");
                break;
        }

        startGameButton.setOnClickListener(v -> {
            // Add logic to start the game
        });


        return view;
    }
}

