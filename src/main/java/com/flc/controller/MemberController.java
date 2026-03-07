package com.flc.controller;

import com.flc.data.DataStore;
import com.flc.model.Member;
import com.flc.validation.ValidationUtil;

import java.util.List;

/**
 * Handles all member-related business logic. - Add a new member - Find members by id or name - List all members
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
     * @throws IllegalArgumentException
     *             if name or phone is invalid
     * @throws IllegalStateException
     *             if a member with the same name already exists
     *
     * @return the created Member
     */
    public Member addMember(String name, String phone) {
        ValidationUtil.validateName(name);
        ValidationUtil.validatePhone(phone);

        if (store.findMemberByName(name.trim()) != null)
            throw new IllegalStateException("A member with the name '" + name.trim() + "' already exists");

        String memberId = generateMemberId();
        Member member = new Member(memberId, name.trim(), phone.trim());
        store.addMember(member);
        return member;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // QUERIES
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Retrieves all members in the system.
     *
     * @return a list of all members (may be empty)
     */
    public List<Member> getAllMembers() {
        return store.getMembers();
    }

    /**
     * Finds a member by their unique ID.
     *
     * @param id the member's unique identifier
     * @return the member, or null if not found
     * @throws IllegalArgumentException if id is null or blank
     */
    public Member findById(String id) {
        ValidationUtil.requireNonBlank(id, "Member ID");
        return store.findMemberById(id);
    }

    /**
     * Finds a member by their exact name (case-sensitive).
     *
     * @param name the member's name
     * @return the first member with this exact name, or null if not found
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
     */
    public void updatePhone(Member member, String newPhone) {
        if (member == null)
            throw new IllegalArgumentException("Member cannot be null");
        if (newPhone == null || newPhone.isBlank())
            throw new IllegalArgumentException("Phone cannot be empty");
        member.setPhone(newPhone.trim());
    }

    /**
     * Updates a member's name.
     *
     * @throws IllegalStateException
     *             if another member already has that name
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