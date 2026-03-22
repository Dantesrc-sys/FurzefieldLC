/**
 * Furzefield Leisure Centre (FLC) - Group Exercise Booking Management System
 *
 * A self-contained desktop application for managing group exercise bookings
 * across an 8-weekend season. Runs entirely locally - no server, no login,
 * no internet connection required.
 *
 * Module: 7COM1025 - University of Hertfordshire - Season 2025/26
 *
 * @author  Sandesh Karki (Dantesrc-sys)
 * @email   dashysandesh@gmail.com
 * @version 1.0
 * @since   2026
 */

package com.flc.controller;

import com.flc.data.DataStore;
import com.flc.model.Member;
import com.flc.validation.ValidationUtil;

import java.util.List;

/**
 * Handles all member-related business logic.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Add a new member with validated and normalised name and phone</li>
 *   <li>Find members by ID or name</li>
 *   <li>Update member name and phone number</li>
 *   <li>List all members</li>
 * </ul>
 *
 * <p>Phone numbers are normalised before validation and storage using
 * {@link ValidationUtil#normalisePhone(String)}, which removes spaces,
 * hyphens, and parentheses. This ensures consistent storage regardless
 * of the format entered by the user.</p>
 */
public class MemberController {

    private final DataStore store;
    private int memberCounter;

    public MemberController() {
        this.store = DataStore.getInstance();
        this.memberCounter = store.getTotalMembers() + 1;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CREATE
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Adds a new member to the system.
     *
     * <p>The phone number is normalised before validation and storage —
     * spaces, hyphens, and parentheses are removed automatically. The name
     * is trimmed of leading and trailing whitespace.</p>
     *
     * @param name  the member's full name
     * @param phone the member's phone number (raw format accepted)
     * @return the created {@link Member}
     * @throws IllegalArgumentException if name or phone fails validation
     * @throws IllegalStateException    if a member with the same name already exists
     */
    public Member addMember(String name, String phone) {
        ValidationUtil.validateName(name);
        ValidationUtil.validatePhone(phone);

        if (store.findMemberByName(name.trim()) != null)
            throw new IllegalStateException(
                "A member with the name '" + name.trim() + "' already exists");

        String memberId = generateMemberId();
        String normalisedPhone = ValidationUtil.normalisePhone(phone);
        Member member = new Member(memberId, name.trim(), normalisedPhone);
        store.addMember(member);
        return member;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // QUERIES
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Retrieves all members in the system.
     *
     * @return an unmodifiable list of all members (may be empty)
     */
    public List<Member> getAllMembers() {
        return store.getMembers();
    }

    /**
     * Finds a member by their unique ID.
     *
     * @param id the member's unique identifier
     * @return the member, or {@code null} if not found
     * @throws IllegalArgumentException if id is null or blank
     */
    public Member findById(String id) {
        ValidationUtil.requireNonBlank(id, "Member ID");
        return store.findMemberById(id);
    }

    /**
     * Finds a member by their exact name (case-insensitive).
     *
     * @param name the member's name
     * @return the first member with this name, or {@code null} if not found
     * @throws IllegalArgumentException if name is null or blank
     */
    public Member findByName(String name) {
        ValidationUtil.requireNonBlank(name, "Name");
        return store.findMemberByName(name);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // UPDATE
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Updates a member's phone number.
     *
     * <p>The phone is normalised before storage — spaces, hyphens, and
     * parentheses are removed automatically.</p>
     *
     * @param member   the member to update
     * @param newPhone the new phone number (raw format accepted)
     * @throws IllegalArgumentException if member is null or phone is blank
     */
    public void updatePhone(Member member, String newPhone) {
        if (member == null)
            throw new IllegalArgumentException("Member cannot be null");
        if (newPhone == null || newPhone.isBlank())
            throw new IllegalArgumentException("Phone cannot be empty");
        member.setPhone(ValidationUtil.normalisePhone(newPhone));
    }

    /**
     * Updates a member's name.
     *
     * @param member  the member to update
     * @param newName the new name
     * @throws IllegalArgumentException if member is null or name is blank
     * @throws IllegalStateException    if another member already has that name
     */
    public void updateName(Member member, String newName) {
        if (member == null)
            throw new IllegalArgumentException("Member cannot be null");
        if (newName == null || newName.isBlank())
            throw new IllegalArgumentException("Name cannot be empty");

        Member existing = store.findMemberByName(newName);
        if (existing != null && !existing.equals(member))
            throw new IllegalStateException("Name '" + newName + "' is already taken");

        member.setName(newName.trim());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    private String generateMemberId() {
        return "M" + String.format("%03d", memberCounter++);
    }
}