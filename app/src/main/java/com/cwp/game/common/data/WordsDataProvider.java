

package com.cwp.game.common.data;

import android.graphics.Color;

import androidx.annotation.NonNull;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class WordsDataProvider extends AbstractDataProvider {
    private final List<ConcreteData> mData;
    List<Integer> colorId;

    private ConcreteData mLastRemovedData;
    private int mLastRemovedPosition = -1;

    ArrayList<ArrayList<String>> wo = new ArrayList<>();
    String[][] words =
            //Number 1
            {{"Tobi", "tobi"},{"Patrick", "patrick"}, {"Olayinka", "olayinka"}, {"John", "john"}
                    , {"Opeyemi", "opeyemi"}, {"Micheal", "micheal"}, {"Jerry", "jerry"}};

    public static String shuffleString(String string)
    {
        List<String> letters = Arrays.asList(string.split(""));
        Collections.shuffle(letters);
        StringBuilder shuffled = new StringBuilder();
        for (String letter : letters) {
            shuffled.append(letter);
        }
        return shuffled.toString();
    }


    /**
     * Shuffles a 2D array with the same number of columns for each row.
     *
     * @return
     */
    public String[][] shuffleWords(String[][] words, int columns, Random rnd) {
        int size = words.length  * columns;
        for (int i = size; i > 1; i--)
            words = swap(words, columns, i - 1, rnd.nextInt(i));
        return words;
    }

    /**
     * Swaps two entries in a 2D array, where i and j are 1-dimensional indexes, looking at the
     * array from left to right and top to bottom.
     *
     * @return
     */
    public static String[][] swap(String[][] words, int columns, int i, int j) {
        String tmp = words[i / columns][i % columns];
        words[i / columns][i % columns] = words[j / columns][j % columns];
        words[j / columns][j % columns] = tmp;

        return words;
    }

    public WordsDataProvider() {

        colorId = new ArrayList<>();
        colorId.add(Color.WHITE);
        colorId.add(Color.YELLOW);
        colorId.add(Color.RED);
        colorId.add(Color.GREEN);
        colorId.add(Color.MAGENTA);

        mData = new LinkedList<>();

        int min = 0;
        int max = colorId.size() -1;
        int len = words.length;



        for (int i = 0; i < 1; i++) {
            // Create a random object for the shuffledWords function
            Random random1 = new Random();

            // Create an object variable of the newly shuffled words
            String[][] sL = shuffleWords(words, 1, random1);

            // Pass the shuffled list the shuffled string function
            String tx = shuffleString(sL[i][0]);
            for (int j = 0; j < tx.length(); j++) {
                final long id = mData.size();
                final int viewType = 0;
                Collections.shuffle(colorId);
                final int random = new Random().nextInt((max - min) + 1) + min;

                final String text = Character.toString(tx.charAt(j));
                //final String text = Character.toString(t.charAt(j));
                final int color = colorId.get(random);
                final int swipeReaction = RecyclerViewSwipeManager.REACTION_CAN_SWIPE_UP | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_DOWN;

                mData.add(new ConcreteData(id, viewType, text, swipeReaction,color,words));
            }
        }
    }


    @Override
    public void clear(int index) {
        mData.clear();

        int min = 0;
        int max = colorId.size() -1;
        int len = words.length;


        if (index <= words.length - 1)
        {
            String tx = shuffleString(words[index][0]);
            for (int j = 0; j < tx.length(); j++) {
                final long id = mData.size();
                final int viewType = 0;
                Collections.shuffle(colorId);
                final int random = new Random().nextInt((max - min) + 1) + min;

                final String text = Character.toString(tx.charAt(j));
                //final String text = Character.toString(t.charAt(j));
                final int color = colorId.get(random);
                final int swipeReaction = RecyclerViewSwipeManager.REACTION_CAN_SWIPE_UP | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_DOWN;
                mData.add(new ConcreteData(id, viewType, text, swipeReaction,color,words));

            }
        }


    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Data getItem(int index) {
        if (index < 0 || index >= getCount()) {
            throw new IndexOutOfBoundsException("index = " + index);
        }

        return mData.get(index);
    }

    @Override
    public int undoLastRemoval() {
        if (mLastRemovedData != null) {
            int insertedPosition;
            if (mLastRemovedPosition >= 0 && mLastRemovedPosition < mData.size()) {
                insertedPosition = mLastRemovedPosition;
            } else {
                insertedPosition = mData.size();
            }

            mData.add(insertedPosition, mLastRemovedData);

            mLastRemovedData = null;
            mLastRemovedPosition = -1;

            return insertedPosition;
        } else {
            return -1;
        }
    }

    @Override
    public void reshuffleItems() {

    }


    @Override
    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        final ConcreteData item = mData.remove(fromPosition);

        mData.add(toPosition, item);
        mLastRemovedPosition = -1;
    }

    @Override
    public void swapItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        Collections.swap(mData, toPosition, fromPosition);
        mLastRemovedPosition = -1;
    }

    @Override
    public boolean isHighlighted(boolean isHighlighted) {
        return false;
    }

    @Override
    public void removeItem(int position) {
        //noinspection UnnecessaryLocalVariable
        final ConcreteData removedItem = mData.remove(position);

        mLastRemovedData = removedItem;
        mLastRemovedPosition = position;
    }

    public static final class ConcreteData extends Data {

        private final long mId;
        @NonNull
        private final String mText;
        private final int mViewType;
        private boolean mPinned;
        private  int textColor;
        private String[][] wordAndAnswers;

        ConcreteData(long id, int viewType, @NonNull String text, int swipeReaction, int color, String[][] words) {
            mId = id;
            mViewType = viewType;
            textColor = color;
            wordAndAnswers = words;

            mText = makeText(id, text, swipeReaction, color, words);
        }

        private static String makeText(long id, String text, int swipeReaction, int color, String[][] words) {
            return text;
        }

        public String[][] getWordAndAnswers() {
            return wordAndAnswers;
        }

        public void setWordAndAnswers(String[][] wordAndAnswers) {
            this.wordAndAnswers = wordAndAnswers;
        }

        @Override
        public boolean isSectionHeader() {
            return false;
        }

        @Override
        public int getViewType() {
            return mViewType;
        }

        @Override
        public long getId() {
            return mId;
        }

        @Override
        public int getCharPos() {
            return 0;
        }

        @Override
        public long setId(int id) {
            return id;
        }

        @Override
        public int getColor() {
            return textColor;
        }

        @NonNull
        @Override
        public String toString() {
            return mText;
        }

        @Override
        public String getText() {
            return mText;
        }

        @Override
        public String getEmoji() {
            return "";
        }

        @Override
        public boolean isPinned() {
            return mPinned;
        }

        @Override
        public boolean isHighlighted() {
            return false;
        }

        @Override
        public boolean setHighlighted(boolean highlighted) {
            return false;
        }

        @Override
        public boolean isBeingMoved() {
            return false;
        }

        @Override
        public boolean setIsBeingMoved(boolean isBeingMoved) {
            return false;
        }

        @Override
        public void reshuffleItems() {

        }

        @Override
        public void setPinned(boolean pinned) {
            mPinned = pinned;
        }

        public int getTextColor() {
            return textColor;
        }

        public void setTextColor(int textColor) {
            this.textColor = textColor;
        }
    }
}
