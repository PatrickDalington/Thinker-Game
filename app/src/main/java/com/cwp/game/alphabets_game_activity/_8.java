package com.cwp.game.alphabets_game_activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.cwp.game.R;
import com.cwp.game.common.fragment.AlphabetsDataProviderFragment;

public class _8 extends AppCompatActivity {

    AlphabetPrelaunchFragment fragment;
    Intent intent;
    private static final String FRAGMENT_TAG_PRE_LUNCH = "alphabet prelunch";
    private static final String FRAGMENT_LIST_VIEW = "list view";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alphabets_game_prelauch);

        fragment = new AlphabetPrelaunchFragment();

        if (savedInstanceState == null) {
            intent = getIntent();
//            if (intent !=null)
//            {
//                timer = intent.getStringExtra("timer");
//            }
//            Bundle bundle = new Bundle();
//            bundle.putString("time", timer);
//            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(new AlphabetsDataProviderFragment(),FRAGMENT_TAG_PRE_LUNCH)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment, FRAGMENT_LIST_VIEW)
                    .commit();
        }
    }
}