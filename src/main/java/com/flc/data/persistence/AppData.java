package com.flc.data.persistence;

import java.util.List;

/**
 * Plain data wrapper serialised to / from flc-data.json. Uses flat IDs instead of object references to avoid circular
 * references.
 */
public class AppData {

    // ── Raw flat records ──────────────────────────────────────────────────────
    public List<MemberDto> members;
    public List<ExerciseTypeDto> exerciseTypes;
    public List<LessonDto> lessons;
    public List<BookingDto> bookings;
    public List<ReviewDto> reviews;

    // ═══════════════════════════════════════════════════════════════════════
    // DTOs (Data Transfer Objects — plain fields only, no behaviour)
    // ═══════════════════════════════════════════════════════════════════════

    public static class MemberDto {
        public String memberId;
        public String name;
        public String phone;
    }

    public static class ExerciseTypeDto {
        public String exerciseId;
        public String name;
        public double price;
    }

    public static class LessonDto {
        public String lessonId;
        public String exerciseTypeId; // FK → ExerciseTypeDto
        public String day; // enum name
        public String timeSlot; // enum name
        public int weekNumber;
        public List<String> memberIds; // FK list → MemberDto
    }

    public static class BookingDto {
        public String bookingId;
        public String memberId; // FK → MemberDto
        public String lessonId; // FK → LessonDto
    }

    public static class ReviewDto {
        public String reviewId;
        public String memberId; // FK → MemberDto
        public String lessonId; // FK → LessonDto
        public int rating;
        public String comment;
    }
}