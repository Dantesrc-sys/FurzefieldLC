package com.flc.model;

/**
 * Represents a member of Furzefield Leisure Centre.
 */
public class Member {

    // ── Fields ────────────────────────────────────────────────────────────────
    private final String memberId;
    private String name;
    private String phone;

    // ── Constructor ───────────────────────────────────────────────────────────
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
    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    // ── Setters (id is immutable) ─────────────────────────────────────────────
    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be empty");
        this.name = name;
    }

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