/**
 * Furzefield Leisure Centre (FLC) - Group Exercise Booking Management System
 *
 * A self-contained desktop application for managing group exercise bookings
 * across an 8-weekend season. Runs entirely locally - no server, no login,
 * no internet connection required.
 *
 * Module: 7COM1025 - University of Hertfordshire - Season 2025/26
 * Github: https://github.com/Dantesrc-sys/FurzefieldLC
 *
 * @author  Sandesh Karki (Dantesrc-sys)
 * @email   dashysandesh@gmail.com
 * @version 1.0
 * @since   2026
 */

package com.flc.model;

public enum TimeSlot {

    MORNING("Morning", "09:00 AM"), AFTERNOON("Afternoon", "01:00 PM"), EVENING("Evening", "06:00 PM");

    private final String displayName;
    private final String time;

    TimeSlot(String displayName, String time) {
        this.displayName = displayName;
        this.time = time;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return displayName + " (" + time + ")";
    }
}
