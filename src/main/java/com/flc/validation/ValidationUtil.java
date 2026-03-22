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

package com.flc.validation;

import java.util.regex.Pattern;

/**
 * Common validation and normalisation utilities for the application.
 * Provides reusable methods to ensure data integrity across all layers.
 *
 * <p>All methods are static. This class cannot be instantiated.</p>
 *
 * <p>Validation methods throw {@link IllegalArgumentException} on failure.
 * Normalisation methods never throw — they only clean and return the input.</p>
 */
public final class ValidationUtil {

    // Phone number pattern: digits only after normalisation, 10-15 digits, optional leading +
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{10,15}$");

    // Name pattern: letters, spaces, hyphens, apostrophes
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s\\-']+$");

    private ValidationUtil() {
        // Utility class
    }

    /**
     * Validates that an object is not null.
     *
     * @param obj  the object to validate
     * @param name the name of the object for error messages
     * @throws IllegalArgumentException if obj is null
     */
    public static void requireNonNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }
    }

    /**
     * Validates that a string is not null or blank.
     *
     * @param str  the string to validate
     * @param name the name of the field for error messages
     * @throws IllegalArgumentException if str is null or blank
     */
    public static void requireNonBlank(String str, String name) {
        if (str == null || str.isBlank()) {
            throw new IllegalArgumentException(name + " cannot be empty");
        }
    }

    /**
     * Validates that a string matches the expected name format.
     *
     * <p>Valid names contain only letters, spaces, hyphens, and apostrophes,
     * and must be between 2 and 50 characters after trimming.</p>
     *
     * @param name the name to validate
     * @throws IllegalArgumentException if name format is invalid
     */
    public static void validateName(String name) {
        requireNonBlank(name, "Name");
        if (!NAME_PATTERN.matcher(name.trim()).matches()) {
            throw new IllegalArgumentException(
                "Name contains invalid characters. Only letters, spaces, hyphens, and apostrophes are allowed.");
        }
        if (name.trim().length() < 2) {
            throw new IllegalArgumentException("Name must be at least 2 characters long");
        }
        if (name.trim().length() > 50) {
            throw new IllegalArgumentException("Name cannot be longer than 50 characters");
        }
    }

    /**
     * Normalises a phone number string before validation or storage.
     *
     * <p>Removes all spaces, hyphens, and parentheses, then trims
     * leading and trailing whitespace. The result is suitable for passing
     * to {@link #validatePhone(String)} and for storing in a {@code Member}.</p>
     *
     * <p>Examples:</p>
     * <ul>
     *   <li>{@code "07700 900001"}  becomes {@code "07700900001"}</li>
     *   <li>{@code "07700-900001"}  becomes {@code "07700900001"}</li>
     *   <li>{@code "(07700)900001"} becomes {@code "07700900001"}</li>
     *   <li>{@code "+44 7700 900001"} becomes {@code "+447700900001"}</li>
     * </ul>
     *
     * @param phone the raw phone number string (must not be null)
     * @return the normalised phone number with formatting characters removed
     */
    public static String normalisePhone(String phone) {
        if (phone == null) return "";
        return phone.replaceAll("[\\s\\-()]", "").trim();
    }

    /**
     * Validates that a phone number string is in an acceptable format.
     *
     * <p>The phone is normalised before validation — spaces, hyphens, and
     * parentheses are stripped. The resulting string must be 10 to 15 digits
     * with an optional leading {@code +}.</p>
     *
     * <p>Call {@link #normalisePhone(String)} before storing the value to ensure
     * the stored phone matches the validated form.</p>
     *
     * @param phone the phone number to validate (raw, un-normalised)
     * @throws IllegalArgumentException if the normalised phone does not match
     *                                  the expected format
     */
    public static void validatePhone(String phone) {
        requireNonBlank(phone, "Phone number");
        String cleanPhone = normalisePhone(phone);
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            throw new IllegalArgumentException(
                "Phone number must be 10-15 digits, optionally starting with +. " +
                "Spaces, hyphens, and parentheses are removed automatically.");
        }
    }

    /**
     * Validates that a number is within a specified range.
     *
     * @param value the value to validate
     * @param min   the minimum allowed value (inclusive)
     * @param max   the maximum allowed value (inclusive)
     * @param name  the name of the field for error messages
     * @throws IllegalArgumentException if value is outside the range
     */
    public static void validateRange(int value, int min, int max, String name) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                name + " must be between " + min + " and " + max);
        }
    }

    /**
     * Validates that a number is positive (greater than zero).
     *
     * @param value the value to validate
     * @param name  the name of the field for error messages
     * @throws IllegalArgumentException if value is not positive
     */
    public static void validatePositive(int value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive");
        }
    }

    /**
     * Validates that a double is non-negative (zero or greater).
     *
     * @param value the value to validate
     * @param name  the name of the field for error messages
     * @throws IllegalArgumentException if value is negative
     */
    public static void validateNonNegative(double value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " cannot be negative");
        }
    }
}