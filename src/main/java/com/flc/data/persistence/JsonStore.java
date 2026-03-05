package com.flc.data.persistence;

import com.flc.data.DataStore;
import com.flc.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads the entire DataStore to/from a JSON file.
 *
 * File location: flc-data.json in the working directory (next to the jar).
 *
 * Strategy:
 *  - Save: convert every model object to a flat DTO (IDs only for references)
 *  - Load: read DTOs, reconstruct model objects, re-link object references
 */
public class JsonStore {

    private static final String FILE_NAME = "flc-data.json";
    private static final Gson   GSON      = new GsonBuilder().setPrettyPrinting().create();

    private JsonStore() {}

    // ═══════════════════════════════════════════════════════════════════════
    // SAVE
    // ═══════════════════════════════════════════════════════════════════════

    public static void save() {
        DataStore store = DataStore.getInstance();
        AppData   data  = new AppData();

        // Members
        data.members = new ArrayList<>();
        for (Member m : store.getMembers()) {
            AppData.MemberDto dto = new AppData.MemberDto();
            dto.memberId = m.getMemberId();
            dto.name     = m.getName();
            dto.phone    = m.getPhone();
            data.members.add(dto);
        }

        // Exercise types
        data.exerciseTypes = new ArrayList<>();
        for (ExerciseType e : store.getExerciseTypes()) {
            AppData.ExerciseTypeDto dto = new AppData.ExerciseTypeDto();
            dto.exerciseId = e.getExerciseId();
            dto.name       = e.getName();
            dto.price      = e.getPrice();
            data.exerciseTypes.add(dto);
        }

        // Lessons (store enrolled member IDs, not objects)
        data.lessons = new ArrayList<>();
        for (Lesson l : store.getLessons()) {
            AppData.LessonDto dto = new AppData.LessonDto();
            dto.lessonId      = l.getLessonId();
            dto.exerciseTypeId= l.getExerciseType().getExerciseId();
            dto.day           = l.getDay().name();
            dto.timeSlot      = l.getTimeSlot().name();
            dto.weekNumber    = l.getWeekNumber();
            dto.memberIds     = new ArrayList<>();
            for (Member m : l.getMembers()) dto.memberIds.add(m.getMemberId());
            data.lessons.add(dto);
        }

        // Bookings
        data.bookings = new ArrayList<>();
        for (Booking b : store.getBookings()) {
            AppData.BookingDto dto = new AppData.BookingDto();
            dto.bookingId = b.getBookingId();
            dto.memberId  = b.getMember().getMemberId();
            dto.lessonId  = b.getLesson().getLessonId();
            data.bookings.add(dto);
        }

        // Reviews
        data.reviews = new ArrayList<>();
        for (Review r : store.getReviews()) {
            AppData.ReviewDto dto = new AppData.ReviewDto();
            dto.reviewId = r.getReviewId();
            dto.memberId = r.getMember().getMemberId();
            dto.lessonId = r.getLesson().getLessonId();
            dto.rating   = r.getRating();
            dto.comment  = r.getComment();
            data.reviews.add(dto);
        }

        // Write to file
        try (Writer writer = new FileWriter(FILE_NAME)) {
            GSON.toJson(data, writer);
            System.out.println("[JsonStore] Saved to " + FILE_NAME);
        } catch (IOException e) {
            System.err.println("[JsonStore] Save failed: " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LOAD
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Loads data from flc-data.json if it exists.
     * @return true if loaded from file, false if file not found (caller should load SampleData)
     */
    public static boolean load() {
        Path path = Paths.get(FILE_NAME);
        if (!Files.exists(path)) {
            System.out.println("[JsonStore] No save file found — will use SampleData");
            return false;
        }

        try (Reader reader = new FileReader(FILE_NAME)) {
            AppData   data  = GSON.fromJson(reader, AppData.class);
            DataStore store = DataStore.getInstance();
            store.clearAll();

            // 1 — Members
            for (AppData.MemberDto dto : data.members)
                store.addMember(new Member(dto.memberId, dto.name, dto.phone));

            // 2 — Exercise types
            for (AppData.ExerciseTypeDto dto : data.exerciseTypes)
                store.addExerciseType(new ExerciseType(dto.exerciseId, dto.name, dto.price));

            // 3 — Lessons (re-link exercise type + enrolled members)
            for (AppData.LessonDto dto : data.lessons) {
                ExerciseType type = store.findExerciseTypeById(dto.exerciseTypeId);
                if (type == null) continue;
                Lesson lesson = new Lesson(
                        dto.lessonId, type,
                        Day.valueOf(dto.day),
                        TimeSlot.valueOf(dto.timeSlot),
                        dto.weekNumber);
                // Re-enrol members
                if (dto.memberIds != null) {
                    for (String memberId : dto.memberIds) {
                        Member m = store.findMemberById(memberId);
                        if (m != null) lesson.addMember(m);
                    }
                }
                store.addLesson(lesson);
            }

            // 4 — Bookings
            for (AppData.BookingDto dto : data.bookings) {
                Member m = store.findMemberById(dto.memberId);
                Lesson l = store.findLessonById(dto.lessonId);
                if (m != null && l != null)
                    store.addBooking(new Booking(dto.bookingId, m, l));
            }

            // 5 — Reviews
            for (AppData.ReviewDto dto : data.reviews) {
                Member m = store.findMemberById(dto.memberId);
                Lesson l = store.findLessonById(dto.lessonId);
                if (m != null && l != null)
                    store.addReview(new Review(dto.reviewId, m, l, dto.rating, dto.comment));
            }

            System.out.println("[JsonStore] Loaded from " + FILE_NAME
                    + " — " + store.getTotalMembers()    + " members, "
                    + store.getTotalLessons()             + " lessons, "
                    + store.getTotalBookings()            + " bookings, "
                    + store.getTotalReviews()             + " reviews");
            return true;

        } catch (Exception e) {
            System.err.println("[JsonStore] Load failed: " + e.getMessage());
            return false;
        }
    }

    /** Returns true if a save file already exists */
    public static boolean saveFileExists() {
        return Files.exists(Paths.get(FILE_NAME));
    }
}