package com.cwp.game.alphabets_game_activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cwp.game.R;
import com.cwp.game.common.utils.ItemViewModel;
import com.cwp.game.common.utils.Model;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

public class ChooseGameMode extends BottomSheetDialogFragment {

    private PSheetListener mListener;
    RelativeLayout easy, normal, difficult;

    DraggableAlphabetsFragment fragment;

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
        View view = inflater.inflate(R.layout.choose_game_mode_item, container, false);

        easy = view.findViewById(R.id.easy);
        normal = view.findViewById(R.id.normal);
        difficult = view.findViewById(R.id.hard);


        /* Getting data using viewModel
        myViewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
            myViewModel.getSelectedItem().observe(requireActivity(), model ->{
                model.setEasyLevel(true);
         });
        */

        fragment = new DraggableAlphabetsFragment();
        Bundle bundle = new Bundle();

        // Create a new instance of CreateSheet class so that we can access submethods and more
        CreateSheet sheet = new CreateSheet();

        //Identifier for game level key
        key = "level";
        easy.setOnClickListener(v -> {
            ChooseGameMode.this.dismiss();
            bundle.putString(key, "easy");
            sheet.setArguments(bundle);
            sheet.show(requireActivity().getSupportFragmentManager(), "viewing sheet" );
        });

        normal.setOnClickListener(v -> {
            bundle.putString(key, "normal");
            sheet.setArguments(bundle);
            ChooseGameMode.this.dismiss();
            sheet.show(requireActivity().getSupportFragmentManager(), "viewing sheet" );
        });


        difficult.setOnClickListener(v -> {
            bundle.putString(key, "difficult");
            sheet.setArguments(bundle);
            ChooseGameMode.this.dismiss();
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

