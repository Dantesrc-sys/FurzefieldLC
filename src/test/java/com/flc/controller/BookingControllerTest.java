package com.flc.controller;

import com.flc.data.DataStore;
import com.flc.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookingControllerTest {

    private DataStore         store;
    private BookingController controller;
    private Member            alice;
    private Member            bob;
    private Lesson            satMorning;
    private Lesson            satAfternoon;
    private Lesson            sunMorning;

    @BeforeEach
    void setUp() {
        store      = DataStore.getInstance();
        store.clearAll();
        controller = new BookingController();

        ExerciseType yoga  = new ExerciseType("E001", "Yoga",  12.00);
        ExerciseType zumba = new ExerciseType("E002", "Zumba", 10.00);
        store.addExerciseType(yoga);
        store.addExerciseType(zumba);

        alice        = new Member("M001", "Alice", "07700900001");
        bob          = new Member("M002", "Bob",   "07700900002");
        store.addMember(alice);
        store.addMember(bob);

        satMorning   = new Lesson("L001", yoga,  Day.SATURDAY, TimeSlot.MORNING,   1);
        satAfternoon = new Lesson("L002", zumba, Day.SATURDAY, TimeSlot.AFTERNOON, 1);
        sunMorning   = new Lesson("L003", yoga,  Day.SUNDAY,   TimeSlot.MORNING,   1);
        store.addLesson(satMorning);
        store.addLesson(satAfternoon);
        store.addLesson(sunMorning);
    }

    // ── Create booking ────────────────────────────────────────────────────────
    @Test
    void shouldCreateBookingSuccessfully() {
        Booking b = controller.createBooking(alice, satMorning);
        assertNotNull(b);
        assertEquals(alice,      b.getMember());
        assertEquals(satMorning, b.getLesson());
        assertTrue(satMorning.hasMember(alice));
    }

    @Test
    void shouldThrowWhenLessonIsFull() {
        satMorning.addMember(new Member("M003", "C", "07700900003"));
        satMorning.addMember(new Member("M004", "D", "07700900004"));
        satMorning.addMember(new Member("M005", "E", "07700900005"));
        satMorning.addMember(new Member("M006", "F", "07700900006"));
        assertThrows(IllegalStateException.class,
            () -> controller.createBooking(alice, satMorning));
    }

    @Test
    void shouldThrowWhenMemberAlreadyBooked() {
        controller.createBooking(alice, satMorning);
        assertThrows(IllegalStateException.class,
            () -> controller.createBooking(alice, satMorning));
    }

    @Test
    void shouldThrowOnTimeConflict() {
        controller.createBooking(alice, satMorning);
        // Another lesson same week + day + slot
        Lesson satMorning2 = new Lesson("L004", store.findExerciseTypeById("E002"),
                Day.SATURDAY, TimeSlot.MORNING, 1);
        store.addLesson(satMorning2);
        assertThrows(IllegalStateException.class,
            () -> controller.createBooking(alice, satMorning2));
    }

    @Test
    void shouldAllowBookingDifferentDaysSameWeek() {
        controller.createBooking(alice, satMorning);
        assertDoesNotThrow(() -> controller.createBooking(alice, sunMorning));
    }

    @Test
    void shouldAllowBookingDifferentSlotSameDay() {
        controller.createBooking(alice, satMorning);
        assertDoesNotThrow(() -> controller.createBooking(alice, satAfternoon));
    }

    // ── Change booking ────────────────────────────────────────────────────────
    @Test
    void shouldChangeBookingSuccessfully() {
        Booking b = controller.createBooking(alice, satMorning);
        controller.changeBooking(b, satAfternoon);
        assertEquals(satAfternoon, b.getLesson());
        assertFalse(satMorning.hasMember(alice));
        assertTrue(satAfternoon.hasMember(alice));
    }

    @Test
    void shouldThrowWhenChangingToSameLesson() {
        Booking b = controller.createBooking(alice, satMorning);
        assertThrows(IllegalStateException.class,
            () -> controller.changeBooking(b, satMorning));
    }

    @Test
    void shouldThrowWhenChangingToFullLesson() {
        satAfternoon.addMember(new Member("M003", "C", "07700900003"));
        satAfternoon.addMember(new Member("M004", "D", "07700900004"));
        satAfternoon.addMember(new Member("M005", "E", "07700900005"));
        satAfternoon.addMember(new Member("M006", "F", "07700900006"));
        Booking b = controller.createBooking(alice, satMorning);
        assertThrows(IllegalStateException.class,
            () -> controller.changeBooking(b, satAfternoon));
    }

    @Test
    void shouldRestoreMemberOnConflictDuringChange() {
        // Alice in satMorning, bob in satAfternoon (same slot week 1)
        controller.createBooking(alice, satMorning);
        // create a same-slot conflict lesson
        Lesson conflict = new Lesson("L005", store.findExerciseTypeById("E001"),
                Day.SATURDAY, TimeSlot.MORNING, 1);
        store.addLesson(conflict);
        controller.createBooking(bob, conflict);

        Booking aliceBooking = store.findBookingsByMember(alice).get(0);
        // try to move alice to conflict — should fail and alice stays in satMorning
        assertThrows(IllegalStateException.class,
            () -> controller.changeBooking(aliceBooking, conflict));
        assertTrue(satMorning.hasMember(alice)); // restored
    }

    // ── Cancel booking ────────────────────────────────────────────────────────
    @Test
    void shouldCancelBookingSuccessfully() {
        Booking b = controller.createBooking(alice, satMorning);
        controller.cancelBooking(b);
        assertFalse(satMorning.hasMember(alice));
        assertNull(store.findBookingById(b.getBookingId()));
    }

    // ── Queries ───────────────────────────────────────────────────────────────
    @Test
    void shouldReturnAvailableLessons() {
        var available = controller.getAvailableLessons();
        assertEquals(3, available.size());
    }

    @Test
    void shouldReturnLessonsByDay() {
        var sat = controller.getLessonsByDay(Day.SATURDAY);
        assertEquals(2, sat.size());
    }

    @Test
    void shouldReturnLessonsByExerciseName() {
        var yoga = controller.getLessonsByExerciseName("Yoga");
        assertEquals(2, yoga.size()); // satMorning + sunMorning
    }

    @Test
    void shouldDetectTimeConflict() {
        controller.createBooking(alice, satMorning);
        assertTrue(controller.hasTimeConflict(alice, satMorning));
        assertFalse(controller.hasTimeConflict(alice, satAfternoon));
    }
}