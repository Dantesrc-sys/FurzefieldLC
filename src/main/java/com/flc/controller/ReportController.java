package com.flc.controller;

import com.flc.data.DataStore;
import com.flc.model.*;

import java.util.*;

/**
 * Generates the two reports required by the coursework:
 *
 * Report 1 — Attendance & Rating Report:
 *   For each lesson on each day, show number of members enrolled + average rating.
 *
 * Report 2 — Highest Income Report:
 *   Which exercise type generated the most total income across all lessons.
 */
public class ReportController {

    private final DataStore        store;
    private final ReviewController reviewController;

    public ReportController() {
        this.store            = DataStore.getInstance();
        this.reviewController = new ReviewController();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // REPORT 1 — Attendance & Average Rating per lesson
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Returns a list of AttendanceRow — one per lesson, sorted by
     * week → day → timeslot.
     */
    public List<AttendanceRow> getAttendanceReport() {
        List<AttendanceRow> rows = new ArrayList<>();

        for (Lesson lesson : store.getLessons()) {
            double avg = reviewController.getAverageRating(lesson);
            rows.add(new AttendanceRow(
                    lesson.getWeekNumber(),
                    lesson.getDay(),
                    lesson.getTimeSlot(),
                    lesson.getExerciseType().getName(),
                    lesson.getEnrolledCount(),
                    avg
            ));
        }

        // Sort: week → day → timeslot
        rows.sort(Comparator
                .comparingInt(AttendanceRow::weekNumber)
                .thenComparing(AttendanceRow::day)
                .thenComparing(AttendanceRow::timeSlot));

        return rows;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // REPORT 2 — Highest Income by Exercise Type
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Returns a list of IncomeRow — one per exercise type, sorted
     * highest income first.
     */
    public List<IncomeRow> getIncomeReport() {
        // Map: exerciseName → total income
        Map<String, Double> incomeMap  = new LinkedHashMap<>();
        Map<String, Integer> countMap  = new LinkedHashMap<>();

        for (ExerciseType type : store.getExerciseTypes()) {
            incomeMap.put(type.getName(), 0.0);
            countMap.put(type.getName(),  0);
        }

        for (Lesson lesson : store.getLessons()) {
            String name   = lesson.getExerciseType().getName();
            double income = lesson.getTotalIncome();
            int    count  = lesson.getEnrolledCount();
            incomeMap.merge(name, income, Double::sum);
            countMap.merge(name,  count,  Integer::sum);
        }

        List<IncomeRow> rows = new ArrayList<>();
        for (ExerciseType type : store.getExerciseTypes()) {
            String name = type.getName();
            rows.add(new IncomeRow(name, type.getPrice(),
                    countMap.get(name), incomeMap.get(name)));
        }

        // Sort highest income first
        rows.sort(Comparator.comparingDouble(IncomeRow::totalIncome).reversed());
        return rows;
    }

    /**
     * Returns the single exercise type with the highest total income.
     * Returns null if there are no lessons.
     */
    public IncomeRow getHighestIncomeExercise() {
        List<IncomeRow> rows = getIncomeReport();
        return rows.isEmpty() ? null : rows.get(0);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // RECORD TYPES  (data containers for report rows)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * One row in the attendance report.
     */
    public record AttendanceRow(
            int      weekNumber,
            Day      day,
            TimeSlot timeSlot,
            String   exerciseName,
            int      enrolledCount,
            double   averageRating
    ) {
        public String formattedRating() {
            return averageRating == 0.0 ? "No reviews" :
                    String.format("%.1f / 5.0", averageRating);
        }
    }

    /**
     * One row in the income report.
     */
    public record IncomeRow(
            String exerciseName,
            double pricePerLesson,
            int    totalEnrolled,
            double totalIncome
    ) {
        public String formattedIncome() {
            return String.format("£%.2f", totalIncome);
        }
    }
}