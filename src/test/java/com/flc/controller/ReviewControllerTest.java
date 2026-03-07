package com.flc.controller;

import com.flc.data.DataStore;
import com.flc.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReviewControllerTest {

    private DataStore store;
    private ReviewController controller;
    private Member alice;
    private Member bob;
    private Lesson lesson;

    @BeforeEach
    void setUp() {
        store = DataStore.getInstance();
        store.clearAll();
        controller = new ReviewController();

        ExerciseType yoga = new ExerciseType("E001", "Yoga", 12.00);
        store.addExerciseType(yoga);

        alice = new Member("M001", "Alice", "07700900001");
        bob = new Member("M002", "Bob", "07700900002");
        store.addMember(alice);
        store.addMember(bob);

        lesson = new Lesson("L001", yoga, Day.SATURDAY, TimeSlot.MORNING, 1);
        store.addLesson(lesson);
        lesson.addMember(alice);
    }

    // ── Add review ────────────────────────────────────────────────────────────
    @Test
    void shouldAddReviewSuccessfully() {
        Review r = controller.addReview(alice, lesson, 5, "Loved it!");
        assertNotNull(r);
        assertEquals(alice, r.getMember());
        assertEquals(lesson, r.getLesson());
        assertEquals(5, r.getRating());
        assertEquals("Loved it!", r.getComment());
    }

    @Test
    void shouldThrowWhenMemberNotEnrolled() {
        assertThrows(IllegalStateException.class, () -> controller.addReview(bob, lesson, 4, "Good"));
    }

    @Test
    void shouldThrowWhenMemberAlreadyReviewed() {
        controller.addReview(alice, lesson, 5, "Great!");
        assertThrows(IllegalStateException.class, () -> controller.addReview(alice, lesson, 3, "Ok"));
    }

    @Test
    void shouldThrowWhenRatingOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> controller.addReview(alice, lesson, 0, "Bad rating"));
        assertThrows(IllegalArgumentException.class, () -> controller.addReview(alice, lesson, 6, "Bad rating"));
    }

    @Test
    void shouldThrowWhenMemberIsNull() {
        assertThrows(IllegalArgumentException.class, () -> controller.addReview(null, lesson, 4, "Good"));
    }

    @Test
    void shouldThrowWhenLessonIsNull() {
        assertThrows(IllegalArgumentException.class, () -> controller.addReview(alice, null, 4, "Good"));
    }

    // ── Average rating ────────────────────────────────────────────────────────
    @Test
    void shouldReturnZeroAverageWhenNoReviews() {
        assertEquals(0.0, controller.getAverageRating(lesson));
    }

    @Test
    void shouldCalculateAverageRating() {
        lesson.addMember(bob);
        controller.addReview(alice, lesson, 4, "Good");
        controller.addReview(bob, lesson, 2, "Not great");
        assertEquals(3.0, controller.getAverageRating(lesson), 0.001);
    }

    @Test
    void shouldCalculateAverageForSingleReview() {
        controller.addReview(alice, lesson, 5, "Perfect");
        assertEquals(5.0, controller.getAverageRating(lesson), 0.001);
    }

    // ── Queries ───────────────────────────────────────────────────────────────
    @Test
    void shouldReturnReviewsForLesson() {
        controller.addReview(alice, lesson, 4, "Good");
        assertEquals(1, controller.getReviewsForLesson(lesson).size());
    }

    @Test
    void shouldReturnReviewsForMember() {
        controller.addReview(alice, lesson, 4, "Good");
        assertEquals(1, controller.getReviewsForMember(alice).size());
    }

    @Test
    void shouldReturnEmptyListWhenNoReviews() {
        assertTrue(controller.getReviewsForLesson(lesson).isEmpty());
    }

    // ── hasReviewed ───────────────────────────────────────────────────────────
    @Test
    void shouldDetectAlreadyReviewed() {
        assertFalse(controller.hasReviewed(alice, lesson));
        controller.addReview(alice, lesson, 5, "Great!");
        assertTrue(controller.hasReviewed(alice, lesson));
    }
}