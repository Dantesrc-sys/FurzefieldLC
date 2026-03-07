package com.flc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.flc.config.AppConfig;

/**
 * Represents a single group exercise lesson at Furzefield Leisure Centre.
 * A lesson belongs to a specific weekend (weekNumber), day, and time slot.
 * Max capacity is defined in AppConfig.MAX_LESSON_CAPACITY (4 members).
 * Members can enroll, and capacity checks are enforced during booking operations.
 */
public class Lesson {

    // ── Fields ────────────────────────────────────────────────────────────────
    private final String lessonId;
    private final ExerciseType exerciseType;
    private final Day day;
    private final TimeSlot timeSlot;
    private final int weekNumber; // 1–8 (8 weekends required)
    private final List<Member> members; // enrolled members (max 4)

    // ── Constructor ───────────────────────────────────────────────────────────
    /**
     * Creates a new Lesson with the specified exercise type, day, time slot, and week.
     *
     * @param lessonId the unique identifier for this lesson
     * @param exerciseType the type of exercise offered in this lesson
     * @param day the day of the week (SATURDAY or SUNDAY)
     * @param timeSlot the time of day (MORNING, AFTERNOON, EVENING)
     * @param weekNumber the weekend number (1-8)
     * @throws IllegalArgumentException if any required parameter is null or invalid
     */
    public Lesson(String lessonId, ExerciseType exerciseType, Day day, TimeSlot timeSlot, int weekNumber) {
        if (lessonId == null || lessonId.isBlank())
            throw new IllegalArgumentException("Lesson ID cannot be empty");
        if (exerciseType == null)
            throw new IllegalArgumentException("ExerciseType cannot be null");
        if (day == null)
            throw new IllegalArgumentException("Day cannot be null");
        if (timeSlot == null)
            throw new IllegalArgumentException("TimeSlot cannot be null");
        if (weekNumber < 1)
            throw new IllegalArgumentException("Week number must be >= 1");

        this.lessonId = lessonId;
        this.exerciseType = exerciseType;
        this.day = day;
        this.timeSlot = timeSlot;
        this.weekNumber = weekNumber;
        this.members = new ArrayList<>();
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    /**
     * Gets the unique identifier for this lesson.
     * @return the lesson ID (immutable)
     */
    public String getLessonId() {
        return lessonId;
    }

    /**
     * Gets the exercise type for this lesson.
     * @return the ExerciseType
     */
    public ExerciseType getExerciseType() {
        return exerciseType;
    }

    /**
     * Gets the day of the week for this lesson.
     * @return the Day (SATURDAY or SUNDAY)
     */
    public Day getDay() {
        return day;
    }

    /**
     * Gets the time slot for this lesson.
     * @return the TimeSlot (MORNING, AFTERNOON, or EVENING)
     */
    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    /**
     * Gets the weekend number for this lesson.
     * @return the week number (1-8)
     */
    public int getWeekNumber() {
        return weekNumber;
    }

    /**
     * Gets the price of this lesson, which is the exercise type's price.
     * @return the lesson price
     */
    public double getPrice() {
        return exerciseType.getPrice();
    }

    /**
     * Gets an unmodifiable view of enrolled members. To modify membership, use addMember or removeMember.
     * @return an unmodifiable list of enrolled members
     */
    public List<Member> getMembers() {
        return Collections.unmodifiableList(members);
    }

    // ── Capacity ──────────────────────────────────────────────────────────────
    public int getEnrolledCount() {
        return members.size();
    }

    public int getAvailableSpaces() {
        return AppConfig.MAX_LESSON_CAPACITY - members.size();
    }

    public boolean isFull() {
        return members.size() >= AppConfig.MAX_LESSON_CAPACITY;
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }

    // ── Enrolment ─────────────────────────────────────────────────────────────
    /**
     * Adds a member to this lesson.
     *
     * @throws IllegalStateException
     *             if the lesson is already full
     * @throws IllegalArgumentException
     *             if the member is already enrolled
     */
    public void addMember(Member member) {
        if (member == null)
            throw new IllegalArgumentException("Member cannot be null");
        if (isFull())
            throw new IllegalStateException("Lesson is full");
        if (hasMember(member))
            throw new IllegalArgumentException("Member already enrolled in this lesson");
        members.add(member);
    }

    /**
     * Removes a member from this lesson.
     *
     * @throws IllegalArgumentException
     *             if the member is not enrolled
     */
    public void removeMember(Member member) {
        if (member == null)
            throw new IllegalArgumentException("Member cannot be null");
        if (!hasMember(member))
            throw new IllegalArgumentException("Member not enrolled in this lesson");
        members.remove(member);
    }

    /** Returns true if the given member is enrolled in this lesson */
    public boolean hasMember(Member member) {
        return members.contains(member);
    }

    // ── Income ────────────────────────────────────────────────────────────────
    /** Total income from this single lesson (enrolled count × price) */
    public double getTotalIncome() {
        return members.size() * exerciseType.getPrice();
    }

    // ── Utility ───────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format("Lesson{id='%s', exercise='%s', week=%d, %s, %s, enrolled=%d/%d}", lessonId,
                exerciseType.getName(), weekNumber, day.getDisplayName(), timeSlot.getDisplayName(), members.size(),
                AppConfig.MAX_LESSON_CAPACITY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Lesson l))
            return false;
        return lessonId.equals(l.lessonId);
    }

    @Override
    public int hashCode() {
        return lessonId.hashCode();
    }
}