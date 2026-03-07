package com.flc.model;

/**
 * Represents a member of Furzefield Leisure Centre.
 * Each member has a unique ID, name, and phone number.
 * Members can enroll in lessons and write reviews.
 */
public class Member {

    // ── Fields ────────────────────────────────────────────────────────────────
    private final String memberId;
    private String name;
    private String phone;

    // ── Constructor ───────────────────────────────────────────────────────────
    /**
     * Creates a new Member with the specified ID, name, and phone number.
     *
     * @param memberId the unique identifier for this member
     * @param name the member's full name
     * @param phone the member's contact phone number
     * @throws IllegalArgumentException if any parameter is null or blank
     */
    public Member(String memberId, String name, String phone) {
        if (memberId == null || memberId.isBlank())
            throw new IllegalArgumentException("Member ID cannot be empty");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be empty");
        if (phone == null || phone.isBlank())
            throw new IllegalArgumentException("Phone cannot be empty");

        this.memberId = memberId;
        this.name = name;
        this.phone = phone;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    /**
     * Gets the unique identifier for this member.
     * @return the member ID (immutable)
     */
    public String getMemberId() {
        return memberId;
    }

    /**
     * Gets the member's full name.
     * @return the member's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the member's contact phone number.
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    // ── Setters (id is immutable) ─────────────────────────────────────────────
    /**
     * Updates the member's name.
     *
     * @param name the new name
     * @throws IllegalArgumentException if name is null or blank
     */
    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be empty");
        this.name = name;
    }

    /**
     * Updates the member's phone number.
     *
     * @param phone the new phone number
     * @throws IllegalArgumentException if phone is null or blank
     */
    public void setPhone(String phone) {
        if (phone == null || phone.isBlank())
            throw new IllegalArgumentException("Phone cannot be empty");
        this.phone = phone;
    }

    // ── Utility ───────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Member{id='" + memberId + "', name='" + name + "', phone='" + phone + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Member m))
            return false;
        return memberId.equals(m.memberId);
    }

    @Override
    public int hashCode() {
        return memberId.hashCode();
    }
}