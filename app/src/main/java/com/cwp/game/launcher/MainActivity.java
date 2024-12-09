package com.cwp.game.launcher;

import static com.cwp.game.utils.Constants.FRAGMENT_TAG_OPTIONS_MENU;
import static com.cwp.game.utils.Constants.GAME_SETTINGS_PREFS;
import static com.cwp.game.utils.Constants.KEY_CURRENT_USER_ID;
import static com.cwp.game.utils.Constants.KEY_MUSIC_ENABLED;
import static com.cwp.game.utils.Constants.USER_PREFS;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.cwp.game.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private MediaPlayer backgroundMusic;
    private boolean isMusicEnabled;
    private SharedPreferences preferences;
    private SharedPreferences userPreferences;

    private DatabaseReference databaseReference;
    private String currentUserId;

    private List<String> loginDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        loginDates = new ArrayList<>();

        initializeViews();
        initializeMusicPlayer();
        manageUserPreferences();

        currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            trackDaysOnApp();
        } else {
            Toast.makeText(this, "No current user found!", Toast.LENGTH_SHORT).show();
        }

        if (savedInstanceState == null) {
            loadOptionsMenuFragment();
        }
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        initializeTabLayout();
    }

    private void initializeTabLayout() {
        TabLayout tabLayout = findViewById(R.id.tablayout);
        ViewPager pager = findViewById(R.id.viewpager);
        pager.setAdapter(new LauncherPagerAdapter(getSupportFragmentManager()));
    }

    private void initializeMusicPlayer() {
        preferences = getSharedPreferences(GAME_SETTINGS_PREFS, MODE_PRIVATE);
        isMusicEnabled = preferences.getBoolean(KEY_MUSIC_ENABLED, true);

        backgroundMusic = MediaPlayer.create(this, R.raw.game_sound);
        if (backgroundMusic != null) {
            backgroundMusic.setLooping(true);
        }
        manageMusicPlayback(isMusicEnabled);
    }

    private void manageUserPreferences() {
        userPreferences = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        if (getCurrentUserId() == null) {
            addNewUser();
        }
    }

    private void loadOptionsMenuFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(new OptionsMenuFragment(), FRAGMENT_TAG_OPTIONS_MENU)
                .commit();
    }

    private String getCurrentUserId() {
        return userPreferences.getString(KEY_CURRENT_USER_ID, null);
    }

    private void trackDaysOnApp() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        databaseReference.child(currentUserId).child("login_dates").get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                loginDates = (List<String>) dataSnapshot.getValue();
            }

            if (!loginDates.contains(today)) {
                loginDates.add(today);
                updateDaysOnFirebase();
            } else {
                Toast.makeText(this, "Today's login already recorded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to fetch login dates.", Toast.LENGTH_SHORT).show());
    }

    private void addNewUser() {
        currentUserId = UUID.randomUUID().toString();
        String userName = "User_" + currentUserId;



        // Saving to shared preference
        SharedPreferences.Editor editor = userPreferences.edit();
        editor.putString(currentUserId + "_name", userName);
        for (int level = 1; level <= 4; level++) {
            editor.putString(currentUserId + "_Alphabet_Coins_L" + level, "0");
            editor.putString(currentUserId + "_Alphabet_Game_L" + level, level == 1 ? "Ready" : "Not completed");
        }
        editor.putString(KEY_CURRENT_USER_ID, currentUserId);
        editor.apply();


        // Saving to firebase database
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", userName);

        for (int level = 1; level <= 4; level++) {
            userData.put("Alphabet_Coins_L" + level, 0);
            userData.put("Alphabet_Game_L" + level, level == 1 ? "Ready" : "Not completed");
        }

        databaseReference.child(currentUserId).setValue(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "New user added!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to add user.", Toast.LENGTH_SHORT).show();
                    }
                });

        userPreferences.edit().putString(KEY_CURRENT_USER_ID, currentUserId).apply();
    }

    private void updateDaysOnFirebase() {
        int totalDays = loginDates.size();
        databaseReference.child(currentUserId).child("days_on_app").setValue(totalDays);
        databaseReference.child(currentUserId).child("login_dates").setValue(loginDates)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Days on app updated: " + totalDays, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update days on app.", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onStart() {
        super.onStart();
        manageMusicPlayback(isMusicEnabled);
    }

    @Override
    protected void onResume() {
        super.onResume();
        manageMusicPlayback(isMusicEnabled);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMusicPlayer();
    }

    private void manageMusicPlayback(boolean enableMusic) {
        if (enableMusic) {
            startMusic();
        } else {
            pauseMusic();
        }
    }

    private void startMusic() {
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.start();
        }
    }

    private void pauseMusic() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    private void releaseMusicPlayer() {
        if (backgroundMusic != null) {
            backgroundMusic.release();
            backgroundMusic = null;
        }
    }
}
