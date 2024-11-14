package com.cwp.game.alphabets_game_activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.cwp.game.R;
import com.cwp.game.common.utils.ItemViewModel;
import com.cwp.game.common.utils.Model;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class ChooseGameLevel_ extends BottomSheetDialogFragment {

    private PSheetListener mListener;
    TextView easy, normal, difficult;
    ImageView back;

    DraggableAlphabetsFragment fragment;

    Model model;
    ItemViewModel myViewModel;

    private BottomSheetBehavior<View> bottomSheetBehavior;
    String key;

    private TextView level1, level2, level3, reward1, reward2, reward3;
    private RelativeLayout level1Container, level2Container, level3Container, board;
    private ImageView cartoon;
    Picasso picasso;

    public interface PSheetListener {
        void onClick();
    }
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_game_level_item, container, false);


        // Connecting views from layout
        level1 = view.findViewById(R.id.level1);
        level2 = view.findViewById(R.id.level2);
        level3 = view.findViewById(R.id.level3);
        cartoon = view.findViewById(R.id.cartoon);
        reward1 = view.findViewById(R.id.reward_1);
        reward2 = view.findViewById(R.id.reward_2);
        reward3 = view.findViewById(R.id.reward_3);
        level1Container = view.findViewById(R.id.level_1);
        level2Container = view.findViewById(R.id.level_2);
        level3Container = view.findViewById(R.id.level_3);
        board = view.findViewById(R.id.board_layout);
        back = view.findViewById(R.id.back);


        //Glide to show gif image
        Glide.with(requireActivity())
                .load(R.drawable.cartoon_female)
                .centerCrop()
                .into(cartoon);

        // Apply animations to views
        applyAnimations();


        fragment = new DraggableAlphabetsFragment();
        Bundle bundle = new Bundle();

        // Create a new instance of CreateSheet class so that we can access submethods and more
        CreateSheet sheet = new CreateSheet();

        //Identifier for game level key
        key = "level";


        back.setOnClickListener(v -> {
            CreateSheet createSheet = new CreateSheet();
            createSheet.show(requireActivity().getSupportFragmentManager(), "Viewing game level");
            dismiss();
        });



        level1Container.setOnClickListener(v -> {
            openLevelDescriptionFragment("level1");
        });

        level2Container.setOnClickListener(v -> {
            openLevelDescriptionFragment("level2");
        });

        level3Container.setOnClickListener(v -> {
            openLevelDescriptionFragment("level3");
        });


        return view;
    }

    private void openLevelDescriptionFragment(String level) {
        if (getActivity() != null) {
            Fragment levelDescriptionFragment = LevelDescriptionFragment.newInstance(level);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, levelDescriptionFragment)
                    .addToBackStack(null)  // Optional to allow back navigation
                    .commit();
        }

        dismiss();
    }


    public void onModeClick(View view) {
        String level = "";
        switch (view.getId()) {
            case R.id.level1:
                level = "level1";
                break;
            case R.id.level2:
                level = "level2";
                break;
            case R.id.level3:
                level = "level3";
                break;
        }

        // Open the LevelDescriptionFragment

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogThemeWithAnimation);

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

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (bottomSheetBehavior != null && bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN); // Close the sheet with animation
                    dismiss(); // Close fragment after animation
                } else {
                    openCreateSheetFragment(); // Fallback behavior
                }
            }
        });


    }

    private void openCreateSheetFragment() {
        CreateSheet sheet = new CreateSheet();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        sheet.show(fragmentManager, "CreateSheet");
    }


    private void applyAnimations() {
        // Slide up the board
        ObjectAnimator slideUpBoard = ObjectAnimator.ofFloat(board, "translationY", 1000f, 0f);
        slideUpBoard.setDuration(300);

        // Fade in the cartoon emoji
        ObjectAnimator fadeInCartoon = ObjectAnimator.ofFloat(cartoon, "alpha", 0f, 1f);
        fadeInCartoon.setDuration(600);

        // Slide in the levels from the right
        ObjectAnimator slideInLevel1 = ObjectAnimator.ofFloat(level1Container, "translationX", 1000f, 0f);
        slideInLevel1.setDuration(600);

        ObjectAnimator slideInLevel2 = ObjectAnimator.ofFloat(level2Container, "translationX", 1000f, 0f);
        slideInLevel2.setDuration(700);

        ObjectAnimator slideInLevel3 = ObjectAnimator.ofFloat(level3Container, "translationX", 1000f, 0f);
        slideInLevel3.setDuration(800);

        // Scaling animation for rewards
        ObjectAnimator scaleReward1 = ObjectAnimator.ofFloat(reward1, "scaleX", 0f, 1f);
        scaleReward1.setDuration(500);
        ObjectAnimator scaleReward1Y = ObjectAnimator.ofFloat(reward1, "scaleY", 0f, 1f);
        scaleReward1Y.setDuration(500);

        ObjectAnimator scaleReward2 = ObjectAnimator.ofFloat(reward2, "scaleX", 0f, 1f);
        scaleReward2.setDuration(500);
        ObjectAnimator scaleReward2Y = ObjectAnimator.ofFloat(reward2, "scaleY", 0f, 1f);
        scaleReward2Y.setDuration(500);

        ObjectAnimator scaleReward3 = ObjectAnimator.ofFloat(reward3, "scaleX", 0f, 1f);
        scaleReward3.setDuration(500);
        ObjectAnimator scaleReward3Y = ObjectAnimator.ofFloat(reward3, "scaleY", 0f, 1f);
        scaleReward3Y.setDuration(500);

        // Start animations
        slideUpBoard.start();
        fadeInCartoon.start();
        slideInLevel1.start();
        slideInLevel2.start();
        slideInLevel3.start();
        scaleReward1.start();
        scaleReward1Y.start();
        scaleReward2.start();
        scaleReward2Y.start();
        scaleReward3.start();
        scaleReward3Y.start();

        slideInLevel1.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeInCartoon.setInterpolator(new OvershootInterpolator());
        scaleReward1.setInterpolator(new BounceInterpolator());

//        AnimatorSet set = new AnimatorSet();
//        set.playSequentially(fadeInCartoon, slideUpBoard, slideInLevel1, slideInLevel2, slideInLevel3);
//        set.start();

    }


    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            View parent = (View) view.getParent();
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED); // Force full expansion
            behavior.setSkipCollapsed(true); // Skip collapsed state to always open fully
        }

        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            assert bottomSheet != null;
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

            // Optional: Set peek height to fill the screen (or part of it)
            bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
        }
    }



}

