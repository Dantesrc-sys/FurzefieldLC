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