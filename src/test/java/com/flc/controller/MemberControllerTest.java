package com.flc.controller;

import com.flc.data.DataStore;
import com.flc.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MemberControllerTest {

    private DataStore        store;
    private MemberController controller;

    @BeforeEach
    void setUp() {
        store      = DataStore.getInstance();
        store.clearAll();
        controller = new MemberController();
    }

    // ── Add member ────────────────────────────────────────────────────────────
    @Test
    void shouldAddMemberSuccessfully() {
        Member m = controller.addMember("Alice", "07700900001");
        assertNotNull(m);
        assertEquals("Alice",       m.getName());
        assertEquals("07700900001", m.getPhone());
    }

    @Test
    void shouldGenerateUniqueMemberIds() {
        Member a = controller.addMember("Alice", "07700900001");
        Member b = controller.addMember("Bob",   "07700900002");
        assertNotEquals(a.getMemberId(), b.getMemberId());
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        assertThrows(IllegalArgumentException.class,
            () -> controller.addMember("", "07700900001"));
    }

    @Test
    void shouldThrowWhenPhoneIsBlank() {
        assertThrows(IllegalArgumentException.class,
            () -> controller.addMember("Alice", ""));
    }

    @Test
    void shouldThrowWhenDuplicateName() {
        controller.addMember("Alice", "07700900001");
        assertThrows(IllegalStateException.class,
            () -> controller.addMember("Alice", "07700900002"));
    }

    @Test
    void shouldTrimWhitespaceFromNameAndPhone() {
        Member m = controller.addMember("  Alice  ", "  07700900001  ");
        assertEquals("Alice",       m.getName());
        assertEquals("07700900001", m.getPhone());
    }

    // ── Find member ───────────────────────────────────────────────────────────
    @Test
    void shouldFindMemberById() {
        Member m = controller.addMember("Alice", "07700900001");
        assertEquals(m, controller.findById(m.getMemberId()));
    }

    @Test
    void shouldReturnNullWhenIdNotFound() {
        assertNull(controller.findById("NONE"));
    }

    @Test
    void shouldFindMemberByName() {
        Member m = controller.addMember("Alice", "07700900001");
        assertEquals(m, controller.findByName("alice")); // case-insensitive
    }

    @Test
    void shouldReturnNullWhenNameNotFound() {
        assertNull(controller.findByName("Unknown"));
    }

    // ── Get all members ───────────────────────────────────────────────────────
    @Test
    void shouldReturnAllMembers() {
        controller.addMember("Alice", "07700900001");
        controller.addMember("Bob",   "07700900002");
        assertEquals(2, controller.getAllMembers().size());
    }

    // ── Update ────────────────────────────────────────────────────────────────
    @Test
    void shouldUpdatePhone() {
        Member m = controller.addMember("Alice", "07700900001");
        controller.updatePhone(m, "07700999999");
        assertEquals("07700999999", m.getPhone());
    }

    @Test
    void shouldThrowWhenUpdatingPhoneToBlank() {
        Member m = controller.addMember("Alice", "07700900001");
        assertThrows(IllegalArgumentException.class,
            () -> controller.updatePhone(m, ""));
    }

    @Test
    void shouldUpdateName() {
        Member m = controller.addMember("Alice", "07700900001");
        controller.updateName(m, "Alice Smith");
        assertEquals("Alice Smith", m.getName());
    }

    @Test
    void shouldThrowWhenUpdatingNameToDuplicate() {
        Member alice = controller.addMember("Alice", "07700900001");
        controller.addMember("Bob", "07700900002");
        assertThrows(IllegalStateException.class,
            () -> controller.updateName(alice, "Bob"));
    }
}