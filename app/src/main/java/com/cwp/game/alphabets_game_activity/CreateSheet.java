package com.cwp.game.alphabets_game_activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cwp.game.R;
import com.cwp.game.common.utils.ItemViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CreateSheet extends BottomSheetDialogFragment {

    private PSheetListener mListener;
    private TextView startButton, practice, changeModeButton, watchAiPlay, closeButton, descriptionTextView;
    private String level;
    private Bundle bundle;

    private ItemViewModel myViewModel;
    private boolean isEasy;

    private DraggableAlphabetsFragment draggableAlphabetFragment;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_sheet_item, container, false);

        initializeViews(view);
        setupBundleData();

        draggableAlphabetFragment = new DraggableAlphabetsFragment();

        setupStartButtonClickListener();
        setupPracticeButtonClickListener();
        setupChangeModeButtonClickListener();
        setupWatchAiPlayButtonClickListener();
        setupCloseButtonClickListener();

        return view;
    }

    private void initializeViews(View view) {
        startButton = view.findViewById(R.id.start);
        practice = view.findViewById(R.id.practice);
        changeModeButton = view.findViewById(R.id.choose_level);
        watchAiPlay = view.findViewById(R.id.play_ai);
        closeButton = view.findViewById(R.id.close_game);
        descriptionTextView = view.findViewById(R.id._tv);
    }

    private void setupBundleData() {
        bundle = getArguments();

        if (bundle != null) {
            String gameOver = bundle.getString("Game Over");
            level = bundle.getString("level");

            if ("yes".equals(gameOver)) {
                startButton.setText("Finish Playing");
            }

            updateDescriptionText();
        } else {
            descriptionTextView.setText("Click start to practice before choosing a game level so that you can get the gist of the game.");
        }
    }

    private void updateDescriptionText() {
        if (level == null) return;

        switch (level) {
            case "easy":
                descriptionTextView.setText("[Easy] Your task is to arrange all letters in alphabetical order within 15 minutes.");
                break;
            case "normal":
                descriptionTextView.setText("[Normal] Your task is to arrange all letters in alphabetical order within 10 minutes.");
                break;
            case "difficult":
                descriptionTextView.setText("[Difficult] Your task is to arrange all letters in alphabetical order within 5 minutes.");
                break;
            default:
                descriptionTextView.setText("Keep it moving, you can do it!!! 😎");
                break;
        }
    }

    private void setupStartButtonClickListener() {
        startButton.setOnClickListener(v ->{
            ChooseGameLevel_ gameLevelFragment = new ChooseGameLevel_();
            gameLevelFragment.show(requireActivity().getSupportFragmentManager(), "Viewing game level");
            dismiss();
        });

    }

    private void setupPracticeButtonClickListener() {
        practice.setOnClickListener(v -> {

        });
    }


    private void setupChangeModeButtonClickListener() {
        changeModeButton.setOnClickListener(v -> {
            ChooseGameMode gameModeFragment = new ChooseGameMode();
            gameModeFragment.show(requireActivity().getSupportFragmentManager(), "Viewing game level");
            dismiss();
        });
    }

    private void setupWatchAiPlayButtonClickListener() {
        watchAiPlay.setOnClickListener(v ->{
            dismiss();
            if (mListener != null) {
                mListener.onWatchAiPlay(); // Callback method
            }
        });
    }

    private void setupCloseButtonClickListener() {
        closeButton.setOnClickListener(v -> {
            dismiss();
            mListener.getString("close");
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof PSheetListener) {
            mListener = (PSheetListener) context;
        } else {
            throw new ClassCastException(context + " must implement PSheetListener");
        }
    }

    public interface PSheetListener {
        void onGameStart(boolean t);
        void getGameLevel(String level);
        void onWatchAiPlay();
        void getString(String title);
    }
}
