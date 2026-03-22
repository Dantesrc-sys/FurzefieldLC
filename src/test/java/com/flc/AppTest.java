/**
 * Furzefield Leisure Centre (FLC) - Group Exercise Booking Management System
 *
 * A self-contained desktop application for managing group exercise bookings
 * across an 8-weekend season. Runs entirely locally - no server, no login,
 * no internet connection required.
 *
 * Module: 7COM1025 - University of Hertfordshire - Season 2025/26
 * Github: https://github.com/Dantesrc-sys/FurzefieldLC
 *
 * @author  Sandesh Karki (Dantesrc-sys)
 * @email   dashysandesh@gmail.com
 * @version 1.0
 * @since   2026
 */

package com.flc;

import com.flc.controller.BookingController;
import com.flc.controller.MemberController;
import com.flc.controller.ReportController;
import com.flc.controller.ReviewController;
import com.flc.data.DataStore;
import com.flc.data.SampleData;
import com.flc.model.Booking;
import com.flc.model.Lesson;
import com.flc.model.Member;
import com.flc.model.Review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Application-level integration tests for Furzefield Leisure Centre.
 *
 * <p>These tests exercise multiple layers together — model, controller, and data —
 * to verify that the full application stack works correctly end-to-end. Unlike
 * the unit tests in individual packages, each test here crosses at least two
 * layers and uses realistic data scenarios.</p>
 *
 * <p>Tests that use {@link SampleData#load()} reinitialise all controllers after
 * loading, because each controller's internal ID counter is initialised from the
 * current {@link DataStore} size at construction time. Reinitialising after load
 * ensures generated IDs do not collide with the already-loaded sample data.</p>
 */
class AppTest {

    private DataStore store;
    private BookingController bookingController;
    private MemberController memberController;
    private ReportController reportController;
    private ReviewController reviewController;

    @BeforeEach
    void setUp() {
        store = DataStore.getInstance();
        store.clearAll();
        bookingController = new BookingController();
        memberController  = new MemberController();
        reportController  = new ReportController();
        reviewController  = new ReviewController();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 1 — full booking lifecycle across all layers
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Verifies that a full booking lifecycle works end-to-end:
     * add a new member, create a booking, confirm it is stored in DataStore,
     * confirm the member appears in the lesson's enrolment list.
     *
     * <p>Crosses: MemberController, BookingController, DataStore, Lesson.</p>
     */
    @Test
    void fullBookingLifecycleIntegration() {
        SampleData.load();
        memberController  = new MemberController();
        bookingController = new BookingController();

        Member newMember = memberController.addMember("Test Member", "07700000099");
        assertNotNull(newMember, "New member should be created");
        assertEquals("07700000099", newMember.getPhone());

        // L01SUN3 is Week 1 Sunday Evening Yoga — empty in sample data
        Lesson emptyLesson = store.findLessonById("L01SUN3");
        assertNotNull(emptyLesson, "L01SUN3 should exist in sample data");
        assertEquals(0, emptyLesson.getEnrolledCount(),
            "L01SUN3 should start with no members enrolled");

        int bookingsBefore = store.getTotalBookings();

        Booking booking = bookingController.createBooking(newMember, emptyLesson);
        assertNotNull(booking, "Booking should be created successfully");

        assertEquals(bookingsBefore + 1, store.getTotalBookings(),
            "Booking count should increase by 1");
        assertNotNull(store.findBookingById(booking.getBookingId()),
            "Booking should be retrievable from DataStore by ID");
        assertEquals(1, emptyLesson.getEnrolledCount(),
            "Lesson enrolled count should be 1 after booking");
        assertTrue(emptyLesson.hasMember(newMember),
            "Lesson should contain the newly enrolled member");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 2 — DataStore singleton shared across all controllers
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Verifies that all controllers share the same {@link DataStore} singleton.
     * A member added via {@link MemberController} must be visible through the
     * shared store reference without any explicit data passing.
     *
     * <p>Crosses: MemberController, DataStore singleton.</p>
     */
    @Test
    void dataStoreSingletonSharedAcrossControllers() {
        assertEquals(0, store.getTotalMembers(), "Store should start empty");

        Member added = memberController.addMember("Singleton Test", "07700000001");

        assertSame(DataStore.getInstance(), store,
            "DataStore.getInstance() should always return the same instance");

        Member found = store.findMemberById(added.getMemberId());
        assertNotNull(found, "Member added via MemberController should be in DataStore");
        assertEquals("Singleton Test", found.getName(),
            "Member name retrieved from DataStore should match what was added");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 3 — income report reflects actual booking data
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Verifies that the income report is generated correctly from real sample
     * data — exactly 5 rows, sorted highest income first, with formatted income
     * strings consistent with the numeric totals.
     *
     * <p>Crosses: SampleData, DataStore, ReportController.</p>
     */
    @Test
    void reportReflectsActualBookingData() {
        SampleData.load();
        reportController = new ReportController();

        List<ReportController.IncomeRow> rows = reportController.getIncomeReport();

        assertEquals(5, rows.size(),
            "Income report should have one row per exercise type");

        for (int i = 0; i < rows.size() - 1; i++) {
            assertTrue(rows.get(i).totalIncome() >= rows.get(i + 1).totalIncome(),
                "Income report rows should be sorted descending by total income");
        }

        for (ReportController.IncomeRow row : rows) {
            assertTrue(row.pricePerLesson() > 0,
                "Every exercise type should have a positive price per lesson");
            assertTrue(row.totalEnrolled() >= 0,
                "Enrolled count should never be negative");
            assertTrue(row.totalIncome() >= 0,
                "Total income should never be negative");
            assertEquals(
                String.format("£%.2f", row.totalIncome()),
                row.formattedIncome(),
                "formattedIncome() should match totalIncome formatted to 2 decimal places"
            );
        }

        ReportController.IncomeRow top = reportController.getHighestIncomeExercise();
        assertNotNull(top, "Top earner should not be null when data is loaded");
        assertEquals(rows.get(0).exerciseName(), top.exerciseName(),
            "getHighestIncomeExercise should return the exercise with the highest income");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 4 — cancelling a booking removes member from lesson and store
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Verifies that cancelling a booking removes the member from the lesson's
     * enrolment list and removes the booking record from DataStore.
     *
     * <p>Uses Frank (M006) and booking B006 from sample data.
     * L01SAT3 (Week 1, Saturday, Evening, Box Fit) has only Frank enrolled.</p>
     *
     * <p>Crosses: SampleData, DataStore, BookingController, Lesson.</p>
     */
    @Test
    void cancelBookingRemovesMemberFromLessonAndStore() {
        SampleData.load();
        bookingController = new BookingController();

        Member frank = store.findMemberById("M006");
        assertNotNull(frank, "Frank (M006) should exist in sample data");

        Booking frankBooking = store.findBookingById("B006");
        assertNotNull(frankBooking, "Booking B006 should exist in sample data");

        Lesson lesson = frankBooking.getLesson();
        assertTrue(lesson.hasMember(frank),
            "Frank should be enrolled in the lesson before cancellation");

        int bookingsBefore = store.getTotalBookings();

        bookingController.cancelBooking(frankBooking);

        assertFalse(lesson.hasMember(frank),
            "Frank should not be enrolled in the lesson after cancellation");
        assertNull(store.findBookingById("B006"),
            "Booking B006 should not exist in DataStore after cancellation");
        assertEquals(bookingsBefore - 1, store.getTotalBookings(),
            "Total booking count should decrease by 1 after cancellation");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 5 — submitting a review updates average rating in real data
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Verifies that submitting a review via {@link ReviewController} is correctly
     * stored in {@link DataStore} and immediately reflected in the average rating
     * calculation.
     *
     * <p>Uses Frank (M006) and lesson L04SAT1 (Week 4, Saturday, Morning, Yoga).
     * Frank is enrolled in L04SAT1 via booking B026 and has no existing review
     * for this lesson in the sample data, making it a valid submission target.</p>
     *
     * <p>Crosses: SampleData, DataStore, ReviewController.</p>
     */
    @Test
    void submitReviewUpdatesAverageRatingInRealData() {
        SampleData.load();
        reviewController = new ReviewController();

        Member frank  = store.findMemberById("M006");
        Lesson lesson = store.findLessonById("L04SAT1");

        assertNotNull(frank,  "Frank (M006) should exist in sample data");
        assertNotNull(lesson, "L04SAT1 should exist in sample data");
        assertTrue(lesson.hasMember(frank),
            "Frank should be enrolled in L04SAT1 via booking B026");

        // L04SAT1 has no reviews in sample data
        assertEquals(0.0, reviewController.getAverageRating(lesson), 0.001,
            "L04SAT1 should have no reviews before submission");
        assertFalse(reviewController.hasReviewed(frank, lesson),
            "Frank should not have reviewed L04SAT1 yet");

        int reviewsBefore = store.getTotalReviews();

        // Submit a review
        Review review = reviewController.addReview(frank, lesson, 4, "Good session.");
        assertNotNull(review, "Review should be created successfully");
        assertEquals(4, review.getRating(), "Review rating should be 4");

        // Verify stored in DataStore
        assertEquals(reviewsBefore + 1, store.getTotalReviews(),
            "Review count should increase by 1");
        assertNotNull(store.findReviewById(review.getReviewId()),
            "Review should be retrievable from DataStore by ID");

        // Verify average rating reflects the new submission
        assertEquals(4.0, reviewController.getAverageRating(lesson), 0.001,
            "Average rating for L04SAT1 should be 4.0 after Frank's review");

        // Verify duplicate submission is rejected
        assertTrue(reviewController.hasReviewed(frank, lesson),
            "hasReviewed should return true after submission");
    }
}