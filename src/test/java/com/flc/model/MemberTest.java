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

package com.flc.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    // ── Construction ──────────────────────────────────────────────────────────
    @Test
    void shouldCreateMemberWithValidData() {
        Member m = new Member("M001", "Alice Smith", "07700900001");
        assertEquals("M001", m.getMemberId());
        assertEquals("Alice Smith", m.getName());
        assertEquals("07700900001", m.getPhone());
    }

    @Test
    void shouldThrowWhenIdIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Member("", "Alice", "07700900001"));
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Member("M001", "  ", "07700900001"));
    }

    @Test
    void shouldThrowWhenPhoneIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Member("M001", "Alice", ""));
    }

    // ── Setters ───────────────────────────────────────────────────────────────
    @Test
    void shouldUpdateName() {
        Member m = new Member("M001", "Alice", "07700900001");
        m.setName("Alice Updated");
        assertEquals("Alice Updated", m.getName());
    }

    @Test
    void shouldThrowWhenSettingBlankName() {
        Member m = new Member("M001", "Alice", "07700900001");
        assertThrows(IllegalArgumentException.class, () -> m.setName(""));
    }

    // ── Equality ──────────────────────────────────────────────────────────────
    @Test
    void shouldBeEqualWhenSameId() {
        Member a = new Member("M001", "Alice", "07700900001");
        Member b = new Member("M001", "Different Name", "00000000000");
        assertEquals(a, b);
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() {
        Member a = new Member("M001", "Alice", "07700900001");
        Member b = new Member("M002", "Alice", "07700900001");
        assertNotEquals(a, b);
    }
}