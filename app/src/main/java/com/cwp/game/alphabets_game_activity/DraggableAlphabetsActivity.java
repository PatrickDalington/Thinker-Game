package com.cwp.game.alphabets_game_activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.cwp.game.R;
import com.cwp.game.common.data.AbstractDataProvider;
import com.cwp.game.common.fragment.AlphabetsDataProviderFragment;

public class DraggableAlphabetsActivity extends AppCompatActivity implements LevelChangeListener, CreateSheet.PSheetListener {
    private static final String FRAGMENT_TAG_DATA_PROVIDER = "alphabet_provider";
    private static final String EXTRA_TIMER = "timer";
    private static final String EXTRA_LEVEL = "level";

    private String timer;
    private String level;
    private DraggableAlphabetsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        if (savedInstanceState == null) {
            initializeIntentData();
            attachDataProviderFragment();
            attachDraggableAlphabetsFragment();
        }
    }

    private void initializeIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            timer = intent.getStringExtra(EXTRA_TIMER);
            level = intent.getStringExtra(EXTRA_LEVEL);
        }
    }

    private void attachDataProviderFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(new AlphabetsDataProviderFragment(), FRAGMENT_TAG_DATA_PROVIDER)
                .commit();
    }

    private void attachDraggableAlphabetsFragment() {
        fragment = new DraggableAlphabetsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("time", timer);
        bundle.putString("level", level);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment, "FRAGMENT_LIST_VIEW")
                .commit();
    }

    public Bundle getData() {
        Bundle bundle = new Bundle();
        bundle.putString("time", timer);
        bundle.putString("name", "Patrick");
        return bundle;
    }

    public AbstractDataProvider getDataProvider() {
        Fragment dataProviderFragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DATA_PROVIDER);
        if (dataProviderFragment instanceof AlphabetsDataProviderFragment) {
            return ((AlphabetsDataProviderFragment) dataProviderFragment).getDataProvider();
        }
        throw new IllegalStateException("Data provider fragment not found");
    }

    @Override
    public void onGameStart(boolean t) {
        // Implement game start logic if necessary
    }

    @Override
    public void getString(String title) {
        // Implement title update logic if necessary
    }

    @Override
    public void getGameLevel(String level) {
        // Implement level update logic if necessary
    }

    @Override
    public void onWatchAiPlay() {
        // Implement AI play logic if necessary
    }

    @Override
    public void onCurrentLevel(String level) {
        // Implement level change logic if necessary
    }
}
