package com.cwp.game.numbers_game_activity;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cwp.game.R;
import com.cwp.game.common.data.AbstractDataProvider;
import com.cwp.game.common.utils.DrawableUtils;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemState;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import java.util.ArrayList;
import java.util.List;

public class DraggableNumbersAdapter extends RecyclerView.Adapter<DraggableNumbersAdapter.MyViewHolder>
        implements DraggableItemAdapter<DraggableNumbersAdapter.MyViewHolder> {

    private static final String TAG = "DraggableAlphabetsAdapter";
    private int mItemMoveMode = RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT;

    private AbstractDataProvider mProvider;
    private final Context mContext;
    private final RecyclerView mRecyclerView;
    private final ItemClickListener mItemClickListener;
    List<Integer> al = new ArrayList<>();
    private TextToSpeech mTTS;
    private final SoundPool mSoundPool;
    private final int mDropSoundId;
    public boolean isMusicEnabled;


    public DraggableNumbersAdapter(Context context, AbstractDataProvider dataProvider, RecyclerView recyclerView, ItemClickListener itemClickListener, boolean isMusicEnabled) {
        this.mContext = context;
        this.mProvider = dataProvider;
        this.mRecyclerView = recyclerView;
        this.mItemClickListener = itemClickListener;
        this.isMusicEnabled = isMusicEnabled;
        setHasStableIds(true);

        // Initialize SoundPool for playing sound effects
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        mSoundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();

        // Load the sound resource (e.g., res/raw/drop_sound.mp3)
        mDropSoundId = mSoundPool.load(context, R.raw.ring, 1);


    }

    public interface ItemClickListener {
        void onClick(int position, String value);
        void showTextForEasyLevel(String alphabets, int currentPosition);
    }

    public void setItemMoveMode(int itemMoveMode) {
        this.mItemMoveMode = itemMoveMode;
    }

    public void updateData(AbstractDataProvider newDataProvider) {
        this.mProvider = newDataProvider;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return mProvider.getItem(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return mProvider.getItem(position).getViewType();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_grid_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AbstractDataProvider.Data item = mProvider.getItem(position);

        // Set text and background based on item state
        holder.mTextView.setText(item.getText());
        holder.mTextView.setTextColor(item.getColor());
        holder.mContainer.setBackgroundColor(item.isHighlighted() ?
                mContext.getResources().getColor(R.color.bg_item_dragging_active_state) :
                mContext.getResources().getColor(R.color.bg_item_normal_state));

        // Setting the margin for the main container for this game typ
        setMargins(holder.main_container);

        // Set click listener for the item
        holder.mContainer.setOnClickListener(v -> {
            mItemClickListener.onClick(position, String.valueOf(item.getId()));
            Toast.makeText(mContext, String.valueOf(item.getCharPos()), Toast.LENGTH_SHORT).show();
        });


        // Update drag state background
        DraggableItemState dragState = holder.getDragState();
        if (dragState.isUpdated()) {
            int bgResId = dragState.isActive() ? R.drawable.bg_item_dragging_active_state :
                    dragState.isDragging() ? R.drawable.bg_item_dragging_state : R.drawable.bg_item_normal_state;
            holder.mContainer.setBackgroundResource(bgResId);
            DrawableUtils.clearState(holder.mContainer.getForeground());
        }
    }

    @Override
    public int getItemCount() {
        return mProvider.getCount();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        Log.d("Item movement", "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");
        if (mItemMoveMode == RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT) {
            mProvider.moveItem(fromPosition, toPosition);
        } else {
            mProvider.swapItem(fromPosition, toPosition);
        }
    }

    @Override
    public boolean onCheckCanStartDrag(@NonNull MyViewHolder holder, int position, int x, int y) {
        return true;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(@NonNull MyViewHolder holder, int position) {
        return null; // No drag-sortable range specified
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }

    @Override
    public void onItemDragStarted(int position) {
        notifyItemChanged(position);
    }

    @Override
    public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
        notifyDataSetChanged();
        mItemClickListener.showTextForEasyLevel("ABCDEFGHIJKLMNOPQRSTUVWXYZ", toPosition + 1);

        if (result) {
            validateNumberOrder();

            // Play the drop sound effect if the sound is not turned off by the player
            if (isMusicEnabled)
                mSoundPool.play(mDropSoundId, 1.0f, 1.0f, 1, 0, 1.0f);


            // Get the view that was just dropped
            View droppedView = mRecyclerView.getLayoutManager().findViewByPosition(toPosition);


            if (droppedView != null) {
                applyBlinkingEffect(droppedView);
                // Optionally remove the animation effect after some time
                new Handler().postDelayed(droppedView::clearAnimation, 500);
            }
        }
    }

    private void applyBlinkingEffect(View view) {
        // Using AndroidViewAnimations to create a blinking effect
        // Create a blinking animation
        Animation blinkAnimation = new AlphaAnimation(0.0f, 2.0f);
        blinkAnimation.setDuration(1000); // Duration for one blink
        blinkAnimation.setStartOffset(0);
        blinkAnimation.setRepeatMode(Animation.REVERSE);
        blinkAnimation.setRepeatCount(Animation.INFINITE); // Infinite blinking

        // Start the animation
        view.startAnimation(blinkAnimation);
    }


    private void createReboundBubbleEffect(View view) {
        SpringSystem springSystem = SpringSystem.create();
        Spring spring = springSystem.createSpring();
        SpringConfig config = SpringConfig.fromOrigamiTensionAndFriction(40, 4);
        spring.setSpringConfig(config);

        spring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float value = (float) spring.getCurrentValue();
                float scale = 1f + (value * 0.5f);
                view.setScaleX(scale);
                view.setScaleY(scale);
            }
        });

        spring.setEndValue(1);
    }

    public void reshuffleItems() {
        mProvider.reshuffleItems();
        notifyDataSetChanged();
    }

    public void updateDataProvider(AbstractDataProvider newProvider) {
        this.mProvider = newProvider;
    }

    private void setMargins (View view) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(4, 4, 4, 4);
            view.requestLayout();
        }
    }
    private void validateNumberOrder() {
        List<Character> expectedOrder = new ArrayList<>();
        List<Character> currentOrder = new ArrayList<>();
        String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < getItemCount(); i++) {
            expectedOrder.add(alpha.charAt(i));
            currentOrder.add(mProvider.getItem(i).getText().charAt(0));
            sb.append(mProvider.getItem(i).getText().charAt(0)).append(", ");
        }

        if (currentOrder.equals(expectedOrder)) {
            mItemClickListener.onClick(1, "completed");
        } else {
            mItemClickListener.onClick(1, "not completed");
        }
    }

    public void setMusicEnabled(boolean isMusicEnabled) {
        this.isMusicEnabled = isMusicEnabled;
    }

    public void play1(){


        for(int x = 0; x < mProvider.getCount() - 1; x++){
            int item1 = al.get(x);
            Toast.makeText(mContext, String.valueOf(item1), Toast.LENGTH_SHORT).show();
            for(int y = 0; y < mProvider.getCount() - 1 - x; y++){

                AbstractDataProvider.Data item2 = mProvider.getItem(x + 1);

                if (item1 == item2.getCharPos()){
                    int finalX = x;

                    mProvider.swapItem(item2.getCharPos(), item1);
                    notifyItemMoved(item2.getCharPos(), item1);
                    onItemDragStarted(item1);
                    onItemDragFinished(item2.getCharPos(), item1, true);
                    mRecyclerView.post(() -> {

                    });
                }
            }

        }
    }

    public void play() {
        if (mProvider == null || mProvider.getCount() < 1) {
            Toast.makeText(mContext, "Data provider is not initialized or has no items.", Toast.LENGTH_SHORT).show();
            return;
        }

        sortStep(0, 0);
    }

    private void sortStep(int i, int j) {
        int n = mProvider.getCount();
        if (i < n - 1) {
            if (j < n - 1 - i) {
                AbstractDataProvider.Data item1 = mProvider.getItem(j);
                AbstractDataProvider.Data item2 = mProvider.getItem(j + 1);

                if (item1.getCharPos() > item2.getCharPos()) {
                    mRecyclerView.post(() -> {
                        mProvider.swapItem(j, j + 1);
                        notifyItemMoved(j, j + 1);
                        onItemDragStarted(j);
                        onItemDragFinished(j, j + 1, true);
                    });
                }
                new Handler().postDelayed(() -> sortStep(i, j + 1), 500);
            } else {
                sortStep(i + 1, 0);
            }
        } else {
            mRecyclerView.post(() -> Toast.makeText(mContext, "Sorting completed", Toast.LENGTH_SHORT).show());
        }
    }

    public static class MyViewHolder extends AbstractDraggableItemViewHolder {
        public final FrameLayout mContainer;
        public final FrameLayout main_container;
        public final View mDragHandle;
        public final TextView mTextView;

        public MyViewHolder(View v) {
            super(v);
            mContainer = v.findViewById(R.id.container);
            mDragHandle = v.findViewById(R.id.drag_handle);
            mTextView = v.findViewById(android.R.id.text1);
            main_container = v.findViewById(R.id.frame_layout);
        }
    }
}
