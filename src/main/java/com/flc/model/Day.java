package com.flc.model;

/**
 * The two days on which lessons are offered each weekend.
 */
public enum Day {

    SATURDAY("Saturday"), SUNDAY("Sunday");

    private final String displayName;

    Day(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}