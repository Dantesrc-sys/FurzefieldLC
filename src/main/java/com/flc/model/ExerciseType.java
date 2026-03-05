package com.flc.model;

/**
 * Represents a type of group exercise offered at Furzefield Leisure Centre.
 * Price stays the same regardless of time slot or day.
 */
public class ExerciseType {

    // ── Fields ────────────────────────────────────────────────────────────────
    private final String exerciseId;
    private String name;
    private double price;

    // ── Constructor ───────────────────────────────────────────────────────────
    public ExerciseType(String exerciseId, String name, double price) {
        if (exerciseId == null || exerciseId.isBlank()) throw new IllegalArgumentException("Exercise ID cannot be empty");
        if (name       == null || name.isBlank())       throw new IllegalArgumentException("Name cannot be empty");
        if (price < 0)                                  throw new IllegalArgumentException("Price cannot be negative");

        this.exerciseId = exerciseId;
        this.name       = name;
        this.price      = price;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String getExerciseId() { return exerciseId; }
    public String getName()       { return name;       }
    public double getPrice()      { return price;      }

    // ── Setters (id is immutable) ─────────────────────────────────────────────
    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be empty");
        this.name = name;
    }

    public void setPrice(double price) {
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        this.price = price;
    }

    // ── Utility ───────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return name + " (£" + String.format("%.2f", price) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExerciseType e)) return false;
        return exerciseId.equals(e.exerciseId);
    }

    @Override
    public int hashCode() {
        return exerciseId.hashCode();
    }
}