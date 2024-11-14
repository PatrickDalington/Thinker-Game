package com.cwp.game.numbers_game_activity;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.NinePatchDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cwp.game.R;
import com.cwp.game.common.data.AbstractDataProvider;
import com.cwp.game.common.data.AlphabetsDataProvider;
import com.cwp.game.common.data.NumbersDataProvider;
import com.google.android.material.snackbar.Snackbar;
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;
import java.util.Objects;

public class DraggableNumbersFragment extends Fragment
        implements DraggableNumbersAdapter.ItemClickListener, CreateSheet.PSheetListener{
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private DraggableNumbersAdapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    SharedPreferences preferences;


    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    TextView timer;
    Toolbar toolbar;
    ImageView speaker;
    boolean isMusicPlaying;
    Bundle bundle;

    CountDownTimer cTimer = null;
    int seconds, minutes;
    TextToSpeech tts;
    TextView displayText, levelText;
    String level;
    boolean isSoundEffectEnabled;
    private NumbersDataProvider dataProvider;

    boolean isAlphabetCompleted;

    private MediaPlayer backgroundMusic;
    private boolean isMusicEnabled;


    public DraggableNumbersFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_list_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Showing bottom fragment for user to choose preferred choice
        CreateSheet preGame = new CreateSheet();
        preGame.show(requireActivity().getSupportFragmentManager(),"Viewing...");

        preferences = requireActivity().getSharedPreferences("game_settings", MODE_PRIVATE);
        isSoundEffectEnabled = preferences.getBoolean("effects_enabled", true);



        //Setting up toolbar
        toolbar = requireView().findViewById(R.id.toolbar);

        // Game text display for levels of game
        displayText = requireView().findViewById(R.id.displayText);

        levelText = requireView().findViewById(R.id.level);

        //Game timer
        timer = requireView().findViewById(R.id.time);

        // Speaker for muting and unmuting
        speaker = view.findViewById(R.id.speaker);


        // Onstart of the fragment set the isMute to true
        isMusicEnabled = true;



        //activity = new DraggableGridExampleActivity();
        bundle = getArguments();

        if (bundle != null) {
            if (Objects.equals(bundle.getString("time"), "visible"))
                toolbar.setVisibility(View.VISIBLE);
        }


        // Initialize data provider
        dataProvider = new NumbersDataProvider();



        //noinspection ConstantConditions
        mRecyclerView = getView().findViewById(R.id.recycler_view);
        mLayoutManager = new GridLayoutManager(requireContext(), 5, RecyclerView.VERTICAL, false);

        // drag & drop manager
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.material_shadow_z3));
        // Start dragging after long press
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
        mRecyclerViewDragDropManager.setInitiateOnMove(false);
        mRecyclerViewDragDropManager.setLongPressTimeout(50);

        // setup dragging item effects (NOTE: DraggableItemAnimator is required)
        mRecyclerViewDragDropManager.setDragStartItemAnimationDuration(250);
        mRecyclerViewDragDropManager.setDraggingItemAlpha(0.8f);
        mRecyclerViewDragDropManager.setDraggingItemScale(1.3f);
        mRecyclerViewDragDropManager.setDraggingItemRotation(10.0f);


        //adapter
        final DraggableNumbersAdapter myItemAdapter = new DraggableNumbersAdapter(getActivity(), getDataProvider(), mRecyclerView, this, isMusicEnabled);
        mAdapter = myItemAdapter;

        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(myItemAdapter);      // wrap for dragging

        GeneralItemAnimator animator = new DraggableItemAnimator(); // DraggableItemAnimator is required to make item animations properly.

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        mRecyclerView.setItemAnimator(animator);

        // additional decorations
        //noinspection StatementWithEmptyBody
        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            mRecyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) Objects.requireNonNull(Objects.requireNonNull(ContextCompat.getDrawable(requireContext(), R.drawable.material_shadow_z1)))));
        }
        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);

        // for debugging
        // animator.setDebug(true);
        // animator.setMoveDuration(2000);


        startBackgroundMusic();

        // Check if is sound is enabled from settings and set the appropriate boolean to the adapter
        mAdapter.setMusicEnabled(isSoundEffectEnabled);



        // Onclick for speaker muting/unmuting
        speaker.setOnClickListener(v -> {
            isSoundEffectEnabled = preferences.getBoolean("effects_enabled", true);
            // If sound effects are enabled in settings
            if (isSoundEffectEnabled) {
                isMusicEnabled = !isMusicEnabled; // Toggle the music enabled state

                if (isMusicEnabled) {
                    // Enable both background music and sound effects
                    speaker.setImageResource(R.drawable.speaker_on);
                    startBackgroundMusic();
                    mAdapter.setMusicEnabled(true);
                } else {
                    // Mute both background music and sound effects
                    speaker.setImageResource(R.drawable.speaker_off);
                    stopBackgroundMusic();
                    mAdapter.setMusicEnabled(false);
                }
            } else {

                // If sound effects are disabled in settings
                isMusicEnabled = !isMusicEnabled; // Toggle the music enabled state

                if (isMusicEnabled) {
                    // Only enable background music, sound effects remain muted
                    speaker.setImageResource(R.drawable.speaker_on);
                    startBackgroundMusic();
                } else {
                    // Mute background music, sound effects remain muted
                    speaker.setImageResource(R.drawable.speaker_off);
                    stopBackgroundMusic();
                }
            }
        });

        // onBackPressed
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish();  // Close the activity
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), callback);
    }

    @Override
    public void onPause() {
        mRecyclerViewDragDropManager.cancelDrag();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
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

        super.onDestroyView();
    }




    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_drag_grid, menu);

        // setting up the item move mode selection switch
        MenuItem menuSwitchItem = menu.findItem(R.id.menu_switch_swap_mode);
        CompoundButton actionView = Objects.requireNonNull(menuSwitchItem.getActionView()).findViewById(R.id.switch_view);

        actionView.setOnCheckedChangeListener((buttonView, isChecked) -> updateItemMoveMode(isChecked));
    }

    private void updateItemMoveMode(boolean swapMode) {
        int mode = (swapMode)
                ? RecyclerViewDragDropManager.ITEM_MOVE_MODE_SWAP
                : RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT;

        mRecyclerViewDragDropManager.setItemMoveMode(mode);
        mAdapter.setItemMoveMode(mode);

        Snackbar.make(requireView(), "Item move mode: " + (swapMode ? "SWAP" : "DEFAULT"), Snackbar.LENGTH_SHORT).show();
    }

    private boolean supportsViewElevation() {
        return true;
    }

    public AbstractDataProvider getDataProvider() {
        return ((DraggableNumbersActivity) requireActivity()).getDataProvider();
    }
        public void reverseTimer(int Seconds,final TextView tv){

            cTimer = new CountDownTimer(Seconds* 1000L +1000, 1000) {

                @SuppressLint({"DefaultLocale", "SetTextI18n"})
                public void onTick(long millisUntilFinished) {
                    seconds = (int) (millisUntilFinished / 1000);
                    minutes = seconds / 60;
                    seconds = seconds % 60;
                    tv.setText(String.format("%02d", minutes)
                            + ":" + String.format("%02d", seconds));
                }

                @SuppressLint("SetTextI18n")
                public void onFinish() {
                    if (isAlphabetCompleted){
                        tv.setText("Yay 😎");

                        if (getActivity() != null) {
                            CreateSheet createSheet = new CreateSheet();
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("Game Over", "no");
                            createSheet.setArguments(bundle1);
                            createSheet.show(requireActivity().getSupportFragmentManager(), "viewing...");
                        }
                    }else{
                        tv.setText("Gave Over 😞");

                        if (getActivity() != null) {
                            CreateSheet createSheet = new CreateSheet();
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("Game Over", "yes");
                            bundle1.putString("level", "practice");
                            createSheet.setArguments(bundle1);
                            createSheet.show(requireActivity().getSupportFragmentManager(), "viewing...");


                        }
                    }

                }
            };
            cTimer.start();


        }

    @SuppressLint("DefaultLocale")
    @Override
    public void onClick(int position, String value) {

        if (value.equals("completed") && !levelText.getText().equals("Practice"))
        {
            // Passing signal that alphabet order is completed
            isAlphabetCompleted = true;

            //show sheet after completed actual game... Cancel the timer as well
            cTimer.cancel();
            GameCompletedSheet gameCompletedSheet = new GameCompletedSheet();
            gameCompletedSheet.show(requireActivity().getSupportFragmentManager(), "viewing...");


        }else if (value.equals("completed") && levelText.getText().equals("Practice")){
            // Passing signal that alphabet order is completed
            isAlphabetCompleted = true;

            //Show sheet after practice session
            GamePracticeCompletedSheet gamePracticeCompletedSheet = new GamePracticeCompletedSheet();
            gamePracticeCompletedSheet.show(requireActivity().getSupportFragmentManager(), "viewing...");

            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.detach(DraggableNumbersFragment.this).attach(DraggableNumbersFragment.this).commit();

        } else if (value.equals("not completed")){

            // Passing signal that alphabet order is completed
            isAlphabetCompleted = false;


            //Toast.makeText(requireActivity(), "You did not complete the setting of alphabets!🫤", Toast.LENGTH_SHORT).show();
            tts = new TextToSpeech(getActivity(), i -> {

                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    tts.setLanguage(Locale.UK);
                }
            });
            tts.speak(value, TextToSpeech.QUEUE_FLUSH, null);
        }
        //Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();

    }

    public void reloadItems() {
        if (mAdapter != null) {
            // Fetch new data
            AbstractDataProvider newDataProvider = getDataProvider();
            mAdapter.updateData(newDataProvider);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onGameStart(boolean t) {
        if (t) {
            mAdapter.reshuffleItems();
            levelText.setText("Practice");
            Toast.makeText(getContext(), "Game started in practice level", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void getString(String title)
    {
       if (title.equals("close")) {
           FragmentManager fm = requireActivity().getFragmentManager();
           if (fm.getBackStackEntryCount() > 0)
               fm.popBackStack();
           else
               requireActivity().finish();

       }
    }

    @Override
    public void getGameLevel(String level) {
        // All triggered from DraggableAplphabetsActiviy

        switch (level) {
            case "easy":
                displayText.setVisibility(View.VISIBLE);
                reverseTimer(60, timer);
                levelText.setText("Easy");
                break;
            case "normal":
                displayText.setVisibility(View.VISIBLE);
                reverseTimer(30, timer);
                displayText.setVisibility(View.GONE);
                levelText.setText("Normal");
                break;
            case "difficult":
                displayText.setVisibility(View.VISIBLE);
                reverseTimer(20, timer);
                displayText.setVisibility(View.GONE);
                levelText.setText("Difficult");
                break;
            default:
                levelText.setText("Practice");
        }

    }

    @Override
    public void onWatchAiPlay() {
        if (mAdapter != null) {
            mAdapter.play();
        }
    }

    public void loadNewDataProvider() {


        // Update the adapter's data provider
        mAdapter.updateDataProvider(getDataProvider());

        // Notify the adapter that the data has changed
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void showTextForEasyLevel(String alphabets, int textPosition) {
        // This is triggered from DraggableAlphabetsAdapter class.
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString alpha = new SpannableString(alphabets);
        alpha.setSpan(new ForegroundColorSpan(Color.YELLOW),textPosition, alphabets.length(), 0);
        builder.append(alpha);
        displayText.setText(builder, TextView.BufferType.SPANNABLE);
    }

    public void startBackgroundMusic(){
        if (isMusicEnabled) {
            // Initialize and play background music
            backgroundMusic = MediaPlayer.create(requireActivity(), R.raw.alphabet_sound); // Add your audio file to the res/raw folder
            backgroundMusic.setLooping(true); // Loop the music
            backgroundMusic.start();

        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.release();
            backgroundMusic = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        stopBackgroundMusic();
    }
}