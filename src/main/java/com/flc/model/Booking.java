package com.flc.model;

/**
 * Represents a booking made by a member for a specific lesson. A booking is the link between a Member and a Lesson.
 */
public class Booking {

    // ── Fields ────────────────────────────────────────────────────────────────
    private final String bookingId;
    private final Member member;
    private Lesson lesson; // mutable — member can change their booking

    // ── Constructor ───────────────────────────────────────────────────────────
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