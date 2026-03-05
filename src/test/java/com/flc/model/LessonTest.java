package com.flc.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LessonTest {

    private ExerciseType yoga;
    private Lesson       lesson;
    private Member       alice;
    private Member       bob;

    @BeforeEach
    void setUp() {
        yoga   = new ExerciseType("E001", "Yoga", 12.50);
        lesson = new Lesson("L001", yoga, Day.SATURDAY, TimeSlot.MORNING, 1);
        alice  = new Member("M001", "Alice", "07700900001");
        bob    = new Member("M002", "Bob",   "07700900002");
    }

    // ── Construction ──────────────────────────────────────────────────────────
    @Test
    void shouldCreateLessonWithValidData() {
        assertEquals("L001",          lesson.getLessonId());
        assertEquals(yoga,            lesson.getExerciseType());
        assertEquals(Day.SATURDAY,    lesson.getDay());
        assertEquals(TimeSlot.MORNING,lesson.getTimeSlot());
        assertEquals(1,               lesson.getWeekNumber());
    }

    @Test
    void shouldStartEmpty() {
        assertTrue(lesson.isEmpty());
        assertEquals(0, lesson.getEnrolledCount());
        assertEquals(4, lesson.getAvailableSpaces());
    }

    @Test
    void shouldThrowWhenExerciseTypeIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new Lesson("L002", null, Day.SATURDAY, TimeSlot.MORNING, 1));
    }

    @Test
    void shouldThrowWhenWeekNumberIsZero() {
        assertThrows(IllegalArgumentException.class,
            () -> new Lesson("L002", yoga, Day.SATURDAY, TimeSlot.MORNING, 0));
    }

    // ── Enrolment ─────────────────────────────────────────────────────────────
    @Test
    void shouldAddMember() {
        lesson.addMember(alice);
        assertTrue(lesson.hasMember(alice));
        assertEquals(1, lesson.getEnrolledCount());
        assertEquals(3, lesson.getAvailableSpaces());
    }

    @Test
    void shouldRemoveMember() {
        lesson.addMember(alice);
        lesson.removeMember(alice);
        assertFalse(lesson.hasMember(alice));
        assertEquals(0, lesson.getEnrolledCount());
    }

    @Test
    void shouldThrowWhenAddingDuplicateMember() {
        lesson.addMember(alice);
        assertThrows(IllegalArgumentException.class, () -> lesson.addMember(alice));
    }

    @Test
    void shouldThrowWhenRemovingNonEnrolledMember() {
        assertThrows(IllegalArgumentException.class, () -> lesson.removeMember(alice));
    }

    // ── Capacity ──────────────────────────────────────────────────────────────
    @Test
    void shouldBecomeFullAtFourMembers() {
        lesson.addMember(alice);
        lesson.addMember(bob);
        lesson.addMember(new Member("M003", "Carol", "07700900003"));
        lesson.addMember(new Member("M004", "Dave",  "07700900004"));
        assertTrue(lesson.isFull());
        assertEquals(0, lesson.getAvailableSpaces());
    }

    @Test
    void shouldThrowWhenAddingToFullLesson() {
        lesson.addMember(alice);
        lesson.addMember(bob);
        lesson.addMember(new Member("M003", "Carol", "07700900003"));
        lesson.addMember(new Member("M004", "Dave",  "07700900004"));
        assertThrows(IllegalStateException.class,
            () -> lesson.addMember(new Member("M005", "Eve", "07700900005")));
    }

    // ── Income ────────────────────────────────────────────────────────────────
    @Test
    void shouldReturnZeroIncomeWhenEmpty() {
        assertEquals(0.0, lesson.getTotalIncome());
    }

    @Test
    void shouldCalculateTotalIncome() {
        lesson.addMember(alice);
        lesson.addMember(bob);
        assertEquals(25.00, lesson.getTotalIncome(), 0.001);
    }

    // ── Price delegation ──────────────────────────────────────────────────────
    @Test
    void shouldReturnPriceFromExerciseType() {
        assertEquals(12.50, lesson.getPrice(), 0.001);
    }

    // ── Members list is unmodifiable ──────────────────────────────────────────
    @Test
    void shouldReturnUnmodifiableMembersList() {
        lesson.addMember(alice);
        assertThrows(UnsupportedOperationException.class,
            () -> lesson.getMembers().add(bob));
    }
}