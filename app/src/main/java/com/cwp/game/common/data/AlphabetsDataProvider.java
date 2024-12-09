

package com.cwp.game.common.data;

import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import androidx.annotation.NonNull;

public class AlphabetsDataProvider extends AbstractDataProvider {
    private static List<ConcreteData> mData = Collections.emptyList();
    List<Integer> colorId;
    private ConcreteData mLastRemovedData;
    private int mLastRemovedPosition = -1;



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


    public AlphabetsDataProvider() {

        final String atoz = "ABCD";
        String text;

        colorId = new ArrayList<>();
        colorId.add(Color.parseColor("#CC8518"));
        colorId.add(Color.parseColor("#FF6200EE"));
        colorId.add(Color.parseColor("#FF3700B3"));
        colorId.add(Color.parseColor("#270538"));
        colorId.add(Color.RED);

        mData = new LinkedList<>();
        String a = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String t = shuffleString("ABCDEFGHIJKLMNOPQRSTUVWXYZ");

        Map<String, Object> map = new TreeMap<>();

        for (int x = 0; x < t.length(); x++){
            map.put(String.valueOf(x), a.charAt(x));
        }



        final List<Map.Entry<String, Object>> e = new ArrayList<>(map.entrySet());
        Log.i("testing", map.entrySet().toString());
        Collections.shuffle(e);
        final Map<String, Object> r = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : e) {
            r.put(entry.getKey(), entry.getValue());
        }
        Log.i("show", Arrays.toString(r.entrySet().toArray()));



        int min = 0;
        int max = colorId.size() -1;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < t.length(); j++) {
                final long id = mData.size();
                final int viewType = 0;
                Collections.shuffle(colorId);
                final int random = new Random().nextInt((max - min) + 1) + min;

                if (r.entrySet().toArray()[j].toString().length() == 3)
                    text = r.entrySet().toArray()[j].toString().substring(2);
                else
                    text = r.entrySet().toArray()[j].toString().substring(3);

                final int charPos = Integer.parseInt((String) r.keySet().toArray()[j]);
                final int color = colorId.get(random);
                final int swipeReaction = RecyclerViewSwipeManager.REACTION_CAN_SWIPE_UP | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_DOWN;
                mData.add(new ConcreteData(charPos, id, viewType, text, swipeReaction, color));
            }
        }

    }

    public void reloadData() {
        mData.clear();
        String a = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String t = shuffleString("ABCDEFGHIJKLMNOPQRSTUVWXYZ");

        Map<String, Object> map = new TreeMap<>();

        for (int x = 0; x < t.length(); x++) {
            map.put(String.valueOf(x), a.charAt(x));
        }

        List<Map.Entry<String, Object>> e = new ArrayList<>(map.entrySet());
        Collections.shuffle(e);
        Map<String, Object> r = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : e) {
            r.put(entry.getKey(), entry.getValue());
        }

        int min = 0;
        int max = colorId.size() - 1;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < t.length(); j++) {
                long id = mData.size();
                int viewType = 0;
                Collections.shuffle(colorId);
                int random = new Random().nextInt((max - min) + 1) + min;

                String text;
                if (r.entrySet().toArray()[j].toString().length() == 3)
                    text = r.entrySet().toArray()[j].toString().substring(2);
                else
                    text = r.entrySet().toArray()[j].toString().substring(3);

                int charPos = Integer.parseInt((String) r.keySet().toArray()[j]);
                int color = colorId.get(random);
                int swipeReaction = RecyclerViewSwipeManager.REACTION_CAN_SWIPE_UP | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_DOWN;

                mData.add(new ConcreteData(charPos, id, viewType, text, swipeReaction, color));
            }
        }
    }

    public void reshuffleItems() {
        Collections.shuffle(mData);
    }
    @Override
    public void clear(int index) {

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
    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        final ConcreteData item = mData.remove(fromPosition);

        mData.add(toPosition, item);
        mLastRemovedPosition = -1;
    }

    public void clear()
    {
        mData.clear();
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
        private final int charPos;
        @NonNull
        private final String mText;
        private final int mViewType;
        private boolean mPinned;
        private  int textColor;

        boolean isHighlighted;
        private boolean isBeingMoved;

        ConcreteData(int charPosition, long id, int viewType, @NonNull String text, int swipeReaction, int color) {
            mId = id;
            charPos = charPosition;
            mViewType = viewType;
            textColor = color;
            mText = makeText(id, text, swipeReaction, color);
        }

        private static String makeText(long id, String text, int swipeReaction, int color) {
            return text;
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
        public int getCharPos(){
            return charPos;
        }

        @Override
        public long setId(int id){
            return id;
        }


        @Override
        public int getColor() {
            return textColor;
        }

        @Override
        public String[][] getWordAndAnswers() {
            return new String[0][];
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
            return isHighlighted;
        }

        @Override
        public boolean setHighlighted(boolean highlighted) {
            return isHighlighted = highlighted;
        }

        @Override
        public boolean isBeingMoved() {
            return isBeingMoved;
        }

        @Override
        public boolean setIsBeingMoved(boolean isBeingMoved) {
            return this.isBeingMoved = isBeingMoved;
        }

        @Override
        public void reshuffleItems() {
            Collections.shuffle(mData);
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
