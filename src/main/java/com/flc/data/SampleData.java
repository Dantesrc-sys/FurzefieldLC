package com.flc.data;

import com.flc.model.*;

/**
 * Populates the DataStore with all sample data required by the coursework:
 *  - 5 exercise types (4+ required)
 *  - 10 members (10 required)
 *  - 8 weekends × 2 days × 3 slots = 48 lessons (48 required)
 *  - bookings linking members to lessons
 *  - 20+ reviews with ratings
 */
public class SampleData {

    private SampleData() {}

    public static void load() {
        DataStore store = DataStore.getInstance();
        store.clearAll();

        loadExerciseTypes(store);
        loadMembers(store);
        loadLessons(store);
        loadBookings(store);
        loadReviews(store);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // EXERCISE TYPES  (5 types, each with a fixed price)
    // ═══════════════════════════════════════════════════════════════════════

    private static void loadExerciseTypes(DataStore store) {
        store.addExerciseType(new ExerciseType("E001", "Yoga",      12.00));
        store.addExerciseType(new ExerciseType("E002", "Zumba",     10.00));
        store.addExerciseType(new ExerciseType("E003", "Aquacise",   9.00));
        store.addExerciseType(new ExerciseType("E004", "Box Fit",   11.00));
        store.addExerciseType(new ExerciseType("E005", "Body Blitz", 13.00));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // MEMBERS  (10 required)
    // ═══════════════════════════════════════════════════════════════════════

    private static void loadMembers(DataStore store) {
        store.addMember(new Member("M001", "Alice Carter",   "07700900001"));
        store.addMember(new Member("M002", "Bob Hughes",     "07700900002"));
        store.addMember(new Member("M003", "Carol James",    "07700900003"));
        store.addMember(new Member("M004", "David Lee",      "07700900004"));
        store.addMember(new Member("M005", "Emma White",     "07700900005"));
        store.addMember(new Member("M006", "Frank Brown",    "07700900006"));
        store.addMember(new Member("M007", "Grace Kim",      "07700900007"));
        store.addMember(new Member("M008", "Harry Evans",    "07700900008"));
        store.addMember(new Member("M009", "Isla Martin",    "07700900009"));
        store.addMember(new Member("M010", "Jack Wilson",    "07700900010"));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LESSONS  — 8 weekends × 2 days × 3 slots = 48 lessons
    // Pattern per day: MORNING=Yoga, AFTERNOON=Zumba, EVENING=BoxFit  (Sat)
    //                  MORNING=Aquacise, AFTERNOON=BodyBlitz, EVENING=Yoga (Sun)
    // ═══════════════════════════════════════════════════════════════════════

    private static void loadLessons(DataStore store) {
        DataStore ds = DataStore.getInstance();
        ExerciseType yoga      = ds.findExerciseTypeById("E001");
        ExerciseType zumba     = ds.findExerciseTypeById("E002");
        ExerciseType aquacise  = ds.findExerciseTypeById("E003");
        ExerciseType boxFit    = ds.findExerciseTypeById("E004");
        ExerciseType bodyBlitz = ds.findExerciseTypeById("E005");

        for (int week = 1; week <= 8; week++) {
            String w = String.format("%02d", week);

            // ── Saturday ──────────────────────────────────────────────────
            store.addLesson(new Lesson("L" + w + "SAT1", yoga,      Day.SATURDAY, TimeSlot.MORNING,   week));
            store.addLesson(new Lesson("L" + w + "SAT2", zumba,     Day.SATURDAY, TimeSlot.AFTERNOON, week));
            store.addLesson(new Lesson("L" + w + "SAT3", boxFit,    Day.SATURDAY, TimeSlot.EVENING,   week));

            // ── Sunday ────────────────────────────────────────────────────
            store.addLesson(new Lesson("L" + w + "SUN1", aquacise,  Day.SUNDAY,   TimeSlot.MORNING,   week));
            store.addLesson(new Lesson("L" + w + "SUN2", bodyBlitz, Day.SUNDAY,   TimeSlot.AFTERNOON, week));
            store.addLesson(new Lesson("L" + w + "SUN3", yoga,      Day.SUNDAY,   TimeSlot.EVENING,   week));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // BOOKINGS  — spread members across lessons
    // ═══════════════════════════════════════════════════════════════════════

    private static void loadBookings(DataStore store) {
        // Helper references
        Member alice   = store.findMemberById("M001");
        Member bob     = store.findMemberById("M002");
        Member carol   = store.findMemberById("M003");
        Member david   = store.findMemberById("M004");
        Member emma    = store.findMemberById("M005");
        Member frank   = store.findMemberById("M006");
        Member grace   = store.findMemberById("M007");
        Member harry   = store.findMemberById("M008");
        Member isla    = store.findMemberById("M009");
        Member jack    = store.findMemberById("M010");

        // Week 1
        book(store, "B001", alice,  "L01SAT1");
        book(store, "B002", bob,    "L01SAT1");
        book(store, "B003", carol,  "L01SAT1");
        book(store, "B004", david,  "L01SAT2");
        book(store, "B005", emma,   "L01SAT2");
        book(store, "B006", frank,  "L01SAT3");
        book(store, "B007", grace,  "L01SUN1");
        book(store, "B008", harry,  "L01SUN1");
        book(store, "B009", isla,   "L01SUN2");
        book(store, "B010", jack,   "L01SUN2");

        // Week 2
        book(store, "B011", alice,  "L02SAT1");
        book(store, "B012", bob,    "L02SAT2");
        book(store, "B013", carol,  "L02SUN1");
        book(store, "B014", david,  "L02SUN2");
        book(store, "B015", emma,   "L02SAT3");
        book(store, "B016", frank,  "L02SUN1");
        book(store, "B017", grace,  "L02SAT1");
        book(store, "B018", harry,  "L02SUN2");
        book(store, "B019", isla,   "L02SAT2");
        book(store, "B020", jack,   "L02SUN3");

        // Week 3
        book(store, "B021", alice,  "L03SAT1");
        book(store, "B022", bob,    "L03SUN1");
        book(store, "B023", carol,  "L03SAT2");
        book(store, "B024", david,  "L03SUN3");
        book(store, "B025", emma,   "L03SAT3");

        // Week 4
        book(store, "B026", frank,  "L04SAT1");
        book(store, "B027", grace,  "L04SUN2");
        book(store, "B028", harry,  "L04SAT2");
        book(store, "B029", isla,   "L04SUN1");
        book(store, "B030", jack,   "L04SAT3");

        // Week 5
        book(store, "B031", alice,  "L05SUN1");
        book(store, "B032", bob,    "L05SAT1");
        book(store, "B033", carol,  "L05SUN2");
        book(store, "B034", david,  "L05SAT2");
        book(store, "B035", emma,   "L05SUN3");

        // Week 6
        book(store, "B036", frank,  "L06SAT1");
        book(store, "B037", grace,  "L06SUN1");
        book(store, "B038", harry,  "L06SAT3");
        book(store, "B039", isla,   "L06SUN2");
        book(store, "B040", jack,   "L06SAT2");

        // Week 7
        book(store, "B041", alice,  "L07SAT2");
        book(store, "B042", bob,    "L07SUN2");
        book(store, "B043", carol,  "L07SAT3");
        book(store, "B044", david,  "L07SUN1");
        book(store, "B045", emma,   "L07SAT1");

        // Week 8
        book(store, "B046", frank,  "L08SUN1");
        book(store, "B047", grace,  "L08SAT1");
        book(store, "B048", harry,  "L08SUN3");
        book(store, "B049", isla,   "L08SAT2");
        book(store, "B050", jack,   "L08SUN2");
    }

    /** Convenience: adds member to lesson and creates the booking record */
    private static void book(DataStore store, String bookingId, Member member, String lessonId) {
        Lesson lesson = store.findLessonById(lessonId);
        if (lesson == null) throw new IllegalStateException("Lesson not found: " + lessonId);
        lesson.addMember(member);
        store.addBooking(new Booking(bookingId, member, lesson));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // REVIEWS  — 20+ reviews with ratings 1–5
    // ═══════════════════════════════════════════════════════════════════════

    private static void loadReviews(DataStore store) {
        review(store, "R001", "M001", "L01SAT1", 5, "Fantastic yoga session, very relaxing!");
        review(store, "R002", "M002", "L01SAT1", 4, "Great instructor, good pace.");
        review(store, "R003", "M003", "L01SAT1", 5, "Best yoga class I have attended.");
        review(store, "R004", "M004", "L01SAT2", 3, "Zumba was okay, a bit fast for me.");
        review(store, "R005", "M005", "L01SAT2", 4, "Really fun and energetic!");
        review(store, "R006", "M006", "L01SAT3", 5, "Box Fit was intense but brilliant.");
        review(store, "R007", "M007", "L01SUN1", 4, "Aquacise was refreshing and well structured.");
        review(store, "R008", "M008", "L01SUN1", 2, "A bit slow, expected more intensity.");
        review(store, "R009", "M009", "L01SUN2", 5, "Body Blitz pushed me to my limits!");
        review(store, "R010", "M010", "L01SUN2", 4, "Excellent session, will book again.");
        review(store, "R011", "M001", "L02SAT1", 5, "Second yoga session, even better.");
        review(store, "R012", "M002", "L02SAT2", 3, "Zumba was fine, nothing special.");
        review(store, "R013", "M003", "L02SUN1", 4, "Good aquacise class.");
        review(store, "R014", "M004", "L02SUN2", 5, "Body Blitz is my favourite class now.");
        review(store, "R015", "M005", "L02SAT3", 1, "Box Fit was too hard, I struggled.");
        review(store, "R016", "M006", "L02SUN1", 4, "Loved the water exercises.");
        review(store, "R017", "M007", "L02SAT1", 5, "Yoga helped with my back pain.");
        review(store, "R018", "M008", "L02SUN2", 3, "Average session today.");
        review(store, "R019", "M009", "L02SAT2", 4, "Zumba was very lively this week.");
        review(store, "R020", "M010", "L02SUN3", 5, "Evening yoga was the perfect end to the weekend.");
        review(store, "R021", "M001", "L03SAT1", 4, "Consistent quality every week.");
        review(store, "R022", "M002", "L03SUN1", 3, "Aquacise was decent.");
    }

    /** Convenience: creates and stores a review */
    private static void review(DataStore store, String reviewId,
            String memberId, String lessonId, int rating, String comment) {
        Member member = store.findMemberById(memberId);
        Lesson lesson = store.findLessonById(lessonId);
        if (member == null) throw new IllegalStateException("Member not found: " + memberId);
        if (lesson == null) throw new IllegalStateException("Lesson not found: " + lessonId);
        store.addReview(new Review(reviewId, member, lesson, rating, comment));
    }
}