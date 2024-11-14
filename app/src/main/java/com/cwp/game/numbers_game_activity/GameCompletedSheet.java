package com.cwp.game.numbers_game_activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cwp.game.R;
import com.cwp.game.common.utils.ItemViewModel;
import com.cwp.game.common.utils.Model;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

public class GameCompletedSheet extends BottomSheetDialogFragment {

    private PSheetListener mListener;
    TextView playAgain, playAnotherGame;

    DraggableNumbersFragment fragment;

    Model model;
    ItemViewModel myViewModel;

    String key;


    public interface PSheetListener {
        void onClick();
    }
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_completed_sheet, container, false);


        playAgain = view.findViewById(R.id.play_again);
        playAnotherGame = view.findViewById(R.id.play_another_game);


        /* Getting data using viewModel
        myViewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
            myViewModel.getSelectedItem().observe(requireActivity(), model ->{
                model.setEasyLevel(true);
         });
        */

        fragment = new DraggableNumbersFragment();
        Bundle bundle = new Bundle();

        // Create a new instance of CreateSheet class so that we can access submethods and more
        CreateSheet sheet = new CreateSheet();

        //Identifier for game level key
        key = "level";

        playAgain.setOnClickListener(v -> {
            bundle.putString(key, "normal");
            sheet.setArguments(bundle);
            GameCompletedSheet.this.dismiss();
            sheet.show(requireActivity().getSupportFragmentManager(), "viewing sheet" );
        });


        playAnotherGame.setOnClickListener(v -> {
            bundle.putString(key, "difficult");
            sheet.setArguments(bundle);
            GameCompletedSheet.this.dismiss();
            sheet.show(requireActivity().getSupportFragmentManager(), "viewing sheet" );
        });


        return view;
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof PSheetListener) {
            try {
                mListener = (PSheetListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() + " must implement ButtomSheetListener");
            }
        }


    }

}

