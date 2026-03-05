package com.flc.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExerciseTypeTest {

    // ── Construction ──────────────────────────────────────────────────────────
    @Test
    void shouldCreateWithValidData() {
        ExerciseType e = new ExerciseType("E001", "Yoga", 12.50);
        assertEquals("E001",  e.getExerciseId());
        assertEquals("Yoga",  e.getName());
        assertEquals(12.50,   e.getPrice());
    }

    @Test
    void shouldThrowWhenIdIsBlank() {
        assertThrows(IllegalArgumentException.class,
            () -> new ExerciseType("", "Yoga", 12.50));
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        assertThrows(IllegalArgumentException.class,
            () -> new ExerciseType("E001", "", 12.50));
    }

    @Test
    void shouldThrowWhenPriceIsNegative() {
        assertThrows(IllegalArgumentException.class,
            () -> new ExerciseType("E001", "Yoga", -5.00));
    }

    @Test
    void shouldAllowZeroPrice() {
        ExerciseType e = new ExerciseType("E001", "Yoga", 0.0);
        assertEquals(0.0, e.getPrice());
    }

    // ── Setters ───────────────────────────────────────────────────────────────
    @Test
    void shouldUpdateName() {
        ExerciseType e = new ExerciseType("E001", "Yoga", 12.50);
        e.setName("Hot Yoga");
        assertEquals("Hot Yoga", e.getName());
    }

    @Test
    void shouldUpdatePrice() {
        ExerciseType e = new ExerciseType("E001", "Yoga", 12.50);
        e.setPrice(15.00);
        assertEquals(15.00, e.getPrice());
    }

    @Test
    void shouldThrowWhenSettingNegativePrice() {
        ExerciseType e = new ExerciseType("E001", "Yoga", 12.50);
        assertThrows(IllegalArgumentException.class, () -> e.setPrice(-1));
    }

    // ── toString ──────────────────────────────────────────────────────────────
    @Test
    void shouldFormatToStringWithPrice() {
        ExerciseType e = new ExerciseType("E001", "Yoga", 12.50);
        assertEquals("Yoga (£12.50)", e.toString());
    }

    // ── Equality ──────────────────────────────────────────────────────────────
    @Test
    void shouldBeEqualWhenSameId() {
        ExerciseType a = new ExerciseType("E001", "Yoga",   12.50);
        ExerciseType b = new ExerciseType("E001", "Zumba",  8.00);
        assertEquals(a, b);
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() {
        ExerciseType a = new ExerciseType("E001", "Yoga", 12.50);
        ExerciseType b = new ExerciseType("E002", "Yoga", 12.50);
        assertNotEquals(a, b);
    }
}