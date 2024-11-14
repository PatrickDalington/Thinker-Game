package com.cwp.game.alphabets_game_activity;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cwp.game.R;
import com.cwp.game.common.fragment.AlphabetsDataProviderFragment;

public class AlphabetPrelaunchFragment extends Fragment {

    private static final String FRAGMENT_TAG_DATA_PROVIDER = "choose game level";
    ImageView start, practice, choose_mode, watch_ai, close_game;

    public AlphabetPrelaunchFragment() {
        // Required empty public constructor
    }


    public static AlphabetPrelaunchFragment newInstance(String param1, String param2) {
        return new AlphabetPrelaunchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alphabet_prelaunch, container, false);

        start = view.findViewById(R.id.start);
        practice = view.findViewById(R.id.practice);


        start.setOnClickListener(v -> {
            ChooseGameLevel chooseGameLevelFragment = new ChooseGameLevel();

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.container, chooseGameLevelFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        practice.setOnClickListener(v -> {

            // Open ChooseGameLevel fragment when "practice" is clicked


        });




        return view;
    }
}