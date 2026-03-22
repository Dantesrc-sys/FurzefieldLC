/**
 * Furzefield Leisure Centre (FLC) - Group Exercise Booking Management System
 *
 * A self-contained desktop application for managing group exercise bookings
 * across an 8-weekend season. Runs entirely locally - no server, no login,
 * no internet connection required.
 *
 * Module: 7COM1025 - University of Hertfordshire - Season 2025/26
 *
 * @author  Sandesh Karki (Dantesrc-sys)
 * @email   dashysandesh@gmail.com
 * @version 1.0
 * @since   2026
 */

package com.flc.config;

/**
 * Application-wide configuration constants for Furzefield Leisure Centre.
 *
 * <p>All user-facing text, business rule thresholds, and structural constants
 * live here. Changing a value in this class propagates automatically to every
 * layer that references it - no other files need editing.</p>
 *
 * <p>Constants are grouped into the following sections:</p>
 * <ul>
 *   <li>Identity - application name and short labels</li>
 *   <li>Footer - copyright and module text</li>
 *   <li>Business rules - capacity limits and rating ranges</li>
 * </ul>
 *
 * <p>This is a utility class. It cannot be instantiated.</p>
 */
public final class AppConfig {

    private AppConfig() {
    }

    // ── Identity ──────────────────────────────────────────────────────────────
    public static final String APP_NAME = "Furzefield Leisure Centre";
    public static final String APP_SHORT = "FLC";
    public static final String APP_SHORT_FULL = "Furzefield LC";
    public static final String APP_SUBTITLE = "Manage group exercise bookings, members & reports";
    public static final String APP_BADGE = "BOOKING MANAGEMENT SYSTEM";

    // ── Footer ────────────────────────────────────────────────────────────────
    public static final String APP_FOOTER_L = "© 2026 Furzefield Leisure Centre";
    public static final String APP_FOOTER_R = "7COM1025  ·  University of Hertfordshire";
    public static final String STRIP_LABEL = "FLC · BOOKING SYSTEM";

    // ── Business rules ────────────────────────────────────────────────────────
    public static final int MAX_LESSON_CAPACITY = 4;
    public static final int RATING_MIN = 1;
    public static final int RATING_MAX = 5;
    public static final int LESSONS_PER_DAY = 3; // morning, afternoon, evening
}