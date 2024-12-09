package com.cwp.game.common.utils;


public class AllBadges {
    private int imageResId;
    private String name, description;

    // Constructor
    public AllBadges(int imageResId, String name, String description) {
        this.imageResId = imageResId;
        this.name = name;
        this.description = description;
    }

    // Getters
    public int getImageResId() {
        return imageResId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {

        return description;
    }
}
