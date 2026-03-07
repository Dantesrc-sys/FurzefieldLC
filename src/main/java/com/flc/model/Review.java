package com.flc.model;

import com.flc.config.AppConfig;

/**
 * Represents a review written by a member after attending a lesson.
 * Ratings must be 1–5 as defined in AppConfig.RATING_MIN and AppConfig.RATING_MAX.
 * Comments are optional (default to empty string if null).
 */
public class Review {

    // ── Fields ────────────────────────────────────────────────────────────────
    private final String reviewId;
    private final Member member;
    private final Lesson lesson;
    private int rating; // 1–5
    private String comment;

    // ── Constructor ───────────────────────────────────────────────────────────
    /**
     * Creates a new Review for a lesson attended by a member.
     *
     * @param reviewId the unique identifier for this review
     * @param member the member writing the review
     * @param lesson the lesson being reviewed
     * @param rating the rating from 1 to 5 (as per AppConfig)
     * @param comment optional comment text (null is converted to empty string)
     * @throws IllegalArgumentException if IDs are blank, if objects are null, or if rating is out of range
     */
    public Review(String reviewId, Member member, Lesson lesson, int rating, String comment) {
        if (reviewId == null || reviewId.isBlank())
            throw new IllegalArgumentException("Review ID cannot be empty");
        if (member == null)
            throw new IllegalArgumentException("Member cannot be null");
        if (lesson == null)
            throw new IllegalArgumentException("Lesson cannot be null");
        validateRating(rating);

        this.reviewId = reviewId;
        this.member = member;
        this.lesson = lesson;
        this.rating = rating;
        this.comment = (comment == null) ? "" : comment.trim();
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String getReviewId() {
        return reviewId;
    }

    public Member getMember() {
        return member;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    // ── Rating label ──────────────────────────────────────────────────────────
    public String getRatingLabel() {
        return switch (rating) {
        case 1 -> "Very Dissatisfied";
        case 2 -> "Dissatisfied";
        case 3 -> "Ok";
        case 4 -> "Satisfied";
        case 5 -> "Very Satisfied";
        default -> "Unknown";
        };
    }

    // ── Setters ───────────────────────────────────────────────────────────────
    public void setRating(int rating) {
        validateRating(rating);
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = (comment == null) ? "" : comment.trim();
    }

    // ── Validation ────────────────────────────────────────────────────────────
    private void validateRating(int rating) {
        if (rating < AppConfig.RATING_MIN || rating > AppConfig.RATING_MAX)
            throw new IllegalArgumentException(
                    "Rating must be between " + AppConfig.RATING_MIN + " and " + AppConfig.RATING_MAX);
    }

    // ── Utility ───────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format("Review{id='%s', member='%s', lesson='%s', rating=%d (%s)}", reviewId, member.getName(),
                lesson.getLessonId(), rating, getRatingLabel());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Review r))
            return false;
        return reviewId.equals(r.reviewId);
    }

    @Override
    public int hashCode() {
        return reviewId.hashCode();
    }
}