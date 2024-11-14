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

package com.cwp.game.common.data;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;

public class ExampleSectionDataProvider extends AbstractDataProvider {
    private final List<ConcreteData> mData;
    private ConcreteData mLastRemovedData;
    private int mLastRemovedPosition = -1;

    List<Integer> colorId;

    public static final int ITEM_VIEW_TYPE_SECTION_HEADER = 0;
    public static final int ITEM_VIEW_TYPE_SECTION_ITEM = 1;

    public ExampleSectionDataProvider() {
        final String sectionItems = "ABCDE";
        final String innerSectionItems = "abc";

        colorId = new ArrayList<>();
        colorId.add(Color.parseColor("#CC8518"));
        colorId.add(Color.parseColor("#FF6200EE"));
        colorId.add(Color.parseColor("#FF3700B3"));
        colorId.add(Color.parseColor("#270538"));
        colorId.add(Color.RED);

        mData = new LinkedList<>();

        final int min = 0;
        final int max = colorId.size() -1;


        for (int i = 0; i < sectionItems.length(); i++) {
            // put section header
            {
                final int random = new Random().nextInt((max - min) + 1) + min;
                Collections.shuffle(colorId);

                final long id = mData.size();
                final int viewType = ITEM_VIEW_TYPE_SECTION_HEADER;
                int color = colorId.get(random);
                final String text = "Section " + sectionItems.charAt(i);
                mData.add(new ConcreteData(id, true, viewType, text, color));
            }

            // put section child items
            for (int j = 0; j < innerSectionItems.length(); j++) {
                Collections.shuffle(colorId);

                final int random = new Random().nextInt((max - min) + 1) + min;
                final long id = mData.size();
                int color = colorId.get(random);
                final int viewType = ITEM_VIEW_TYPE_SECTION_ITEM;
                final String text = Character.toString(innerSectionItems.charAt(j));
                mData.add(new ConcreteData(id, false, viewType, text,color));
            }
        }
    }

    @Override
    public void clear(int index) {
        mData.clear();
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

        Collections.swap(mData, fromPosition, toPosition);
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
        private final boolean mIsSectionHeader;
        @NonNull
        private final String mText;
        private final int mViewType;
        private boolean mPinned;
        private final int textColor;
        ConcreteData(long id, boolean isSectionHeader, int viewType, @NonNull String text, int color) {
            mId = id;
            mIsSectionHeader = isSectionHeader;
            mViewType = viewType;
            mText = text;
            textColor = color;
        }

        @Override
        public boolean isSectionHeader() {
            return mIsSectionHeader;
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
    }
}
