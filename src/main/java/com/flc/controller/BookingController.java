package com.flc.controller;

import com.flc.data.DataStore;
import com.flc.model.*;
import com.flc.validation.ValidationUtil;

import java.util.List;

/**
 * Handles all booking-related business logic. - Create a booking (checks capacity + time conflicts) - Change a booking
 * (checks capacity + time conflicts) - Cancel a booking - Query bookings
 */
public class BookingController {

    private final DataStore store;
    private int bookingCounter;

    public BookingController() {
        this.store = DataStore.getInstance();
        this.bookingCounter = store.getTotalBookings() + 1;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CREATE
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Books a member into a lesson.
     *
     * @throws IllegalStateException
     *             if lesson is full
     * @throws IllegalStateException
     *             if member already booked in this lesson
     * @throws IllegalStateException
     *             if member has a time conflict on the same day/week/slot
     *
     * @return the created Booking
     */
    public Booking createBooking(Member member, Lesson lesson) {
        ValidationUtil.requireNonNull(member, "Member");
        ValidationUtil.requireNonNull(lesson, "Lesson");

        if (lesson.isFull())
            throw new IllegalStateException("Lesson is full: " + lesson.getLessonId());

        if (lesson.hasMember(member))
            throw new IllegalStateException("Member already booked in this lesson");

        if (hasTimeConflict(member, lesson))
            throw new IllegalStateException(
                    "Time conflict: member already has a booking on " + lesson.getDay().getDisplayName() + " "
                            + lesson.getTimeSlot().getDisplayName() + " in week " + lesson.getWeekNumber());

        lesson.addMember(member);
        String bookingId = generateBookingId();
        Booking booking = new Booking(bookingId, member, lesson);
        store.addBooking(booking);
        return booking;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CHANGE
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Moves an existing booking to a different lesson.
     *
     * @throws IllegalStateException
     *             if new lesson is full
     * @throws IllegalStateException
     *             if member has a time conflict with new lesson
     * @throws IllegalStateException
     *             if member is already booked in new lesson
     */
    public void changeBooking(Booking booking, Lesson newLesson) {
        ValidationUtil.requireNonNull(booking, "Booking");
        ValidationUtil.requireNonNull(newLesson, "New lesson");

        Lesson oldLesson = booking.getLesson();
        Member member = booking.getMember();

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
                    "Time conflict: member already has a booking on " + newLesson.getDay().getDisplayName() + " "
                            + newLesson.getTimeSlot().getDisplayName() + " in week " + newLesson.getWeekNumber());
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
        ValidationUtil.requireNonNull(booking, "Booking");
        booking.getLesson().removeMember(booking.getMember());
        store.removeBooking(booking);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // QUERIES
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Retrieves all bookings made by a specific member.
     *
     * @param member the member to query
     * @return a list of bookings for this member (may be empty)
     * @throws IllegalArgumentException if member is null
     */
    public List<Booking> getBookingsForMember(Member member) {
        ValidationUtil.requireNonNull(member, "Member");
        return store.findBookingsByMember(member);
    }

    /**
     * Retrieves all bookings in a specific lesson.
     *
     * @param lesson the lesson to query
     * @return a list of bookings for this lesson (may be empty)
     * @throws IllegalArgumentException if lesson is null
     */
    public List<Booking> getBookingsForLesson(Lesson lesson) {
        ValidationUtil.requireNonNull(lesson, "Lesson");
        return store.findBookingsByLesson(lesson);
    }

    /**
     * Retrieves all lessons that are not yet full.
     *
     * @return a list of available lessons (lessons with capacity &lt; max)
     */
    public List<Lesson> getAvailableLessons() {
        return store.getLessons().stream().filter(l -> !l.isFull()).toList();
    }

    /**
     * Retrieves all available (not full) lessons on a specific day.
     *
     * @param day the day of the week to filter by
     * @return a list of available lessons on this day (may be empty)
     * @throws IllegalArgumentException if day is null
     */
    public List<Lesson> getAvailableLessonsByDay(Day day) {
        ValidationUtil.requireNonNull(day, "Day");
        return store.findLessonsByDay(day).stream().filter(l -> !l.isFull()).toList();
    }

    /**
     * Retrieves all lessons on a specific day, including full lessons.
     *
     * @param day the day of the week to filter by
     * @return a list of all lessons on this day (may be empty)
     * @throws IllegalArgumentException if day is null
     */
    public List<Lesson> getLessonsByDay(Day day) {
        ValidationUtil.requireNonNull(day, "Day");
        return store.findLessonsByDay(day);
    }

    /**
     * Retrieves all lessons for a specific exercise type.
     *
     * @param name the name of the exercise type to filter by
     * @return a list of lessons offering this exercise (may be empty)
     * @throws IllegalArgumentException if name is null or blank
     */
    public List<Lesson> getLessonsByExerciseName(String name) {
        ValidationUtil.requireNonBlank(name, "Exercise name");
        return store.findLessonsByExerciseName(name);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Returns true if the member already has a booking on the same week + day + time slot as the given lesson.
     */
    public boolean hasTimeConflict(Member member, Lesson lesson) {
        return store.findBookingsByMember(member).stream()
                .anyMatch(b -> b.getLesson().getWeekNumber() == lesson.getWeekNumber()
                        && b.getLesson().getDay() == lesson.getDay()
                        && b.getLesson().getTimeSlot() == lesson.getTimeSlot());
    }

    private String generateBookingId() {
        return "B" + String.format("%03d", bookingCounter++);
    }
}