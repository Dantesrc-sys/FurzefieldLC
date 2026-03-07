package com.flc.controller;

import com.flc.data.DataStore;
import com.flc.data.SampleData;
import com.flc.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class ReportControllerTest {

    private DataStore store;
    private ReportController reportController;

    @BeforeEach
    void setUp() {
        SampleData.load(); // loads all 48 lessons, 10 members, 50 bookings, 22 reviews
        store = DataStore.getInstance();
        reportController = new ReportController();
    }

    // ── Attendance report ─────────────────────────────────────────────────────
    @Test
    void shouldReturn48RowsInAttendanceReport() {
        List<ReportController.AttendanceRow> rows = reportController.getAttendanceReport();
        assertEquals(48, rows.size());
    }

    @Test
    void shouldSortAttendanceReportByWeekThenDayThenSlot() {
        List<ReportController.AttendanceRow> rows = reportController.getAttendanceReport();
        ReportController.AttendanceRow first = rows.get(0);
        assertEquals(1, first.weekNumber());
        assertEquals(Day.SATURDAY, first.day());
        assertEquals(TimeSlot.MORNING, first.timeSlot());
    }

    @Test
    void shouldShowCorrectEnrolledCountInAttendanceReport() {
        // Week 1 Saturday Morning (L01SAT1) has Alice, Bob, Carol = 3 members
        List<ReportController.AttendanceRow> rows = reportController.getAttendanceReport();
        ReportController.AttendanceRow firstRow = rows.get(0);
        assertEquals(3, firstRow.enrolledCount());
    }

    @Test
    void shouldShowAverageRatingInAttendanceReport() {
        // L01SAT1 has reviews R001(5), R002(4), R003(5) → avg = 4.67
        List<ReportController.AttendanceRow> rows = reportController.getAttendanceReport();
        ReportController.AttendanceRow firstRow = rows.get(0);
        assertEquals(4.67, firstRow.averageRating(), 0.01);
    }

    @Test
    void shouldShowNoReviewsWhenLessonHasNone() {
        List<ReportController.AttendanceRow> rows = reportController.getAttendanceReport();
        // Find a lesson with no reviews — week 8 lessons have none
        ReportController.AttendanceRow noReview = rows.stream()
                .filter(r -> r.weekNumber() == 8 && r.averageRating() == 0.0).findFirst().orElse(null);
        assertNotNull(noReview);
        assertEquals("No reviews", noReview.formattedRating());
    }

    // ── Income report ─────────────────────────────────────────────────────────
    @Test
    void shouldReturn5RowsInIncomeReport() {
        List<ReportController.IncomeRow> rows = reportController.getIncomeReport();
        assertEquals(5, rows.size());
    }

    @Test
    void shouldSortIncomeReportHighestFirst() {
        List<ReportController.IncomeRow> rows = reportController.getIncomeReport();
        for (int i = 0; i < rows.size() - 1; i++) {
            assertTrue(rows.get(i).totalIncome() >= rows.get(i + 1).totalIncome());
        }
    }

    @Test
    void shouldIdentifyHighestIncomeExercise() {
        ReportController.IncomeRow top = reportController.getHighestIncomeExercise();
        assertNotNull(top);
        // Yoga has most lessons (16) at £12 = up to £192 max
        // Just verify it returns the one with the highest income
        List<ReportController.IncomeRow> rows = reportController.getIncomeReport();
        assertEquals(rows.get(0).exerciseName(), top.exerciseName());
    }

    @Test
    void shouldFormatIncomeCorrectly() {
        ReportController.IncomeRow top = reportController.getHighestIncomeExercise();
        assertTrue(top.formattedIncome().startsWith("£"));
    }

    @Test
    void shouldReturnNullHighestIncomeWhenNoLessons() {
        store.clearAll();
        ReportController emptyController = new ReportController();
        assertNull(emptyController.getHighestIncomeExercise());
    }

    // ── Manual data test ──────────────────────────────────────────────────────
    @Test
    void shouldCalculateTotalIncomeCorrectly() {
        store.clearAll();
        DataStore ds = DataStore.getInstance();
        ExerciseType yoga = new ExerciseType("E001", "Yoga", 10.00);
        ExerciseType zumba = new ExerciseType("E002", "Zumba", 8.00);
        ds.addExerciseType(yoga);
        ds.addExerciseType(zumba);

        Member alice = new Member("M001", "Alice", "07700900001");
        Member bob = new Member("M002", "Bob", "07700900002");
        ds.addMember(alice);
        ds.addMember(bob);

        Lesson l1 = new Lesson("L001", yoga, Day.SATURDAY, TimeSlot.MORNING, 1);
        Lesson l2 = new Lesson("L002", zumba, Day.SATURDAY, TimeSlot.AFTERNOON, 1);
        l1.addMember(alice);
        l1.addMember(bob); // 2 × £10 = £20
        l2.addMember(alice); // 1 × £8 = £8
        ds.addLesson(l1);
        ds.addLesson(l2);

        ReportController rc = new ReportController();
        List<ReportController.IncomeRow> rows = rc.getIncomeReport();

        ReportController.IncomeRow yogaRow = rows.stream().filter(r -> r.exerciseName().equals("Yoga")).findFirst()
                .orElseThrow();
        ReportController.IncomeRow zumbaRow = rows.stream().filter(r -> r.exerciseName().equals("Zumba")).findFirst()
                .orElseThrow();

        assertEquals(20.0, yogaRow.totalIncome(), 0.001);
        assertEquals(8.0, zumbaRow.totalIncome(), 0.001);
        assertEquals("Yoga", rows.get(0).exerciseName()); // yoga is highest
    }
}