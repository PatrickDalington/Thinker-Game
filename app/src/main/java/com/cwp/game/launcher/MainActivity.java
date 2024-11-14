package com.cwp.game.launcher;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.cwp.game.R;
import com.google.android.material.tabs.TabLayout;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String FRAGMENT_TAG_OPTIONS_MENU = "options_menu";
    private static final String USER_PREFS = "Users";
    private static final String GAME_SETTINGS_PREFS = "game_settings";
    private static final String KEY_MUSIC_ENABLED = "music_enabled";
    private static final String KEY_CURRENT_USER_ID = "current_user_id";

    private Toolbar toolbar;
    private MediaPlayer backgroundMusic;
    private boolean isMusicEnabled;
    private SharedPreferences preferences;
    private SharedPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        initializeMusicPlayer();
        manageUserPreferences();

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

    private void addNewUser() {
        String userId = UUID.randomUUID().toString();
        String userName = "User_" + userId;

        SharedPreferences.Editor editor = userPreferences.edit();
        editor.putString(userId + "_name", userName);

        for (int level = 1; level <= 4; level++) {
            editor.putString(userId + "_Alphabet_Coins_L" + level, "0");
            editor.putString(userId + "_Alphabet_Game_L" + level, level == 1 ? "Ready" : "Not completed");
        }

        editor.putString(KEY_CURRENT_USER_ID, userId);
        editor.apply();
    }

    private String getCurrentUserId() {
        return userPreferences.getString(KEY_CURRENT_USER_ID, null);
    }

    public String getCurrentUserName() {
        String currentUserId = getCurrentUserId();
        return currentUserId != null ? userPreferences.getString(currentUserId + "_name", "Unknown User") : "Unknown User";
    }

    public void switchUser(String newUserId) {
        SharedPreferences.Editor editor = userPreferences.edit();
        editor.putString(KEY_CURRENT_USER_ID, newUserId);
        editor.apply();
        Toast.makeText(this, "Switched to user: " + getCurrentUserName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        manageMusicPlayback(preferences.getBoolean(KEY_MUSIC_ENABLED, true));
    }

    @Override
    protected void onResume() {
        super.onResume();
        manageMusicPlayback(preferences.getBoolean(KEY_MUSIC_ENABLED, true));
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
