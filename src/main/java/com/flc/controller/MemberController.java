package com.flc.controller;

import com.flc.data.DataStore;
import com.flc.model.Member;

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
     *             if name or phone is blank
     * @throws IllegalStateException
     *             if a member with the same name already exists
     *
     * @return the created Member
     */
    public Member addMember(String name, String phone) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be empty");
        if (phone == null || phone.isBlank())
            throw new IllegalArgumentException("Phone cannot be empty");

        if (store.findMemberByName(name) != null)
            throw new IllegalStateException("A member with the name '" + name + "' already exists");

        String memberId = generateMemberId();
        Member member = new Member(memberId, name.trim(), phone.trim());
        store.addMember(member);
        return member;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // QUERIES
    // ═══════════════════════════════════════════════════════════════════════

    public List<Member> getAllMembers() {
        return store.getMembers();
    }

    public Member findById(String id) {
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("ID cannot be empty");
        return store.findMemberById(id);
    }

    public Member findByName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be empty");
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