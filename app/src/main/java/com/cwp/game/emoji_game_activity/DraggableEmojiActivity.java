package com.cwp.game.emoji_game_activity;


import android.content.Intent;
import android.os.Bundle;

import com.cwp.game.R;
import com.cwp.game.common.data.AbstractDataProvider;
import com.cwp.game.common.fragment.EmojisDataProviderFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class DraggableEmojiActivity extends AppCompatActivity implements CreateSheet.PSheetListener{
    private static final String FRAGMENT_TAG_DATA_PROVIDER = "emoji provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";
    Intent intent;
    String timer;
    DraggableEmojiFragment fragment;
    private DraggableEmojiAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        fragment = new DraggableEmojiFragment();

        if (savedInstanceState == null) {
            intent = getIntent();
            if (intent !=null)
            {
                timer = intent.getStringExtra("timer");
            }
            Bundle bundle = new Bundle();
            bundle.putString("time", timer);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(new EmojisDataProviderFragment(), FRAGMENT_TAG_DATA_PROVIDER)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment, FRAGMENT_LIST_VIEW)
                    .commit();
        }


        //Toast.makeText(this, timmer, Toast.LENGTH_SHORT).show();

    }

    public Bundle getData()
    {
        Bundle bundle = new Bundle();
        bundle.putString("time", timer);
        bundle.putString("name", "Patrick");
        return bundle;
    }


    public void reloadFragmentItems() {
        if (fragment != null) {
            fragment.reloadItems();
        }
    }

    public AbstractDataProvider getDataProvider() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DATA_PROVIDER);
        assert fragment != null;
        return ((EmojisDataProviderFragment) fragment).getDataProvider();
    }


    @Override
    public void onGameStart(boolean t) {

        fragment.onGameStart(false);
    }

    @Override
    public void getString(String title)
    {
       fragment.getString(title);
    }


    @Override
    public void getGameLevel(String level) {
        fragment.getGameLevel(level);
    }

    @Override
    public void onWatchAiPlay() {
        fragment.onWatchAiPlay();
    }
}
