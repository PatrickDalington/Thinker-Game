package com.cwp.game.alphabets_game_activity;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.NinePatchDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cwp.game.R;
import com.cwp.game.common.data.AbstractDataProvider;
import com.google.android.material.snackbar.Snackbar;
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.util.Locale;
import java.util.Objects;

public class DraggableAlphabetsFragment extends Fragment
        implements LevelChangeListener, DraggableAlphabetsAdapter.ItemClickListener, CoinsUpdateListener {

    private RecyclerView mRecyclerView;
    private DraggableAlphabetsAdapter mAdapter;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private TextView timer, displayText, levelText, game_coin;
    private Toolbar toolbar;
    private ImageView speaker;
    private SharedPreferences preferences;
    private CountDownTimer cTimer = null;
    private TextToSpeech tts;
    private MediaPlayer backgroundMusic;
    private boolean isMusicEnabled, isAlphabetCompleted, isSoundEffectEnabled;
    private String level, time;
    private int coins = 0;
    private RelativeLayout background;
    private LevelChangeListener levelChangeListener;

    public DraggableAlphabetsFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recyclerview_for_alphabet_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setUpRecyclerView(view);
        startBackgroundMusic();

        // Handle speaker click for music toggle
        speaker.setOnClickListener(v -> toggleMusic());


        // Retrieve the level argument from the Bundle
        if (getArguments() != null) {
            level = getArguments().getString("level");
            time = getArguments().getString("time");

            switch (Objects.requireNonNull(level)) {
                case "1":
                    background.setBackgroundResource(R.drawable.alphabet_home_bg);
                    reverseTimer(Integer.parseInt(time));
                    break;
                case "2":
                    background.setBackgroundResource(R.drawable.alphabet_bg_2);
                    reverseTimer(Integer.parseInt(time));
                    break;
                case "3":
                    background.setBackgroundResource(R.drawable.alphabet_bg_3);
                    reverseTimer(Integer.parseInt(time));
                    break;
            }
            //showLevelDialog(level);
        }

        handleOnBackPressed();
    }

    private void showLevelDialog(String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Level")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void initViews(View view) {
        backgroundMusic = new MediaPlayer();
        preferences = requireActivity().getSharedPreferences("game_settings", MODE_PRIVATE);
        isSoundEffectEnabled = preferences.getBoolean("effects_enabled", true);

        toolbar = view.findViewById(R.id.toolbar);
        displayText = view.findViewById(R.id.displayText);
        levelText = view.findViewById(R.id.level);
        timer = view.findViewById(R.id.time);
        game_coin = view.findViewById(R.id.game_coin);
        speaker = view.findViewById(R.id.speaker);
        background = view.findViewById(R.id.alphabet_home_bg);

    }

    private void setUpRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 6));

        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.material_shadow_z3));

        mRecyclerViewDragDropManager.setLongPressTimeout(2);
        mAdapter = new DraggableAlphabetsAdapter(this, getActivity(), getDataProvider(), mRecyclerView, this, isSoundEffectEnabled);
        RecyclerView.Adapter wrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(mAdapter);

        mRecyclerView.setAdapter(wrappedAdapter);
        mRecyclerView.setItemAnimator(new DraggableItemAnimator());
        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);
    }

    private AbstractDataProvider getDataProvider() {
        return ((DraggableAlphabetsActivity) requireActivity()).getDataProvider();
    }

    public void reverseTimer(int seconds) {
        cTimer = new CountDownTimer(seconds * 1000L, 1000) {
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) (millisUntilFinished / 1000);
                timer.setText(String.format(Locale.getDefault(), "%02d:%02d", secondsLeft / 60, secondsLeft % 60));
            }

            public void onFinish() {
                if (isAlphabetCompleted) {
                    timer.setText("Yay 😎");
                    showCompletionDialog(false);
                } else {
                    timer.setText("00:00");
                    showCompletionDialog(true);
                }
            }
        }.start();
    }

    private void showCompletionDialog(boolean isGameOver) {

        GameOverFragment gameOverFragment = new GameOverFragment();
        Bundle bundle = new Bundle();
        bundle.putString("coins", String.valueOf(this.coins));
        gameOverFragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, gameOverFragment, "GAME OVER")
                .commit();

    }

    private void toggleMusic() {
        isMusicEnabled = !isMusicEnabled;
        speaker.setImageResource(isMusicEnabled ? R.drawable.speaker_on : R.drawable.speaker_off);
        if (isMusicEnabled) {
            startBackgroundMusic();
        } else {
            stopBackgroundMusic();
        }
        mAdapter.setMusicEnabled(isMusicEnabled);
    }

    public void startBackgroundMusic() {
        // Initialize MediaPlayer if it's null
        if (backgroundMusic == null) {
            backgroundMusic = new MediaPlayer();
            backgroundMusic = MediaPlayer.create(requireActivity(), R.raw.alphabet_sound);
            backgroundMusic.setLooping(true);  // Ensure it's set to loop
        }

        // Start the music if it's not already playing
        if (!backgroundMusic.isPlaying()) {
            backgroundMusic.start();
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            if (backgroundMusic.isPlaying()) {
                backgroundMusic.stop();
            }
            backgroundMusic.release();
            backgroundMusic = null;  // Reset to null after releasing
        }
    }


    @Override
    public void onDestroyView() {
        mRecyclerViewDragDropManager.release();
        WrapperAdapterUtils.releaseAll(mAdapter);
        stopBackgroundMusic();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        stopBackgroundMusic();
        super.onDetach();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LevelChangeListener) {
            levelChangeListener = (LevelChangeListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement LevelChangeListener");
        }
    }


    private void updateItemMoveMode(boolean swapMode) {
        int mode = swapMode ? RecyclerViewDragDropManager.ITEM_MOVE_MODE_SWAP : RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT;
        mRecyclerViewDragDropManager.setItemMoveMode(mode);
        mAdapter.setItemMoveMode(mode);
        Snackbar.make(requireView(), "Item move mode: " + (swapMode ? "SWAP" : "DEFAULT"), Snackbar.LENGTH_SHORT).show();
    }

    private void handleOnBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), callback);
    }

    @Override
    public void onClick(int position, String value) {
        if (value.equals("completed")) {
            isAlphabetCompleted = true;
            cTimer.cancel();

            // Create GameCompleted fragment with level passed as argument
            GameCompleted gameCompletedFragment = new GameCompleted();
            Bundle bundle = new Bundle();
            bundle.putString("level", level); // Pass level
            bundle.putString("coins", String.valueOf(this.coins)); // Pass coins gained
            gameCompletedFragment.setArguments(bundle);

            // Replace current fragment with GameCompleted fragment
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            levelChangeListener.onCurrentLevel(level); // Notify listener
            transaction.replace(R.id.container, gameCompletedFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            Toast.makeText(requireActivity(), "Current level: " + level, Toast.LENGTH_SHORT).show();

        } else {
            if (value.equals("not completed")) {

                // Passing signal that alphabet order is completed
                isAlphabetCompleted = false;


                //Toast.makeText(requireActivity(), "You did not complete the setting of alphabets!🫤", Toast.LENGTH_SHORT).show();
                tts = new TextToSpeech(getActivity(), i -> {

                    // if No error is found then only it will run
                    if (i != TextToSpeech.ERROR) {
                        // To Choose language of speech
                        tts.setLanguage(Locale.UK);
                    }
                });
                tts.speak(value, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    @Override
    public void showTextForEasyLevel(String alphabets, int currentPosition) {

    }


    @Override
    public void onCoinsUpdate(int coins) {
        this.coins += coins;
        game_coin.setText(String.valueOf(this.coins));
    }

    @Override
    public void onCurrentLevel(String level) {
        Toast.makeText(getActivity(), level, Toast.LENGTH_SHORT).show();
    }
}