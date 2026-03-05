package com.flc.data;

import com.flc.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DataStoreTest {

    private DataStore    store;
    private Member       alice;
    private ExerciseType yoga;
    private Lesson       lesson;

    @BeforeEach
    void setUp() {
        store  = DataStore.getInstance();
        store.clearAll();
        alice  = new Member("M001", "Alice", "07700900001");
        yoga   = new ExerciseType("E001", "Yoga", 12.50);
        lesson = new Lesson("L001", yoga, Day.SATURDAY, TimeSlot.MORNING, 1);
    }

    // ── Members ───────────────────────────────────────────────────────────────
    @Test
    void shouldAddAndFindMember() {
        store.addMember(alice);
        assertEquals(alice, store.findMemberById("M001"));
    }

    @Test
    void shouldThrowOnDuplicateMemberId() {
        store.addMember(alice);
        assertThrows(IllegalArgumentException.class,
            () -> store.addMember(new Member("M001", "Bob", "07700900002")));
    }

    @Test
    void shouldFindMemberByName() {
        store.addMember(alice);
        assertEquals(alice, store.findMemberByName("alice")); // case-insensitive
    }

    @Test
    void shouldReturnNullWhenMemberNotFound() {
        assertNull(store.findMemberById("NONE"));
    }

    // ── Exercise types ────────────────────────────────────────────────────────
    @Test
    void shouldAddAndFindExerciseType() {
        store.addExerciseType(yoga);
        assertEquals(yoga, store.findExerciseTypeById("E001"));
    }

    @Test
    void shouldFindExerciseTypeByName() {
        store.addExerciseType(yoga);
        assertEquals(yoga, store.findExerciseTypeByName("YOGA")); // case-insensitive
    }

    @Test
    void shouldThrowOnDuplicateExerciseId() {
        store.addExerciseType(yoga);
        assertThrows(IllegalArgumentException.class,
            () -> store.addExerciseType(new ExerciseType("E001", "Zumba", 10.00)));
    }

    // ── Lessons ───────────────────────────────────────────────────────────────
    @Test
    void shouldAddAndFindLesson() {
        store.addLesson(lesson);
        assertEquals(lesson, store.findLessonById("L001"));
    }

    @Test
    void shouldFindLessonsByDay() {
        store.addLesson(lesson);
        var results = store.findLessonsByDay(Day.SATURDAY);
        assertEquals(1, results.size());
        assertTrue(results.contains(lesson));
    }

    @Test
    void shouldFindLessonsByExerciseName() {
        store.addLesson(lesson);
        var results = store.findLessonsByExerciseName("yoga");
        assertEquals(1, results.size());
    }

    @Test
    void shouldFindLessonsByWeek() {
        store.addLesson(lesson);
        assertEquals(1, store.findLessonsByWeek(1).size());
        assertEquals(0, store.findLessonsByWeek(2).size());
    }

    // ── Bookings ──────────────────────────────────────────────────────────────
    @Test
    void shouldAddAndFindBooking() {
        store.addMember(alice);
        store.addLesson(lesson);
        lesson.addMember(alice);
        Booking b = new Booking("B001", alice, lesson);
        store.addBooking(b);
        assertEquals(b, store.findBookingById("B001"));
    }

    @Test
    void shouldFindBookingsByMember() {
        lesson.addMember(alice);
        Booking b = new Booking("B001", alice, lesson);
        store.addBooking(b);
        assertEquals(1, store.findBookingsByMember(alice).size());
    }

    @Test
    void shouldRemoveBooking() {
        lesson.addMember(alice);
        Booking b = new Booking("B001", alice, lesson);
        store.addBooking(b);
        store.removeBooking(b);
        assertNull(store.findBookingById("B001"));
    }

    // ── Reviews ───────────────────────────────────────────────────────────────
    @Test
    void shouldAddAndFindReview() {
        lesson.addMember(alice);
        Review r = new Review("R001", alice, lesson, 5, "Great!");
        store.addReview(r);
        assertEquals(r, store.findReviewById("R001"));
    }

    @Test
    void shouldFindReviewsByLesson() {
        lesson.addMember(alice);
        store.addReview(new Review("R001", alice, lesson, 4, "Good"));
        assertEquals(1, store.findReviewsByLesson(lesson).size());
    }

    // ── clearAll ──────────────────────────────────────────────────────────────
    @Test
    void shouldClearAllData() {
        store.addMember(alice);
        store.addExerciseType(yoga);
        store.clearAll();
        assertEquals(0, store.getTotalMembers());
        assertEquals(0, store.getTotalExerciseTypes());
    }

    // ── Counts ────────────────────────────────────────────────────────────────
    @Test
    void shouldReturnCorrectCounts() {
        store.addMember(alice);
        store.addExerciseType(yoga);
        store.addLesson(lesson);
        assertEquals(1, store.getTotalMembers());
        assertEquals(1, store.getTotalExerciseTypes());
        assertEquals(1, store.getTotalLessons());
    }
}