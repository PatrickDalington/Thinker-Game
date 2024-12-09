package com.cwp.game.alphabets_game_activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.cwp.game.R;

public class PauseGameFragment extends Fragment {

    RelativeLayout mainFrame;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pause_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainFrame = view.findViewById(R.id.main_frame);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mainFrame.getForeground() == null) {
                // Set a default semi-transparent foreground color
                mainFrame.setForeground(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.semi_transparent)));
            }
            mainFrame.getForeground().setAlpha(220); // Adjust transparency
        }

    }
}
