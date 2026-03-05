package com.flc.controller;

import com.flc.data.DataStore;
import com.flc.model.*;

import java.util.List;

/**
 * Handles all booking-related business logic.
 * - Create a booking (checks capacity + time conflicts)
 * - Change a booking (checks capacity + time conflicts)
 * - Cancel a booking
 * - Query bookings
 */
public class BookingController {

    private final DataStore store;
    private int bookingCounter;

    public BookingController() {
        this.store          = DataStore.getInstance();
        this.bookingCounter = store.getTotalBookings() + 1;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CREATE
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Books a member into a lesson.
     * @throws IllegalStateException    if lesson is full
     * @throws IllegalStateException    if member already booked in this lesson
     * @throws IllegalStateException    if member has a time conflict on the same day/week/slot
     * @return the created Booking
     */
    public Booking createBooking(Member member, Lesson lesson) {
        validateNotNull(member, "Member");
        validateNotNull(lesson, "Lesson");

        if (lesson.isFull())
            throw new IllegalStateException("Lesson is full: " + lesson.getLessonId());

        if (lesson.hasMember(member))
            throw new IllegalStateException("Member already booked in this lesson");

        if (hasTimeConflict(member, lesson))
            throw new IllegalStateException(
                "Time conflict: member already has a booking on "
                + lesson.getDay().getDisplayName()
                + " " + lesson.getTimeSlot().getDisplayName()
                + " in week " + lesson.getWeekNumber());

        lesson.addMember(member);
        String bookingId = generateBookingId();
        Booking booking  = new Booking(bookingId, member, lesson);
        store.addBooking(booking);
        return booking;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CHANGE
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Moves an existing booking to a different lesson.
     * @throws IllegalStateException if new lesson is full
     * @throws IllegalStateException if member has a time conflict with new lesson
     * @throws IllegalStateException if member is already booked in new lesson
     */
    public void changeBooking(Booking booking, Lesson newLesson) {
        validateNotNull(booking,   "Booking");
        validateNotNull(newLesson, "New lesson");

        Lesson oldLesson = booking.getLesson();
        Member member    = booking.getMember();

        if (oldLesson.equals(newLesson))
            throw new IllegalStateException("New lesson is the same as the current lesson");

        if (newLesson.isFull())
            throw new IllegalStateException("New lesson is full: " + newLesson.getLessonId());

        if (newLesson.hasMember(member))
            throw new IllegalStateException("Member is already booked in the new lesson");

        // Temporarily remove from old lesson before conflict check
        // so the old slot does not count as a conflict
        oldLesson.removeMember(member);

        if (hasTimeConflict(member, newLesson)) {
            // Roll back — put member back in old lesson
            oldLesson.addMember(member);
            throw new IllegalStateException(
                "Time conflict: member already has a booking on "
                + newLesson.getDay().getDisplayName()
                + " " + newLesson.getTimeSlot().getDisplayName()
                + " in week " + newLesson.getWeekNumber());
        }

        newLesson.addMember(member);
        booking.changeLesson(newLesson);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CANCEL
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Cancels a booking and removes the member from the lesson.
     */
    public void cancelBooking(Booking booking) {
        validateNotNull(booking, "Booking");
        booking.getLesson().removeMember(booking.getMember());
        store.removeBooking(booking);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // QUERIES
    // ═══════════════════════════════════════════════════════════════════════

    public List<Booking> getBookingsForMember(Member member) {
        validateNotNull(member, "Member");
        return store.findBookingsByMember(member);
    }

    public List<Booking> getBookingsForLesson(Lesson lesson) {
        validateNotNull(lesson, "Lesson");
        return store.findBookingsByLesson(lesson);
    }

    public List<Lesson> getAvailableLessons() {
        return store.getLessons().stream()
                .filter(l -> !l.isFull())
                .toList();
    }

    public List<Lesson> getAvailableLessonsByDay(Day day) {
        validateNotNull(day, "Day");
        return store.findLessonsByDay(day).stream()
                .filter(l -> !l.isFull())
                .toList();
    }

    public List<Lesson> getLessonsByDay(Day day) {
        validateNotNull(day, "Day");
        return store.findLessonsByDay(day);
    }

    public List<Lesson> getLessonsByExerciseName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Exercise name cannot be empty");
        return store.findLessonsByExerciseName(name);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Returns true if the member already has a booking on the same
     * week + day + time slot as the given lesson.
     */
    public boolean hasTimeConflict(Member member, Lesson lesson) {
        return store.findBookingsByMember(member).stream()
                .anyMatch(b ->
                    b.getLesson().getWeekNumber() == lesson.getWeekNumber() &&
                    b.getLesson().getDay()        == lesson.getDay()        &&
                    b.getLesson().getTimeSlot()   == lesson.getTimeSlot());
    }

    private String generateBookingId() {
        return "B" + String.format("%03d", bookingCounter++);
    }

    private void validateNotNull(Object obj, String name) {
        if (obj == null) throw new IllegalArgumentException(name + " cannot be null");
    }
}