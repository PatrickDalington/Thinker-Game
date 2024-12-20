/*
 *    Copyright (C) 2016 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.cwp.game.word_game_activity;

import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cwp.game.R;
import com.cwp.game.alphabets_game_activity.CreateSheet;
import com.cwp.game.common.data.AbstractDataProvider;
import com.cwp.game.common.fragment.WordsDataProviderFragment;

public class DraggableStaggeredWordsActivity extends AppCompatActivity implements CreateSheet.PSheetListener {
    private static final String FRAGMENT_TAG_DATA_PROVIDER = "words provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";
    private Intent intent;
    String timer;
    DraggableStaggeredWordsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        fragment = new DraggableStaggeredWordsFragment();

        if (savedInstanceState == null) {
            intent = getIntent();
            if (intent !=null)
            {
                timer = intent.getStringExtra("timer");
                Bundle bundle = new Bundle();
                bundle.putString("time", timer);
                fragment.setArguments(bundle);
            }
            getSupportFragmentManager().beginTransaction()
                    .add(new WordsDataProviderFragment(), FRAGMENT_TAG_DATA_PROVIDER)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DraggableStaggeredWordsFragment(), FRAGMENT_LIST_VIEW)
                    .commit();
        }
    }

    public AbstractDataProvider getDataProvider() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DATA_PROVIDER);
        assert fragment != null;
        return ((WordsDataProviderFragment) fragment).getDataProvider();
    }

    @Override
    public void onGameStart(boolean t) {

    }

    @Override
    public void getGameLevel(String level) {

    }

    @Override
    public void onWatchAiPlay() {

    }

    @Override
    public void getString(String title) {

    }
}
