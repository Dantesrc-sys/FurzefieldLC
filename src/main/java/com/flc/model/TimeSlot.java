package com.flc.model;

public enum TimeSlot {

    MORNING   ("Morning",   "09:00 AM"),
    AFTERNOON ("Afternoon", "01:00 PM"),
    EVENING   ("Evening",   "06:00 PM");

    private final String displayName;
    private final String time;

    TimeSlot(String displayName, String time) {
        this.displayName = displayName;
        this.time        = time;
    }

    public String getDisplayName() { return displayName; }
    public String getTime()        { return time;        }

    @Override
    public String toString() { return displayName + " (" + time + ")"; }
}
