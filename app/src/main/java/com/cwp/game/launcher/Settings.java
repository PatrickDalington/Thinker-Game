package com.cwp.game.launcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cwp.game.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class Settings extends AppCompatActivity {

    private static final String PREFS_NAME = "game_settings";
    private static final String KEY_NOTIFICATIONS_ENABLED = "drop_notifications_enabled";
    Button back_home;
    SwitchMaterial drop_down_notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        back_home = findViewById(R.id.back_home);
        SwitchMaterial switchMusic = findViewById(R.id.switch_music);
        SwitchMaterial switchSoundEffect = findViewById(R.id.switch_sound);
        drop_down_notification = findViewById(R.id.switch_drop_notifications);

        back_home.setOnClickListener(v->{
            finish();
        });

        // Load saved switch state (enabled/disabled)
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isMusicEnabled = preferences.getBoolean("music_enabled", true);
        boolean isSoundEffectEnabled = preferences.getBoolean("effects_enabled", true);
        switchMusic.setChecked(isMusicEnabled);
        switchSoundEffect.setChecked(isSoundEffectEnabled);


        switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences("game_settings", MODE_PRIVATE).edit();
            editor.putBoolean("music_enabled", isChecked);
            editor.apply();

        });

        switchSoundEffect.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences("game_settings", MODE_PRIVATE).edit();
            editor.putBoolean("effects_enabled", isChecked);
            editor.apply();
        }));


        drop_down_notification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setDropNotificationsEnabled(isChecked);
        });



        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();  // Close the activity
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void setDropNotificationsEnabled(boolean isEnabled) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_NOTIFICATIONS_ENABLED, isEnabled);
        editor.apply();
    }
}