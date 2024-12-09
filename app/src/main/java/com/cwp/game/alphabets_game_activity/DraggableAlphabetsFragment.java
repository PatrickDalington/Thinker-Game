package com.cwp.game.alphabets_game_activity;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.content.Context.MODE_PRIVATE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.NinePatchDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.cwp.game.R;
import com.cwp.game.common.data.AbstractDataProvider;
import com.cwp.game.common.data.AlphabetsDataProvider;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DraggableAlphabetsFragment extends Fragment
        implements LevelChangeListener, DraggableAlphabetsAdapter.ItemClickListener, CoinsUpdateListener, DiamondUpdateListener {

    private boolean isUserActive = false;
    private long lastMoveTimestamp = System.currentTimeMillis();
    private int coinsGained = 0;
    private int movesMade = 0;
    private int totalItems = 26; // Total items in the game (A-Z)
    private int correctlyPlacedItems = 0;

    private int secondsLeft;
    private long gameStartTime = System.currentTimeMillis();
    private long maxGameTime = 5 * 60 * 1000; // 5 minutes in milliseconds
    private final int COIN_THRESHOLD = 100; // High coin gain threshold
    private static final String USER_PREFS = "Users" ;
    private static final String DEFAULT_USER_ID = "default_user";
    private static final String PREFS_NAME = "game_settings";
    private static final String DROP_DOWN_NOTIFICATIONS_ENABLED = "drop_notifications_enabled";
    private RecyclerView mRecyclerView;
    private DraggableAlphabetsAdapter mAdapter;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private TextView timer, displayText, levelText, game_coin;
    private Toolbar toolbar;
    private ImageView speaker, pause_game, exit_game;
    private SharedPreferences preferences;
    private CountDownTimer cTimer = null;
    private TextToSpeech tts;
    private MediaPlayer backgroundMusic;
    private boolean isMusicEnabled, isAlphabetCompleted, isSoundEffectEnabled;
    private String level, time;
    private int coins = 0;
    private int diamond = 0;
    private RelativeLayout background;
    private LevelChangeListener levelChangeListener;
    private Boolean refreshOnLoad;
    private String currentUserId;

    private DatabaseReference databaseReference;
    private List<String> loginDates;

    public DraggableAlphabetsFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        loginDates = new ArrayList<>();
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
        //createNotificationChannel();

        // Setting user active
        isUserActive = true;

        // Handle speaker click for music toggle
        speaker.setOnClickListener(v -> toggleMusic());
        pause_game.setOnClickListener(v -> { showPausedScreen(); });



        // Retrieve the level argument from the Bundle
        if (getArguments() != null) {
            level = getArguments().getString("level");
            time = getArguments().getString("time");
            refreshOnLoad = getArguments().getBoolean("refreshOnLoad");

            currentUserId = getCurrentUserId();
            if (currentUserId != null) {
                trackDaysOnApp(level);
            } else {
                Toast.makeText(requireActivity(), "No current user found!", Toast.LENGTH_SHORT).show();
            }

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
        pause_game = view.findViewById(R.id.pause_game);
        exit_game = view.findViewById(R.id.exit_game);




    }


    private void trackDaysOnApp(String level) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        databaseReference.child(currentUserId).child("level_" + level + "_login_dates").get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                loginDates = (List<String>) dataSnapshot.getValue();
            }

            if (!loginDates.contains(today)) {
                loginDates.add(today);
                updateDaysOnFirebase(level);
            } else {
                Toast.makeText(requireActivity(), "Today's login already recorded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(requireActivity(), "Failed to fetch login dates.", Toast.LENGTH_SHORT).show());
    }

    private void updateDaysOnFirebase(String level) {
        int totalDays = loginDates.size();
        databaseReference.child(currentUserId).child("days_on_app_level_" + level).setValue(totalDays);
        databaseReference.child(currentUserId).child("play_dates_for_level_" + level).setValue(loginDates)
                .addOnSuccessListener(unused ->
                        Toast.makeText(requireActivity(), "Days on app updated: " + totalDays, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(requireActivity(), "Failed to update days on app.", Toast.LENGTH_SHORT).show());
    }

    private void setUpRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 6));

        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.material_shadow_z3));

        mRecyclerViewDragDropManager.setLongPressTimeout(2);
        mAdapter = new DraggableAlphabetsAdapter(this, this, getActivity(), getDataProvider(), mRecyclerView, this, isSoundEffectEnabled);
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

            int intervalCounter = 0; // Track intervals for showing notifications

            public void onTick(long millisUntilFinished) {
                secondsLeft = (int) (millisUntilFinished / 1000);
                timer.setText(String.format(Locale.getDefault(), "%02d:%02d", secondsLeft / 60, secondsLeft % 60));

                intervalCounter++;
                // Show motivational notification every 30 seconds
                if (intervalCounter % 30 == 0) {
                    showMotivationalHeadsUpNotification();
                }
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


    private boolean areNotificationsEnabled() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(DROP_DOWN_NOTIFICATIONS_ENABLED, true); // Default is true
    }



    private void showCompletionDialog(boolean isGameOver) {

        GameOverFragment gameOverFragment = new GameOverFragment();
        Bundle bundle = new Bundle();
        bundle.putString("coins", String.valueOf(this.coins));
        bundle.putString("level", level);
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


    private void createNotificationChannel() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "motivational_notifications";
            CharSequence channelName = "Motivational Messages";
            String description = "Notifications to encourage gameplay";
            int importance = NotificationManager.IMPORTANCE_HIGH; // Heads-up notification

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showMotivationalHeadsUpNotification() {
        if (!areNotificationsEnabled()) {
            // Notifications are disabled, do not show
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, exit the method
                Toast.makeText(requireContext(), "Notification permission not granted", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String[] messages = {
                "You're doing amazing! Keep it up! 💪",
                "Fantastic progress! Keep going! 🚀",
                "Almost there! You can do it! 🌟",
                "Coins piling up! Great job! 💰",
                "Time is ticking, but you're crushing it! ⏳",
                "Don't give up! Success is near! 🏆"
        };

        // Select a random message
        String message = messages[(int) (Math.random() * messages.length)];

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "motivational_notifications")
                .setSmallIcon(R.drawable.logo) // Replace with your app's icon
                .setContentTitle("Keep Going!")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority for heads-up
                .setCategory(NotificationCompat.CATEGORY_MESSAGE) // Heads-up category
                .setAutoCancel(true); // Dismiss when tapped

        // Display the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }


    private void showPausedScreen(){
        PauseGameFragment pauseFragment = new PauseGameFragment();
        Bundle bundle = new Bundle();
        bundle.putString("level", level); // Pass level
        bundle.putString("coins", String.valueOf(this.coins)); // Pass coins gained
        pauseFragment.setArguments(bundle);

        updateUserNextLevel(Integer.parseInt(level));

        requireActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.container, pauseFragment)
                .commit();

    }

    @Override
    public void onClick(int position, String value) {

        lastMoveTimestamp = System.currentTimeMillis();

        if (value.equals("completed")) {
            isAlphabetCompleted = true;
            cTimer.cancel();

            VictoryFragment victoryFragment = new VictoryFragment();
            Bundle bundle = new Bundle();
            bundle.putString("level", level); // Pass level
            bundle.putString("coins", String.valueOf(this.coins)); // Pass coins gained
            victoryFragment.setArguments(bundle);

            updateUserNextLevel(Integer.parseInt(level));

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, victoryFragment)
                    .commit();


//            // Create GameCompleted fragment with level passed as argument
//            GameCompleted gameCompletedFragment = new GameCompleted();
//            Bundle bundle = new Bundle();
//            bundle.putString("level", level); // Pass level
//            bundle.putString("coins", String.valueOf(this.coins)); // Pass coins gained
//            gameCompletedFragment.setArguments(bundle);
//
//            // Replace current fragment with GameCompleted fragment
//            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//            levelChangeListener.onCurrentLevel(level); // Notify listener
//            transaction.replace(R.id.container, gameCompletedFragment);
//            transaction.addToBackStack(null);
//            transaction.commit();
//
//            Toast.makeText(requireActivity(), "Current level: " + level, Toast.LENGTH_SHORT).show();

        } else if (value.equals("not completed")) {

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

    private void updateUserNextLevel(int level) {
        SharedPreferences prefs = requireActivity().getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String nextLevelKey;

        String userId = getCurrentUserId();
        String currentLevelKey = userId + "_Alphabet_Game_L" + level;
        nextLevelKey = userId + "_Alphabet_Game_L" + (level + 1);


        String coinsKey = userId + "_Alphabet_Coins_L" + level;
        String diamondKey = userId + "_Alphabet_Diamond_L" + level;

        editor.putString(currentLevelKey, "Completed");
        editor.putString(coinsKey, String.valueOf(this.coins));
        editor.putString(diamondKey, String.valueOf(this.diamond));
        editor.putString(nextLevelKey, "Ready");
        editor.apply();
    }
    private String getCurrentUserId() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        return prefs.getString("current_user_id", DEFAULT_USER_ID);
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
    public void onDiamondUpdate(int diamondCount) {
        this.diamond = diamondCount;
        Toast.makeText(requireActivity(), "You have gained " + this.diamond + " diamonds", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCurrentLevel(String level) {
        Toast.makeText(getActivity(), level, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (refreshOnLoad) {
            // Check if refresh is required
            AlphabetsDataProvider dataProvider = (AlphabetsDataProvider) getDataProvider();
            dataProvider.reloadData();

            // Notify the adapter about the updated data
            if (mAdapter != null) {
                mAdapter.setData(dataProvider);
            }
        }

    }


}