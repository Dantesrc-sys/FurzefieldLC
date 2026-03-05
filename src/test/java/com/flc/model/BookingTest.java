package com.flc.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    private Member       alice;
    private ExerciseType yoga;
    private Lesson       lessonA;
    private Lesson       lessonB;
    private Booking      booking;

    @BeforeEach
    void setUp() {
        alice   = new Member("M001", "Alice", "07700900001");
        yoga    = new ExerciseType("E001", "Yoga", 12.50);
        lessonA = new Lesson("L001", yoga, Day.SATURDAY, TimeSlot.MORNING,   1);
        lessonB = new Lesson("L002", yoga, Day.SATURDAY, TimeSlot.AFTERNOON, 1);

        lessonA.addMember(alice);
        booking = new Booking("B001", alice, lessonA);
    }

    // ── Construction ──────────────────────────────────────────────────────────
    @Test
    void shouldCreateBookingWithValidData() {
        assertEquals("B001", booking.getBookingId());
        assertEquals(alice,  booking.getMember());
        assertEquals(lessonA,booking.getLesson());
    }

    @Test
    void shouldThrowWhenBookingIdIsBlank() {
        assertThrows(IllegalArgumentException.class,
            () -> new Booking("", alice, lessonA));
    }

    @Test
    void shouldThrowWhenMemberIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new Booking("B001", null, lessonA));
    }

    @Test
    void shouldThrowWhenLessonIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new Booking("B001", alice, null));
    }

    // ── Change lesson ─────────────────────────────────────────────────────────
    @Test
    void shouldChangeLessonSuccessfully() {
        booking.changeLesson(lessonB);
        assertEquals(lessonB, booking.getLesson());
    }

    @Test
    void shouldThrowWhenChangingToNullLesson() {
        assertThrows(IllegalArgumentException.class,
            () -> booking.changeLesson(null));
    }

    @Test
    void shouldThrowWhenChangingToFullLesson() {
        lessonB.addMember(new Member("M002", "Bob",   "07700900002"));
        lessonB.addMember(new Member("M003", "Carol", "07700900003"));
        lessonB.addMember(new Member("M004", "Dave",  "07700900004"));
        lessonB.addMember(new Member("M005", "Eve",   "07700900005"));

        assertThrows(IllegalStateException.class,
            () -> booking.changeLesson(lessonB));
    }

    // ── Equality ──────────────────────────────────────────────────────────────
    @Test
    void shouldBeEqualWhenSameBookingId() {
        Booking b2 = new Booking("B001", alice, lessonB);
        assertEquals(booking, b2);
    }

    @Test
    void shouldNotBeEqualWhenDifferentBookingId() {
        Booking b2 = new Booking("B002", alice, lessonA);
        assertNotEquals(booking, b2);
    }
}