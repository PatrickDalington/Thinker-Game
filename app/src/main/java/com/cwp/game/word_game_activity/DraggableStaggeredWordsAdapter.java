package com.cwp.game.word_game_activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.cwp.game.R;
import com.cwp.game.common.data.AbstractDataProvider;
import com.cwp.game.common.utils.DrawableUtils;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemState;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import java.util.ArrayList;
import java.util.List;

public class DraggableStaggeredWordsAdapter
        extends RecyclerView.Adapter<DraggableStaggeredWordsAdapter.BaseViewHolder>
        implements DraggableItemAdapter<DraggableStaggeredWordsAdapter.BaseViewHolder> {

    private static final String TAG = "DraggableItemAdapter";

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_NORMAL_ITEM_OFFSET = 1;
    private static final boolean USE_DUMMY_HEADER = true;
    private static final boolean RANDOMIZE_ITEM_SIZE = false;

    private final AbstractDataProvider mProvider;
    private final ItemClickListener itemClickListener;
    private final Context context;
    private String[][] words;
    private int wordCurrentPos = 0;
    private int gameScore = 0;
    private NormalItemViewHolder myHolder;

    private final ScoreUpdateListener scoreUpdateListener;

    public interface ItemClickListener {
        void onClick(int position, boolean showButton);
    }

    public DraggableStaggeredWordsAdapter(Context context, AbstractDataProvider dataProvider, ItemClickListener itemClickListener, ScoreUpdateListener scoreUpdateListener) {
        this.context = context;
        this.mProvider = dataProvider;
        this.itemClickListener = itemClickListener;
        this.scoreUpdateListener = scoreUpdateListener;
        setHasStableIds(true); // Required for DraggableItemAdapter
    }

    @Override
    public long getItemId(int position) {
        return isHeader(position) ? RecyclerView.NO_ID : mProvider.getItem(toNormalItemPosition(position)).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_NORMAL_ITEM_OFFSET + mProvider.getItem(toNormalItemPosition(position)).getViewType();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.dummy_header_item, parent, false);
            return new HeaderItemViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.list_grid_item_words, parent, false);
            NormalItemViewHolder holder = new NormalItemViewHolder(view);


            return holder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (isHeader(position)) {
            bindHeaderViewHolder((HeaderItemViewHolder) holder);
        } else {
            try {
                bindNormalItemViewHolder((NormalItemViewHolder) holder, toNormalItemPosition(position));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        setItemBackground(holder);
    }

    private void bindHeaderViewHolder(HeaderItemViewHolder holder) {
        StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
        lp.setFullSpan(true);
    }

    private void bindNormalItemViewHolder(NormalItemViewHolder holder, int position) throws InterruptedException {
        this.myHolder = holder;
        AbstractDataProvider.Data item = mProvider.getItem(position);

        // Set the text and style
        holder.mTextView.setText(item.getText());
        holder.mTextView.setTextSize(18f);
        holder.mTextView.setTextColor(item.getColor());

        words = item.getWordAndAnswers();
        holder.mContainer.setOnClickListener(v -> {
            // Handle click events here
        });

        // Adjust the item height
        int itemHeight = calcItemHeight(context, item);
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();


        handleDragState(holder, item);
    }

    private void handleDragState(NormalItemViewHolder holder, AbstractDataProvider.Data item) {
        DraggableItemState dragState = holder.getDragState();
        if (dragState.isUpdated()) {
            int bgResId = dragState.isActive() ? R.drawable.dragging_background_words : R.drawable.bg_item_normal_state_words;
            holder.mContainer.setBackgroundResource(bgResId);
            if (dragState.isActive()) {
                holder.mTextView.setTextColor(Color.WHITE);
                DrawableUtils.clearState(holder.mContainer.getForeground());
            }
        }
    }

    private void setItemBackground(BaseViewHolder holder) {
        holder.itemView.setBackgroundColor(Color.BLACK);
        if (holder.itemView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
            params.setMargins(4, 4, 4, 4);
            holder.itemView.requestLayout();
        }
    }

    @Override
    public int getItemCount() {
        return getHeaderItemCount() + mProvider.getCount();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        fromPosition = toNormalItemPosition(fromPosition);
        toPosition = toNormalItemPosition(toPosition);
        mProvider.moveItem(fromPosition, toPosition);
    }

    @Override
    public boolean onCheckCanStartDrag(@NonNull BaseViewHolder holder, int position, int x, int y) {
        return !isHeader(position);
    }

    @Nullable
    @Override
    public ItemDraggableRange onGetItemDraggableRange(@NonNull BaseViewHolder holder, int position) {
        return new ItemDraggableRange(getHeaderItemCount(), getItemCount() - 1);
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onItemDragStarted(int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
        notifyDataSetChanged();
        handleWordCompletion();
    }

    private void handleWordCompletion() {
        List<Character> chars = new ArrayList<>();
        for (int i = 0; i < mProvider.getCount(); i++) {
            chars.add(mProvider.getItem(i).getText().charAt(0));
        }

        StringBuilder word = new StringBuilder();
        for (char c : chars) {
            word.append(c);
        }

        if (word.toString().equalsIgnoreCase(words[wordCurrentPos][0])) {
            wordCurrentPos++;
            gameScore += 15;

            if (scoreUpdateListener != null) {
                scoreUpdateListener.onScoreUpdated(gameScore);
            }

            if (wordCurrentPos < words.length) {
                mProvider.clear(wordCurrentPos);
                myHolder.mTextView.setText(mProvider.getItem(0).getText());
            }
        }
    }

    public interface ScoreUpdateListener {
        void onScoreUpdated(int newScore);
    }
    static int getHeaderItemCount() {
        return USE_DUMMY_HEADER ? 1 : 0;
    }

    static boolean isHeader(int position) {
        return position < getHeaderItemCount();
    }

    static int toNormalItemPosition(int position) {
        return position - getHeaderItemCount();
    }

    static int calcItemHeight(Context context, AbstractDataProvider.Data item) {
        float density = context.getResources().getDisplayMetrics().density;
        return RANDOMIZE_ITEM_SIZE ? (int) ((8 + (swapBit((int) item.getId(), 0, 8) % 13)) * 10 * density) : (int) (70 * density);
    }

    static int swapBit(int x, int pos1, int pos2) {
        int m1 = 1 << pos1;
        int m2 = 1 << pos2;
        int y = x & ~(m1 | m2);
        if ((x & m1) != 0) y |= m2;
        if ((x & m2) != 0) y |= m1;
        return y;
    }

    public static class BaseViewHolder extends AbstractDraggableItemViewHolder {
        public BaseViewHolder(View v) {
            super(v);
        }
    }

    public static class HeaderItemViewHolder extends BaseViewHolder {
        public HeaderItemViewHolder(View v) {
            super(v);
        }
    }

    public static class NormalItemViewHolder extends BaseViewHolder {
        public FrameLayout mContainer, mainContainer;
        public TextView mTextView;

        public NormalItemViewHolder(View v) {
            super(v);
            mainContainer = v.findViewById(R.id.frame_layout);
            mContainer = v.findViewById(R.id.container);
            mTextView = v.findViewById(android.R.id.text1);
        }
    }
}
