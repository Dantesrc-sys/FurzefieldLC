/**
 * Furzefield Leisure Centre (FLC) - Group Exercise Booking Management System
 *
 * A self-contained desktop application for managing group exercise bookings
 * across an 8-weekend season. Runs entirely locally - no server, no login,
 * no internet connection required.
 *
 * Module: 7COM1025 - University of Hertfordshire - Season 2025/26
 *
 * @author  Sandesh Karki (Dantesrc-sys)
 * @email   dashysandesh@gmail.com
 * @version 1.0
 * @since   2026
 */

package com.flc.model;

/**
 * Represents a confirmed booking linking one member to one lesson.
 *
 * <p>A booking is the join between a {@link Member} and a {@link Lesson}.
 * The member reference is immutable, but the lesson reference is mutable to
 * allow the booking to be moved to a different lesson via
 * {@link #changeLesson(Lesson)}.</p>
 *
 * <p>All business rules governing booking creation and changes are enforced
 * by {@link com.flc.controller.BookingController}, not by this class directly.
 * This class validates only structural constraints such as null checks and
 * capacity on the new lesson.</p>
 *
 * <p>Equality is based solely on {@code bookingId}.</p>
 *
 * @see com.flc.controller.BookingController
 */
public class Booking {

    // ── Fields ────────────────────────────────────────────────────────────────
    private final String bookingId;
    private final Member member;
    private Lesson lesson; // mutable - member can change their booking

    // ── Constructor ───────────────────────────────────────────────────────────
    /**
     * Creates a new Booking linking a member to a lesson.
     *
     * @param bookingId the unique identifier for this booking
     * @param member the member making the booking
     * @param lesson the lesson being booked
     * @throws IllegalArgumentException if any parameter is null or if IDs are blank
     */
    public Booking(String bookingId, Member member, Lesson lesson) {
        if (bookingId == null || bookingId.isBlank())
            throw new IllegalArgumentException("Booking ID cannot be empty");
        if (member == null)
            throw new IllegalArgumentException("Member cannot be null");
        if (lesson == null)
            throw new IllegalArgumentException("Lesson cannot be null");

        this.bookingId = bookingId;
        this.member = member;
        this.lesson = lesson;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    /**
     * Gets the unique identifier for this booking.
     * @return the booking ID (immutable)
     */
    public String getBookingId() {
        return bookingId;
    }

    public Member getMember() {
        return member;
    }

    public Lesson getLesson() {
        return lesson;
    }

    // ── Change booking ────────────────────────────────────────────────────────
    /**
     * Moves this booking to a different lesson. Caller (BookingController) is responsible for: - removing member from
     * old lesson - adding member to new lesson - checking no time conflict exists
     */
    public void changeLesson(Lesson newLesson) {
        if (newLesson == null)
            throw new IllegalArgumentException("New lesson cannot be null");
        if (newLesson.isFull())
            throw new IllegalStateException("New lesson is full");
        this.lesson = newLesson;
    }

    // ── Utility ───────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format("Booking{id='%s', member='%s', lesson='%s'}", bookingId, member.getName(),
                lesson.getLessonId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Booking b))
            return false;
        return bookingId.equals(b.bookingId);
    }

    @Override
    public int hashCode() {
        return bookingId.hashCode();
    }
}