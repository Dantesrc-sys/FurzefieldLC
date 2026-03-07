package com.flc.data;

import com.flc.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Central in-memory data store for Furzefield Leisure Centre. Holds all lists of members, exercise types, lessons,
 * bookings, and reviews. Single instance shared across the whole application (Singleton).
 */
public class DataStore {

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static final DataStore INSTANCE = new DataStore();

    public static DataStore getInstance() {
        return INSTANCE;
    }

    private DataStore() {
    }

    // ── Storage lists ─────────────────────────────────────────────────────────
    private final List<Member> members = new ArrayList<>();
    private final List<ExerciseType> exerciseTypes = new ArrayList<>();
    private final List<Lesson> lessons = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private final List<Review> reviews = new ArrayList<>();

    // ── Members ───────────────────────────────────────────────────────────────
    public void addMember(Member m) {
        if (m == null)
            throw new IllegalArgumentException("Member cannot be null");
        if (findMemberById(m.getMemberId()) != null)
            throw new IllegalArgumentException("Member ID already exists: " + m.getMemberId());
        members.add(m);
    }

    public List<Member> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public Member findMemberById(String id) {
        return members.stream().filter(m -> m.getMemberId().equals(id)).findFirst().orElse(null);
    }

    public Member findMemberByName(String name) {
        return members.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    // ── Exercise types ────────────────────────────────────────────────────────
    public void addExerciseType(ExerciseType e) {
        if (e == null)
            throw new IllegalArgumentException("ExerciseType cannot be null");
        if (findExerciseTypeById(e.getExerciseId()) != null)
            throw new IllegalArgumentException("Exercise ID already exists: " + e.getExerciseId());
        exerciseTypes.add(e);
    }

    public List<ExerciseType> getExerciseTypes() {
        return Collections.unmodifiableList(exerciseTypes);
    }

    public ExerciseType findExerciseTypeById(String id) {
        return exerciseTypes.stream().filter(e -> e.getExerciseId().equals(id)).findFirst().orElse(null);
    }

    public ExerciseType findExerciseTypeByName(String name) {
        return exerciseTypes.stream().filter(e -> e.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    // ── Lessons ───────────────────────────────────────────────────────────────
    public void addLesson(Lesson l) {
        if (l == null)
            throw new IllegalArgumentException("Lesson cannot be null");
        if (findLessonById(l.getLessonId()) != null)
            throw new IllegalArgumentException("Lesson ID already exists: " + l.getLessonId());
        lessons.add(l);
    }

    public List<Lesson> getLessons() {
        return Collections.unmodifiableList(lessons);
    }

    public Lesson findLessonById(String id) {
        return lessons.stream().filter(l -> l.getLessonId().equals(id)).findFirst().orElse(null);
    }

    public List<Lesson> findLessonsByDay(Day day) {
        return lessons.stream().filter(l -> l.getDay() == day).toList();
    }

    public List<Lesson> findLessonsByExerciseName(String name) {
        return lessons.stream().filter(l -> l.getExerciseType().getName().equalsIgnoreCase(name)).toList();
    }

    public List<Lesson> findLessonsByWeek(int weekNumber) {
        return lessons.stream().filter(l -> l.getWeekNumber() == weekNumber).toList();
    }

    public List<Lesson> findLessonsByDayAndWeek(Day day, int weekNumber) {
        return lessons.stream().filter(l -> l.getDay() == day && l.getWeekNumber() == weekNumber).toList();
    }

    // ── Bookings ──────────────────────────────────────────────────────────────
    public void addBooking(Booking b) {
        if (b == null)
            throw new IllegalArgumentException("Booking cannot be null");
        if (findBookingById(b.getBookingId()) != null)
            throw new IllegalArgumentException("Booking ID already exists: " + b.getBookingId());
        bookings.add(b);
    }

    public void removeBooking(Booking b) {
        if (b == null)
            throw new IllegalArgumentException("Booking cannot be null");
        bookings.remove(b);
    }

    public List<Booking> getBookings() {
        return Collections.unmodifiableList(bookings);
    }

    public Booking findBookingById(String id) {
        return bookings.stream().filter(b -> b.getBookingId().equals(id)).findFirst().orElse(null);
    }

    public List<Booking> findBookingsByMember(Member member) {
        return bookings.stream().filter(b -> b.getMember().equals(member)).toList();
    }

    public List<Booking> findBookingsByLesson(Lesson lesson) {
        return bookings.stream().filter(b -> b.getLesson().equals(lesson)).toList();
    }

    // ── Reviews ───────────────────────────────────────────────────────────────
    public void addReview(Review r) {
        if (r == null)
            throw new IllegalArgumentException("Review cannot be null");
        if (findReviewById(r.getReviewId()) != null)
            throw new IllegalArgumentException("Review ID already exists: " + r.getReviewId());
        reviews.add(r);
    }

    public List<Review> getReviews() {
        return Collections.unmodifiableList(reviews);
    }

    public Review findReviewById(String id) {
        return reviews.stream().filter(r -> r.getReviewId().equals(id)).findFirst().orElse(null);
    }

    public List<Review> findReviewsByLesson(Lesson lesson) {
        return reviews.stream().filter(r -> r.getLesson().equals(lesson)).toList();
    }

    public List<Review> findReviewsByMember(Member member) {
        return reviews.stream().filter(r -> r.getMember().equals(member)).toList();
    }

    // ── Utility ───────────────────────────────────────────────────────────────
    /** Clears all data — used in tests to reset state between runs */
    public void clearAll() {
        members.clear();
        exerciseTypes.clear();
        lessons.clear();
        bookings.clear();
        reviews.clear();
    }

    public int getTotalMembers() {
        return members.size();
    }

    public int getTotalLessons() {
        return lessons.size();
    }

    public int getTotalBookings() {
        return bookings.size();
    }

    public int getTotalReviews() {
        return reviews.size();
    }

    public int getTotalExerciseTypes() {
        return exerciseTypes.size();
    }
}