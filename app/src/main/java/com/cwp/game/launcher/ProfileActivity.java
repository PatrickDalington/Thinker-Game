package com.cwp.game.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cwp.game.R;
import com.cwp.game.common.utils.Badge;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    ImageView profile_image;
    private RecyclerView badgesRecyclerView;
    private CircleImageView profileImage, badgeImage;
    private TextView back, profileName, streakDays, currentLedge, totalCoins, see_all;
    private Spinner levelSpinner;

    private CardView coinCardView, gemCardView, heartCardView;
    private ImageView coinImage, gemImage, heartImage;
    private TextView coinLevel, coinDescription, coinProgressCount, coinProgressText;
    private TextView gemLevel, gemDescription, gemProgressCount, gemProgressText;
    private TextView heartLevel, heartDescription, heartProgressCount, heartProgressText;
    private ProgressBar coinProgressBar, gemProgressBar, heartProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);


        // Calling onBack Function
        OpenOnBackPressed();

        // Bind views
        profileImage = findViewById(R.id.profile_image);
        profileName = findViewById(R.id.profile_name);
        streakDays = findViewById(R.id.streak_days);
        currentLedge = findViewById(R.id.current_ledge);
        totalCoins = findViewById(R.id.total_coins);
        levelSpinner = findViewById(R.id.spinner);
        back = findViewById(R.id.back);

        // See all textview
        see_all = findViewById(R.id.see_all);

        // Reference the views
        coinCardView = findViewById(R.id.card_level_1);
        gemCardView = findViewById(R.id.gem_cardview);
        heartCardView = findViewById(R.id.heart_cardview);

        // ImageViews for the icons
        coinImage = findViewById(R.id.coin);
        gemImage = findViewById(R.id.gem);
        heartImage = findViewById(R.id.heart);

        // TextViews for level, description, and progress
        coinLevel = findViewById(R.id.level_1);
        coinDescription = findViewById(R.id.des_1);
        coinProgressCount = findViewById(R.id.coin_progress_count);
        coinProgressText = findViewById(R.id.coin_progress_text);

        gemLevel = findViewById(R.id.gem_1);
        gemDescription = findViewById(R.id.des_2);
        gemProgressCount = findViewById(R.id.gem_progress_count);
        gemProgressText = findViewById(R.id.gem_progress_text);

        heartLevel = findViewById(R.id.heart_1);
        heartDescription = findViewById(R.id.des_3);
        heartProgressCount = findViewById(R.id.heart_progress_count);
        heartProgressText = findViewById(R.id.heart_progress_text);

        // ProgressBars
        coinProgressBar = findViewById(R.id.game_progress);
        gemProgressBar = findViewById(R.id.gem_progress);
        heartProgressBar = findViewById(R.id.heart_progress);


        // Set values for the progress bars and text (example)
        coinProgressBar.setProgress(40); // Set progress to 40% for coins
        gemProgressBar.setProgress(70); // Set progress to 70% for gems
        heartProgressBar.setProgress(60); // Set progress to 60% for hearts

        // Update progress text and progress counts
        coinProgressText.setText("40%");
        gemProgressText.setText("70%");
        heartProgressText.setText("60%");

        coinProgressCount.setText("4/10");
        gemProgressCount.setText("7/10");
        heartProgressCount.setText("6/10");


        // Bind RecyclerView
        badgesRecyclerView = findViewById(R.id.badges_recyclerview);

        // Prepare badge images (Drawable resource IDs)
        List<Badge> badgeList = Arrays.asList(
                new Badge(R.drawable.badge_1, "Badge 1"),
                new Badge(R.drawable.badge_2, "Badge 2"),
                new Badge(R.drawable.badge_3, "Badge 3"),
                new Badge(R.drawable.badge_4, "Badge 4"),
                new Badge(R.drawable.badge_5, "Badge 5"),
                new Badge(R.drawable.badge_6, "Badge 6")
        );


        // Set up adapter and layout manager
        BadgesAdapter adapter = new BadgesAdapter(this, badgeList);
        badgesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        badgesRecyclerView.setAdapter(adapter);

        see_all.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, Badges.class);
            startActivity(intent);
        });

        back.setOnClickListener(v -> {
            finish();
        });

        setupSpinner();
    }


    private void setupSpinner() {
        // Array of levels
        String[] levels = {"LEVEL 1 - ALPHABET", "LEVEL 2 - ALPHABET"};

        // Create ArrayAdapter for Spinner with custom layouts
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.profile_levels_spinner_items, levels);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        levelSpinner.setAdapter(adapter);
        levelSpinner.setSelection(0); // Set the default selection to the first item

        // Handle selection event
        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateLevelDetails(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void OpenOnBackPressed(){
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(callback);
    }
    private void updateLevelDetails(int level) {
        // Update the views based on the selected level
        if (level == 0) {
            streakDays.setText("25");
            currentLedge.setText("Gold");
            totalCoins.setText("2.5k");
            badgesRecyclerView.scrollToPosition(0);
        } else if (level == 1) {
            streakDays.setText("10");
            currentLedge.setText("Silver");
            totalCoins.setText("1.2k");
            badgesRecyclerView.scrollToPosition(4);
        }
    }
}