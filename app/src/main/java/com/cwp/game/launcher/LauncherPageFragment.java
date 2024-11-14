/*
 *    Copyright (C) 2015 Haruki Hasegawa
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

package com.cwp.game.launcher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.cwp.game.R;
import com.cwp.game.alphabets_game_activity._8;
import com.cwp.game.emoji_game_activity.DraggableEmojiActivity;
import com.cwp.game.numbers_game_activity.DraggableNumbersActivity;
import com.cwp.game.word_game_activity.DraggableStaggeredWordsActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LauncherPageFragment extends Fragment {
    private static final String ARG_PAGE_NO = "page no";

    public static final int PAGE_DRAG = 0;
    public static final int PAGE_SWIPE = 1;
    public static final int PAGE_EXPAND = 2;
    public static final int PAGE_HEADER = 3;
    public static final int PAGE_ADAPTER = 4;
    public static final int PAGE_ADVANCED = 5;

    public static final int NUM_PAGES = 6;

    RecyclerView recyclerView;


    public static LauncherPageFragment newInstance(int pageNo) {
        LauncherPageFragment fragment = new LauncherPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NO, pageNo);
        fragment.setArguments(args);
        return fragment;
    }

    public LauncherPageFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_list_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        int pageNo = getArguments().getInt(ARG_PAGE_NO);

        LauncherButtonsAdapter adapter = createAdapter(pageNo);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);

    }

    private void applyLayoutAnimation() {
        // Load the layout animation from resources
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_slide_in_right);

        // Set the layout animation to the RecyclerView
        recyclerView.setLayoutAnimation(layoutAnimationController);

        // Optionally, start the animation
        recyclerView.scheduleLayoutAnimation();
    }

    public static String getPageTitle(int pageNo) {
        switch (pageNo) {
            case PAGE_DRAG:
                return "Drag";
            case PAGE_SWIPE:
                return "Swipe";
            case PAGE_EXPAND:
                return "Expand";
            case PAGE_HEADER:
                return "Header";
            case PAGE_ADAPTER:
                return "Adapter";
            case PAGE_ADVANCED:
                return "Advanced";
            default:
                throw new IllegalArgumentException();
        }
    }

    private LauncherButtonsAdapter createAdapter(int pageNo) {
        LauncherButtonsAdapter adapter = new LauncherButtonsAdapter(this);

        switch (pageNo) {
            case PAGE_DRAG:
                // Drag
                //adapter.put(MinimalDraggableExampleActivity.class, R.string.activity_title_demo_d_minimal);
                //adapter.put(DraggableExampleActivity.class, R.string.activity_title_demo_d_basic);
                //adapter.put(DragOnLongPressExampleActivity.class, R.string.activity_title_demo_d_on_longpress);
                //adapter.put(DraggableWithSectionExampleActivity.class, R.string.activity_title_demo_d_with_section);
                //adapter.put(PuzzleActivity.class, R.string.activity_title_demo_d_check_can_drop);
                adapter.put(_8.class, R.string.activity_title_alphabet);
                adapter.put(DraggableNumbersActivity.class, R.string.activity_title_number);
                adapter.put(DraggableEmojiActivity.class, R.string.activity_title_emoji);
                adapter.put(DraggableStaggeredWordsActivity.class, R.string.activity_title_alphabets_ordering);
                break;
            case PAGE_SWIPE:
                // Swipe
                //adapter.put(MinimalSwipeableExampleActivity.class, R.string.activity_title_demo_s_minimal);
                //adapter.put(SwipeableExampleActivity.class, R.string.activity_title_demo_s_basic);
                //adapter.put(SwipeOnLongPressExampleActivity.class, R.string.activity_title_demo_s_on_longpress);
                //adapter.put(SwipeableWithButtonExampleActivity.class, R.string.activity_title_demo_us);
                //adapter.put(VerticalSwipeableExampleActivity.class, R.string.activity_title_demo_s_vertical);
                //adapter.put(ViewPagerSwipeableExampleActivity.class, R.string.activity_title_demo_s_viewpager);
                break;
            case PAGE_EXPAND:
                // Expand
                //adapter.put(MinimalExpandableExampleActivity.class, R.string.activity_title_demo_e_minimal);
                //adapter.put(ExpandableExampleActivity.class, R.string.activity_title_demo_e_basic);
                //adapter.put(AddRemoveExpandableExampleActivity.class, R.string.activity_title_demo_e_add_remove);
                //adapter.put(AlreadyExpandedGroupsExpandableExampleActivity.class, R.string.activity_title_demo_e_already_expanded);
                break;
            case PAGE_HEADER:
                // Headers/Footers
                //adapter.put(MinimalHeaderFooterExampleActivity.class, R.string.activity_title_demo_hf_minimal);
                //adapter.put(ExpandableWithHeaderFooterExampleActivity.class, R.string.activity_title_demo_hf_e);
                //adapter.put(AddRemoveHeaderFooterExampleActivity.class, R.string.activity_title_demo_hf_add_remove);
                break;
            case PAGE_ADAPTER:
                // Adapter
                //adapter.put(CompositionAllExampleActivity.class, R.string.activity_title_demo_composition_all);
                //adapter.put(CustomInsertionWrapperAdapterExampleActivity.class, R.string.activity_title_demo_wa_insertion);
                //adapter.put(CustomFilteringWrapperAdapterExampleActivity.class, R.string.activity_title_demo_wa_filtering);
                break;
            case PAGE_ADVANCED:
                // Advanced
                //adapter.put(DraggableSwipeableExampleActivity.class, R.string.activity_title_demo_ds);
                //adapter.put(ExpandableDraggableSwipeableExampleActivity.class, R.string.activity_title_demo_eds);
                //adapter.put(ExpandableDraggableWithSectionExampleActivity.class, R.string.activity_title_demo_ed_with_section);
                break;
            default:
                throw new IllegalArgumentException();
        }

        return adapter;
    }

    @Override
    public void onStart() {
        super.onStart();
        applyLayoutAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();
        applyLayoutAnimation();
    }
}
