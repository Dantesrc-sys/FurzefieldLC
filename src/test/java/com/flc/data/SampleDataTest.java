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

package com.flc.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SampleDataTest {

    @BeforeEach
    void setUp() {
        SampleData.load();
    }

    @Test
    void shouldLoadFiveExerciseTypes() {
        assertEquals(5, DataStore.getInstance().getTotalExerciseTypes());
    }

    @Test
    void shouldLoadTenMembers() {
        assertEquals(10, DataStore.getInstance().getTotalMembers());
    }

    @Test
    void shouldLoadFortyEightLessons() {
        assertEquals(48, DataStore.getInstance().getTotalLessons());
    }

    @Test
    void shouldLoadFiftyBookings() {
        assertEquals(50, DataStore.getInstance().getTotalBookings());
    }

    @Test
    void shouldLoadTwentyTwoReviews() {
        assertEquals(22, DataStore.getInstance().getTotalReviews());
    }

    @Test
    void shouldHaveSixLessonsPerWeek() {
        assertEquals(6, DataStore.getInstance().findLessonsByWeek(1).size());
    }

    @Test
    void shouldFindYogaLessons() {
        var yogaLessons = DataStore.getInstance().findLessonsByExerciseName("Yoga");
        assertEquals(16, yogaLessons.size()); // 8 weeks × 2 yoga slots (SAT morning + SUN evening)
    }

    @Test
    void shouldFindSaturdayLessons() {
        var satLessons = DataStore.getInstance().findLessonsByDay(com.flc.model.Day.SATURDAY);
        assertEquals(24, satLessons.size()); // 8 weeks × 3 slots
    }
}