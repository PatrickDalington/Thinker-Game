package com.cwp.game.launcher;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cwp.game.R;
import com.cwp.game.common.utils.AllBadges;
import com.cwp.game.common.utils.Badge;

import java.util.Arrays;
import java.util.List;

public class Badges extends AppCompatActivity {

    private RecyclerView badgesRecyclerView;
    TextView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_badges);

        badgesRecyclerView = findViewById(R.id.badges_recycler);
        back = findViewById(R.id.back);

        // Prepare badge images (Drawable resource IDs)
        List<AllBadges> badgeList = Arrays.asList(
                new AllBadges(R.drawable.badge_1, "Badge 1", "Description for Badge 1"),
                new AllBadges(R.drawable.badge_2, "Badge 2", "Description for Badge 2"),
                new AllBadges(R.drawable.badge_3, "Badge 3", "Description for Badge 3"),
                new AllBadges(R.drawable.badge_4, "Badge 4", "Description for Badge 4"),
                new AllBadges(R.drawable.badge_5, "Badge 5", "Description for Badge 5"),
                new AllBadges(R.drawable.badge_6, "Badge 6", "Description for Badge 6"),
                new AllBadges(R.drawable.badge_1, "Badge 1", "Description for Badge 1"),
                new AllBadges(R.drawable.badge_2, "Badge 2", "Description for Badge 2"),
                new AllBadges(R.drawable.badge_3, "Badge 3", "Description for Badge 3"),
                new AllBadges(R.drawable.badge_4, "Badge 4", "Description for Badge 4"),
                new AllBadges(R.drawable.badge_5, "Badge 5", "Description for Badge 5"),
                new AllBadges(R.drawable.badge_6, "Badge 6", "Description for Badge 6"),
                new AllBadges(R.drawable.badge_1, "Badge 1", "Description for Badge 1"),
                new AllBadges(R.drawable.badge_2, "Badge 2", "Description for Badge 2"),
                new AllBadges(R.drawable.badge_3, "Badge 3", "Description for Badge 3"),
                new AllBadges(R.drawable.badge_4, "Badge 4", "Description for Badge 4"),
                new AllBadges(R.drawable.badge_5, "Badge 5", "Description for Badge 5"),
                new AllBadges(R.drawable.badge_6, "Badge 6", "Description for Badge 6"),
                new AllBadges(R.drawable.badge_1, "Badge 1", "Description for Badge 1"),
                new AllBadges(R.drawable.badge_2, "Badge 2", "Description for Badge 2"),
                new AllBadges(R.drawable.badge_3, "Badge 3", "Description for Badge 3"),
                new AllBadges(R.drawable.badge_4, "Badge 4", "Description for Badge 4"),
                new AllBadges(R.drawable.badge_5, "Badge 5", "Description for Badge 5"),
                new AllBadges(R.drawable.badge_6, "Badge 6", "Description for Badge 6"),
                new AllBadges(R.drawable.badge_1, "Badge 1", "Description for Badge 1"),
                new AllBadges(R.drawable.badge_2, "Badge 2", "Description for Badge 2"),
                new AllBadges(R.drawable.badge_3, "Badge 3", "Description for Badge 3"),
                new AllBadges(R.drawable.badge_4, "Badge 4", "Description for Badge 4"),
                new AllBadges(R.drawable.badge_5, "Badge 5", "Description for Badge 5"),
                new AllBadges(R.drawable.badge_6, "Badge 6", "Description for Badge 6")


        );



        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();  // Close the activity
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        back.setOnClickListener(v ->{
            finish();
        });


        // Set up adapter and layout manager
        AllBadgesAdapter  adapter = new AllBadgesAdapter(this, badgeList);
        badgesRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        badgesRecyclerView.setAdapter(adapter);
    }
}