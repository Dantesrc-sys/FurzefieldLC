package com.flc.controller;

import com.flc.data.DataStore;
import com.flc.model.*;
import com.flc.validation.ValidationUtil;

import java.util.List;

/**
 * Handles all review-related business logic. - Add a review after attending a lesson - Query reviews by lesson or
 * member - Calculate average rating for a lesson
 */
public class ReviewController {

    private final DataStore store;
    private int reviewCounter;

    public ReviewController() {
        this.store = DataStore.getInstance();
        this.reviewCounter = store.getTotalReviews() + 1;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CREATE
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Adds a review for a lesson the member has attended.
     *
     * @throws IllegalStateException
     *             if member was not enrolled in the lesson
     * @throws IllegalStateException
     *             if member already reviewed this lesson
     * @throws IllegalArgumentException
     *             if rating is outside 1–5
     *
     * @return the created Review
     */
    public Review addReview(Member member, Lesson lesson, int rating, String comment) {
        ValidationUtil.requireNonNull(member, "Member");
        ValidationUtil.requireNonNull(lesson, "Lesson");
        ValidationUtil.validateRange(rating, 1, 5, "Rating");

        if (!lesson.hasMember(member))
            throw new IllegalStateException("Member has not attended this lesson and cannot review it");

        if (hasReviewed(member, lesson))
            throw new IllegalStateException("Member has already submitted a review for this lesson");

        String reviewId = generateReviewId();
        Review review = new Review(reviewId, member, lesson, rating, comment);
        store.addReview(review);
        return review;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // QUERIES
    // ═══════════════════════════════════════════════════════════════════════

    public List<Review> getReviewsForLesson(Lesson lesson) {
        ValidationUtil.requireNonNull(lesson, "Lesson");
        return store.findReviewsByLesson(lesson);
    }

    public List<Review> getReviewsForMember(Member member) {
        ValidationUtil.requireNonNull(member, "Member");
        return store.findReviewsByMember(member);
    }

    /**
     * Retrieves all reviews in the system.
     *
     * @return a list of all reviews (may be empty)
     */
    public List<Review> getAllReviews() {
        return store.getReviews();
    }

    /**
     * Returns the average rating for a lesson, or 0.0 if no reviews exist.
     */
    public double getAverageRating(Lesson lesson) {
        ValidationUtil.requireNonNull(lesson, "Lesson");
        List<Review> reviews = store.findReviewsByLesson(lesson);
        if (reviews.isEmpty())
            return 0.0;
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }

    /**
     * Returns true if the member has already reviewed the given lesson.
     */
    public boolean hasReviewed(Member member, Lesson lesson) {
        return store.findReviewsByLesson(lesson).stream().anyMatch(r -> r.getMember().equals(member));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    private String generateReviewId() {
        return "R" + String.format("%03d", reviewCounter++);
    }
}