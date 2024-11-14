package com.cwp.game.word_game_activity;

import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cwp.game.R;
import com.cwp.game.alphabets_game_activity.CreateSheet;
import com.cwp.game.common.data.AbstractDataProvider;
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.Objects;

public class DraggableStaggeredWordsFragment extends Fragment
        implements DraggableStaggeredWordsAdapter.ItemClickListener,
        DraggableStaggeredWordsAdapter.ScoreUpdateListener, CreateSheet.PSheetListener {

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private Button next;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private TextView level, scores;
    private Toolbar toolbar;
    Bundle bundle;

    // Constructor
    public DraggableStaggeredWordsFragment() {
        super();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.fragment_recycler_list_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views and setup listeners
        initializeViews(requireView());
        setupRecyclerView(requireView());
        setupButtonListener();

        // Showing bottom fragment for user to choose preferred choice
        CreateSheet preGame = new CreateSheet();
        preGame.show(requireActivity().getSupportFragmentManager(),"Viewing...");

        toolbar = requireView().findViewById(R.id.toolbar);

        //activity = new DraggableGridExampleActivity();
        bundle = getArguments();

        if (bundle != null) {
            Toast.makeText(getActivity(), "Bundle is not null", Toast.LENGTH_SHORT).show();
            if (Objects.equals(bundle.getString("time"), "visible"))
                toolbar.setVisibility(View.VISIBLE);
        }

    }

    private void initializeViews(View view) {
        // Initialize button and set its initial state
        next = view.findViewById(R.id.next);
        next.setVisibility(View.INVISIBLE);// Initially hide the button




        level = view.findViewById(R.id.current_level);

        scores = view.findViewById(R.id.current_score);

        TextView timer = view.findViewById(R.id.time);
        timer.setVisibility(View.VISIBLE);

        RelativeLayout game_board = view.findViewById(R.id.score_board);
        game_board.setVisibility(View.VISIBLE);
    }

    private void setupButtonListener() {
        next.setOnClickListener(v -> {
            // Handle button click logic
        });
    }

    private void setupRecyclerView(View view) {
        // Initialize RecyclerView and layout manager
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mLayoutManager = new StaggeredGridLayoutManager(8, StaggeredGridLayoutManager.VERTICAL);

        // Initialize Drag & Drop manager
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        setupDragDropManager();

        // Setup adapter
        DraggableStaggeredWordsAdapter myItemAdapter =
                new DraggableStaggeredWordsAdapter(getActivity(), getDataProvider(), this, this);
        mAdapter = myItemAdapter;
        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(myItemAdapter); // Wrap for dragging

        // Set item animator
        GeneralItemAnimator animator = new DraggableItemAnimator();

        // Apply configurations to RecyclerView
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);
        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.setHasFixedSize(false);

        // Add item decoration for non-elevation views
        addItemDecorationIfNeeded();

        // Attach Drag & Drop manager to RecyclerView
        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);
    }

    private void setupDragDropManager() {
        // Configure drag & drop manager
        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.material_shadow_z3));
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);  // Start dragging on long press
        mRecyclerViewDragDropManager.setInitiateOnMove(false);
        mRecyclerViewDragDropManager.setLongPressTimeout(5);
    }

    private void addItemDecorationIfNeeded() {
        // Add shadow decoration for non-elevation supported devices
        if (!supportsViewElevation()) {
            NinePatchDrawable shadowDrawable = (NinePatchDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.material_shadow_z1);
            mRecyclerView.addItemDecoration(new ItemShadowDecorator(Objects.requireNonNull(shadowDrawable)));
        }
    }

    @Override
    public void onPause() {
        // Cancel ongoing drag operation when fragment pauses
        mRecyclerViewDragDropManager.cancelDrag();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        // Release resources when the view is destroyed
        cleanupResources();
        super.onDestroyView();
    }

    private void cleanupResources() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.release();
            mRecyclerViewDragDropManager = null;
        }

        if (mRecyclerView != null) {
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }

        mAdapter = null;
        mLayoutManager = null;
    }

    private boolean supportsViewElevation() {
        // Use view elevation for Lollipop and above
        return true;
    }

    public AbstractDataProvider getDataProvider() {
        // Retrieve data provider from the parent activity
        return ((DraggableStaggeredWordsActivity) requireActivity()).getDataProvider();
    }

    @Override
    public void onClick(int position, boolean showButton) {
        // Handle item click events
        if (showButton) {
            Toast.makeText(getActivity(), "Show Button", Toast.LENGTH_SHORT).show();
            next.setVisibility(View.VISIBLE);  // Show the button when requested
        }
    }

    @Override
    public void onScoreUpdated(int newScore) {
        scores.setText(String.valueOf(newScore));
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
