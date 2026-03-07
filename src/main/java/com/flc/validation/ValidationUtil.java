package com.flc.validation;

import java.util.regex.Pattern;

/**
 * Common validation utilities for the application.
 * Provides reusable validation methods to ensure data integrity.
 */
public final class ValidationUtil {

    // Phone number pattern: UK format (07700 000000 or similar)
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
     * @param name the name to validate
     * @throws IllegalArgumentException if name format is invalid
     */
    public static void validateName(String name) {
        requireNonBlank(name, "Name");
        if (!NAME_PATTERN.matcher(name.trim()).matches()) {
            throw new IllegalArgumentException("Name contains invalid characters. Only letters, spaces, hyphens, and apostrophes are allowed.");
        }
        if (name.trim().length() < 2) {
            throw new IllegalArgumentException("Name must be at least 2 characters long");
        }
        if (name.trim().length() > 50) {
            throw new IllegalArgumentException("Name cannot be longer than 50 characters");
        }
    }

    /**
     * Validates that a string matches the expected phone number format.
     *
     * @param phone the phone number to validate
     * @throws IllegalArgumentException if phone format is invalid
     */
    public static void validatePhone(String phone) {
        requireNonBlank(phone, "Phone number");
        String cleanPhone = phone.replaceAll("\\s+", ""); // Remove spaces
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            throw new IllegalArgumentException("Phone number must be 10-15 digits, optionally starting with +");
        }
    }

    /**
     * Validates that a number is within a specified range.
     *
     * @param value the value to validate
     * @param min   the minimum allowed value
     * @param max   the maximum allowed value
     * @param name  the name of the field for error messages
     * @throws IllegalArgumentException if value is outside the range
     */
    public static void validateRange(int value, int min, int max, String name) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(name + " must be between " + min + " and " + max);
        }
    }

    /**
     * Validates that a number is positive.
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
     * Validates that a double is non-negative (zero or positive).
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