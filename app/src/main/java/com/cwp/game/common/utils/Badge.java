package com.cwp.game.common.utils;


public class Badge {
    private int imageResId;
    private String name;

    // Constructor
    public Badge(int imageResId, String name) {
        this.imageResId = imageResId;
        this.name = name;
    }

    // Getters
    public int getImageResId() {
        return imageResId;
    }

    public String getName() {
        return name;
    }


}
