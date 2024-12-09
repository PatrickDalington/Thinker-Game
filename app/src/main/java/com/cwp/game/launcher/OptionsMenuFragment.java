package com.cwp.game.launcher;

import static com.cwp.game.R.*;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cwp.game.R;

import androidx.fragment.app.Fragment;

public class OptionsMenuFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        for(int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#CC8518")), 0,     spanString.length(), 0); //fix the color to white
            item.setTitle(spanString);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startSettingActivity();
                return true;

            case id.profile:
                // Handle instructions action
                Intent intent = new Intent(requireActivity(), ProfileActivity.class);
                startActivity(intent);
                return true;

            case R.id.license:
                // Handle license action
                return true;

            case R.id.about:
                // Handle about action
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startSettingActivity() {
       Intent intent = new Intent(requireActivity(), Settings.class);
       startActivity(intent);
    }
}
