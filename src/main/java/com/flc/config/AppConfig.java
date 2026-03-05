package com.flc.config;

/**
 * Application-wide string constants for Furzefield Leisure Centre.
 * All user-facing text lives here — change once, applies everywhere.
 */
public final class AppConfig {

    private AppConfig() {}

    // ── Identity ──────────────────────────────────────────────────────────────
    public static final String APP_NAME       = "Furzefield Leisure Centre";
    public static final String APP_SHORT      = "FLC";                          
    public static final String APP_SHORT_FULL = "Furzefield LC";                
    public static final String APP_SUBTITLE   = "Manage group exercise bookings, members & reports";
    public static final String APP_BADGE      = "BOOKING MANAGEMENT SYSTEM";  

    // ── Footer ────────────────────────────────────────────────────────────────
    public static final String APP_FOOTER_L   = "© 2026 Furzefield Leisure Centre";
    public static final String APP_FOOTER_R   = "7COM1025  ·  University of Hertfordshire";
    public static final String STRIP_LABEL    = "FLC · BOOKING SYSTEM";

    // ── Business rules ────────────────────────────────────────────────────────
    public static final int MAX_LESSON_CAPACITY = 4;
    public static final int RATING_MIN          = 1;
    public static final int RATING_MAX          = 5;
    public static final int LESSONS_PER_DAY     = 3;  // morning, afternoon, evening
}