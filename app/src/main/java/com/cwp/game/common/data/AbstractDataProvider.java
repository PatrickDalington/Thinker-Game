package com.cwp.game.common.data;

public abstract class AbstractDataProvider {




    public static abstract class Data {
        public abstract long getId();

        public abstract int getCharPos();

        public abstract long setId(int id);

        public abstract int getColor();

        public abstract String[][] getWordAndAnswers();

        public abstract boolean isSectionHeader();

        public abstract int getViewType();

        public abstract String getText();
        public abstract String getEmoji();

        public abstract void setPinned(boolean pinned);

        public abstract boolean isPinned();

        public abstract boolean isHighlighted();
        public abstract boolean setHighlighted(boolean highlighted);

        public abstract boolean isBeingMoved();
        public abstract boolean setIsBeingMoved(boolean isBeingMoved);

        public abstract void reshuffleItems();
    }

    public abstract void clear(int index);
    public abstract int getCount();

    public abstract Data getItem(int index);

    public abstract void removeItem(int position);

    public abstract void moveItem(int fromPosition, int toPosition);

    public abstract void swapItem(int fromPosition, int toPosition);

    public abstract boolean isHighlighted(boolean isHighlighted);
    public abstract int undoLastRemoval();
    public abstract  void reshuffleItems();




}
