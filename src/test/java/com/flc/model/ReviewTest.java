package com.flc.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    private Member alice;
    private Lesson lesson;
    private Review review;

    @BeforeEach
    void setUp() {
        alice = new Member("M001", "Alice", "07700900001");
        lesson = new Lesson("L001", new ExerciseType("E001", "Yoga", 12.50), Day.SATURDAY, TimeSlot.MORNING, 1);
        lesson.addMember(alice);
        review = new Review("R001", alice, lesson, 4, "Really enjoyed it!");
    }

    // ── Construction ──────────────────────────────────────────────────────────
    @Test
    void shouldCreateReviewWithValidData() {
        assertEquals("R001", review.getReviewId());
        assertEquals(alice, review.getMember());
        assertEquals(lesson, review.getLesson());
        assertEquals(4, review.getRating());
        assertEquals("Really enjoyed it!", review.getComment());
    }

    @Test
    void shouldAcceptNullCommentAsEmptyString() {
        Review r = new Review("R002", alice, lesson, 3, null);
        assertEquals("", r.getComment());
    }

    @Test
    void shouldTrimComment() {
        Review r = new Review("R002", alice, lesson, 3, "  Great!  ");
        assertEquals("Great!", r.getComment());
    }

    @Test
    void shouldThrowWhenReviewIdIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Review("", alice, lesson, 4, "Good"));
    }

    @Test
    void shouldThrowWhenMemberIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Review("R002", null, lesson, 4, "Good"));
    }

    @Test
    void shouldThrowWhenLessonIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Review("R002", alice, null, 4, "Good"));
    }

    // ── Rating validation ─────────────────────────────────────────────────────
    @Test
    void shouldAcceptAllValidRatings() {
        for (int i = 1; i <= 5; i++) {
            final int rating = i;
            assertDoesNotThrow(() -> new Review("R00" + rating, alice, lesson, rating, ""));
        }
    }

    @Test
    void shouldThrowWhenRatingIsTooLow() {
        assertThrows(IllegalArgumentException.class, () -> new Review("R002", alice, lesson, 0, ""));
    }

    @Test
    void shouldThrowWhenRatingIsTooHigh() {
        assertThrows(IllegalArgumentException.class, () -> new Review("R002", alice, lesson, 6, ""));
    }

    // ── Rating labels ─────────────────────────────────────────────────────────
    @Test
    void shouldReturnCorrectRatingLabels() {
        assertEquals("Very Dissatisfied", new Review("R1", alice, lesson, 1, "").getRatingLabel());
        assertEquals("Dissatisfied", new Review("R2", alice, lesson, 2, "").getRatingLabel());
        assertEquals("Ok", new Review("R3", alice, lesson, 3, "").getRatingLabel());
        assertEquals("Satisfied", new Review("R4", alice, lesson, 4, "").getRatingLabel());
        assertEquals("Very Satisfied", new Review("R5", alice, lesson, 5, "").getRatingLabel());
    }

    // ── Setters ───────────────────────────────────────────────────────────────
    @Test
    void shouldUpdateRating() {
        review.setRating(5);
        assertEquals(5, review.getRating());
    }

    @Test
    void shouldThrowWhenSettingInvalidRating() {
        assertThrows(IllegalArgumentException.class, () -> review.setRating(0));
        assertThrows(IllegalArgumentException.class, () -> review.setRating(6));
    }

    @Test
    void shouldUpdateComment() {
        review.setComment("Updated comment");
        assertEquals("Updated comment", review.getComment());
    }

    // ── Equality ──────────────────────────────────────────────────────────────
    @Test
    void shouldBeEqualWhenSameReviewId() {
        Review r2 = new Review("R001", alice, lesson, 1, "Different");
        assertEquals(review, r2);
    }

    @Test
    void shouldNotBeEqualWhenDifferentReviewId() {
        Review r2 = new Review("R002", alice, lesson, 4, "Really enjoyed it!");
        assertNotEquals(review, r2);
    }
}