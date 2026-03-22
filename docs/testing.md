<div align="center">
  <img src="../src/main/resources/assets/logo.png" alt="Furzefield LC Logo" width="72" height="72"/>
  <h1>Furzefield Leisure Centre</h1>
  <p><em>Group exercise booking management system</em></p>

  ![Java](https://img.shields.io/badge/Java-25.0.2-orange?style=flat-square)
  ![Maven](https://img.shields.io/badge/Maven-3.9.12-red?style=flat-square)
  ![Gson](https://img.shields.io/badge/Gson-2.10.1-blue?style=flat-square)
  ![Module](https://img.shields.io/badge/7COM1025-University%20of%20Hertfordshire-green?style=flat-square)
</div>

---

## Testing

> Module 7COM1025 - University of Hertfordshire  
> Furzefield Leisure Centre - Booking Management System  
> Framework: JUnit 5 (junit-jupiter 5.10.0)  
> Run tests: `mvn test`

---

## Summary

| Metric | Value |
|---|---|
| Test classes | 15 |
| Total test methods | 167 |
| Test framework | JUnit 5 (junit-jupiter 5.10.0) |
| Scope | Unit tests only - no integration or UI tests |
| Isolation | Each test class calls `DataStore.clearAll()` in `@BeforeEach` to guarantee a clean state |

---

## Test class overview

| Class | Package | Tests | What it covers |
|---|---|---|---|
| `AppTest` | `com.flc` | 1 | Baseline smoke test |
| `BookingControllerTest` | `com.flc.controller` | 15 | Create, change, cancel booking and all validations |
| `MemberControllerTest` | `com.flc.controller` | 15 | Add, find, and update members |
| `ReportControllerTest` | `com.flc.controller` | 11 | Attendance and income report generation |
| `ReviewControllerTest` | `com.flc.controller` | 13 | Submit reviews, average rating, and duplicate detection |
| `DataStoreTest` | `com.flc.data` | 18 | In-memory storage for all five entity types |
| `SampleDataTest` | `com.flc.data` | 8 | Sample data loader integrity |
| `JsonStoreTest` | `com.flc.data.persistence` | 3 | Save, load, and file existence detection |
| `BookingTest` | `com.flc.model` | 9 | Booking construction, lesson change, equality |
| `ExerciseTypeTest` | `com.flc.model` | 11 | ExerciseType construction, setters, equality |
| `LessonTest` | `com.flc.model` | 14 | Lesson capacity, enrolment, income calculation |
| `MemberTest` | `com.flc.model` | 8 | Member construction, setters, equality |
| `ReviewTest` | `com.flc.model` | 15 | Review construction, rating validation, labels |
| `ImageUtilTest` | `com.flc.util` | 8 | Image loading, tinting, and null handling |
| `ModernTableTest` | `com.flc.util` | 10 | Table factory, column configuration, colour maps |

---

## Controller tests

### BookingControllerTest - 15 tests

Tests the full booking lifecycle through `BookingController`. Each test uses a fresh
`DataStore` state with two members, two exercise types, and three lessons.

**Create booking - 6 tests**

| Test | What it verifies |
|---|---|
| `shouldCreateBookingSuccessfully` | Happy path - booking is created, member is enrolled in lesson, booking is stored in DataStore |
| `shouldThrowWhenLessonIsFull` | `IllegalStateException` thrown when lesson already has 4 members |
| `shouldThrowWhenMemberAlreadyBooked` | `IllegalStateException` thrown when member is already enrolled in the same lesson |
| `shouldThrowOnTimeConflict` | `IllegalStateException` thrown when member has an existing booking on the same week, day, and time slot |
| `shouldAllowBookingDifferentDaysSameWeek` | No conflict when same week but different day (Saturday vs Sunday) |
| `shouldAllowBookingDifferentSlotSameDay` | No conflict when same day but different time slot (Morning vs Afternoon) |

**Change booking - 4 tests**

| Test | What it verifies |
|---|---|
| `shouldChangeBookingSuccessfully` | Booking moves to new lesson, member removed from old lesson and added to new lesson |
| `shouldThrowWhenChangingToSameLesson` | `IllegalStateException` thrown when new lesson is identical to current lesson |
| `shouldThrowWhenChangingToFullLesson` | `IllegalStateException` thrown when target lesson is at capacity |
| `shouldRestoreMemberOnConflictDuringChange` | If a time conflict is detected during change, the member is restored to the original lesson |

**Cancel booking - 1 test**

| Test | What it verifies |
|---|---|
| `shouldCancelBookingSuccessfully` | Booking removed from DataStore, member removed from lesson enrolment list |

**Queries - 4 tests**

| Test | What it verifies |
|---|---|
| `shouldReturnAvailableLessons` | Returns only lessons with available spaces |
| `shouldReturnLessonsByDay` | Filters lessons correctly by day |
| `shouldReturnLessonsByExerciseName` | Filters lessons correctly by exercise type name |
| `shouldDetectTimeConflict` | `hasTimeConflict()` correctly identifies conflicting and non-conflicting slots |

---

### MemberControllerTest - 15 tests

Tests member management through `MemberController`.

**Add member - 6 tests**

| Test | What it verifies |
|---|---|
| `shouldAddMemberSuccessfully` | Member created with correct name and phone |
| `shouldGenerateUniqueMemberIds` | Consecutive adds produce different IDs |
| `shouldThrowWhenNameIsBlank` | `IllegalArgumentException` on blank name |
| `shouldThrowWhenPhoneIsBlank` | `IllegalArgumentException` on blank phone |
| `shouldThrowWhenDuplicateName` | `IllegalStateException` when name already exists |
| `shouldTrimWhitespaceFromNameAndPhone` | Leading and trailing whitespace removed before storage |

**Find member - 4 tests**

| Test | What it verifies |
|---|---|
| `shouldFindMemberById` | Returns correct member by exact ID |
| `shouldReturnNullWhenIdNotFound` | Returns null for unknown ID |
| `shouldFindMemberByName` | Case-insensitive name lookup |
| `shouldReturnNullWhenNameNotFound` | Returns null for unknown name |

**Get all - 1 test**

| Test | What it verifies |
|---|---|
| `shouldReturnAllMembers` | Returns complete list after multiple adds |

**Update - 4 tests**

| Test | What it verifies |
|---|---|
| `shouldUpdatePhone` | Phone number updated correctly |
| `shouldThrowWhenUpdatingPhoneToBlank` | `IllegalArgumentException` on blank phone update |
| `shouldUpdateName` | Name updated correctly |
| `shouldThrowWhenUpdatingNameToDuplicate` | `IllegalStateException` when updated name already belongs to another member |

---

### ReportControllerTest - 11 tests

Tests both report generators using the full sample data set loaded via `SampleData.load()`.

**Attendance and rating report - 5 tests**

| Test | What it verifies |
|---|---|
| `shouldReturn48RowsInAttendanceReport` | One row per lesson across the full 8-weekend season |
| `shouldSortAttendanceReportByWeekThenDayThenSlot` | First row is Week 1, Saturday, Morning |
| `shouldShowCorrectEnrolledCountInAttendanceReport` | L01SAT1 shows 3 enrolled members |
| `shouldShowAverageRatingInAttendanceReport` | L01SAT1 averages 4.67 from three reviews (5, 4, 5) |
| `shouldShowNoReviewsWhenLessonHasNone` | Week 8 lessons return "No reviews" label |

**Income report - 6 tests**

| Test | What it verifies |
|---|---|
| `shouldReturn5RowsInIncomeReport` | One row per exercise type |
| `shouldSortIncomeReportHighestFirst` | Exercise types ordered by total income descending |
| `shouldIdentifyHighestIncomeExercise` | Top earner matches first row in sorted report |
| `shouldFormatIncomeCorrectly` | Formatted income string begins with pound sign |
| `shouldReturnNullHighestIncomeWhenNoLessons` | Returns null when DataStore is empty |
| `shouldCalculateTotalIncomeCorrectly` | Manual data: 2 Yoga members at £10 = £20, 1 Zumba member at £8 = £8 |

---

### ReviewControllerTest - 13 tests

Tests review submission and querying through `ReviewController`.

**Add review - 6 tests**

| Test | What it verifies |
|---|---|
| `shouldAddReviewSuccessfully` | Review created with correct member, lesson, rating, and comment |
| `shouldThrowWhenMemberNotEnrolled` | `IllegalStateException` when reviewer is not enrolled in the lesson |
| `shouldThrowWhenMemberAlreadyReviewed` | `IllegalStateException` on duplicate review submission |
| `shouldThrowWhenRatingOutOfRange` | `IllegalArgumentException` for ratings of 0 and 6 |
| `shouldThrowWhenMemberIsNull` | `IllegalArgumentException` on null member |
| `shouldThrowWhenLessonIsNull` | `IllegalArgumentException` on null lesson |

**Average rating - 3 tests**

| Test | What it verifies |
|---|---|
| `shouldReturnZeroAverageWhenNoReviews` | Returns 0.0 when no reviews exist for a lesson |
| `shouldCalculateAverageRating` | Two reviews (4 and 2) produce an average of 3.0 |
| `shouldCalculateAverageForSingleReview` | Single review of 5 returns exactly 5.0 |

**Queries - 3 tests**

| Test | What it verifies |
|---|---|
| `shouldReturnReviewsForLesson` | Returns all reviews for a specific lesson |
| `shouldReturnReviewsForMember` | Returns all reviews by a specific member |
| `shouldReturnEmptyListWhenNoReviews` | Returns empty list when no reviews exist |

**hasReviewed - 1 test**

| Test | What it verifies |
|---|---|
| `shouldDetectAlreadyReviewed` | Returns false before submission, true after |

---

## Data layer tests

### DataStoreTest - 18 tests

Tests the singleton `DataStore` directly, covering all five entity collections.

| Group | Tests | What is covered |
|---|---|---|
| Members | 4 | Add, find by ID, find by name (case-insensitive), null on unknown ID |
| Exercise types | 3 | Add, find by name (case-insensitive), duplicate ID rejection |
| Lessons | 4 | Add, find by day, find by exercise name, find by week number |
| Bookings | 3 | Add, find by member, remove |
| Reviews | 2 | Add, find by lesson |
| Utility | 2 | `clearAll()` empties all collections, `getTotalX()` counts are correct |

---

### SampleDataTest - 8 tests

Verifies the integrity of the sample data loaded by `SampleData.load()`.

| Test | Expected value | Source |
|---|---|---|
| `shouldLoadFiveExerciseTypes` | 5 | `SampleData.loadExerciseTypes()` |
| `shouldLoadTenMembers` | 10 | `SampleData.loadMembers()` |
| `shouldLoadFortyEightLessons` | 48 | 8 weeks x 2 days x 3 slots |
| `shouldLoadFiftyBookings` | 50 | `SampleData.loadBookings()` |
| `shouldLoadTwentyTwoReviews` | 22 | `SampleData.loadReviews()` |
| `shouldHaveSixLessonsPerWeek` | 6 per week | 2 days x 3 slots |
| `shouldFindYogaLessons` | 16 | 8 weeks x 2 Yoga slots (Saturday Morning and Sunday Evening) |
| `shouldFindSaturdayLessons` | 24 | 8 weeks x 3 Saturday slots |

---

### JsonStoreTest - 3 tests

Tests the JSON persistence layer using the actual file system. Each test deletes
`flc-data.json` before running and removes it afterwards.

| Test | What it verifies |
|---|---|
| `testSaveFileExists` | `saveFileExists()` returns false before save and true after file is created |
| `testLoadWhenNoFile` | `load()` returns false when no save file is present |
| `testSaveAndLoad` | Full round trip - load sample data, save to file, clear DataStore, reload, verify counts match |

---

## Model tests

### LessonTest - 14 tests

| Group | Tests | Key behaviours verified |
|---|---|---|
| Construction | 4 | Valid creation, starts empty, null exercise type rejected, week number zero rejected |
| Enrolment | 4 | Add member, remove member, duplicate member rejected, non-enrolled removal rejected |
| Capacity | 2 | Becomes full at exactly 4 members, fifth member rejected with `IllegalStateException` |
| Income | 2 | Zero income when empty, correct multiplication of count by price |
| Price | 1 | `getPrice()` delegates correctly to `exerciseType.getPrice()` |
| Immutability | 1 | `getMembers()` returns an unmodifiable list |

---

### ReviewTest - 15 tests

| Group | Tests | Key behaviours verified |
|---|---|---|
| Construction | 6 | Valid creation, null comment stored as empty string, comment trimmed, blank ID rejected, null member rejected, null lesson rejected |
| Rating validation | 3 | All values 1 to 5 accepted, 0 rejected, 6 rejected |
| Labels | 1 | All five rating labels return correct text |
| Setters | 3 | Rating updated, invalid rating rejected, comment updated |
| Equality | 2 | Equal when same `reviewId`, not equal when different `reviewId` |

---

### BookingTest - 9 tests

| Group | Tests | Key behaviours verified |
|---|---|---|
| Construction | 4 | Valid creation, blank ID rejected, null member rejected, null lesson rejected |
| Change lesson | 3 | Successful change, null lesson rejected, full lesson rejected |
| Equality | 2 | Equal when same `bookingId`, not equal when different `bookingId` |

---

### MemberTest - 8 tests

| Group | Tests | Key behaviours verified |
|---|---|---|
| Construction | 4 | Valid creation, blank ID rejected, blank name rejected, blank phone rejected |
| Setters | 2 | Name updated, blank name rejected |
| Equality | 2 | Equal when same `memberId`, not equal when different `memberId` |

---

### ExerciseTypeTest - 11 tests

| Group | Tests | Key behaviours verified |
|---|---|---|
| Construction | 5 | Valid creation, blank ID rejected, blank name rejected, negative price rejected, zero price accepted |
| Setters | 3 | Name updated, price updated, negative price setter rejected |
| toString | 1 | Format is `Name (£price)` e.g. `Yoga (£12.50)` |
| Equality | 2 | Equal when same `exerciseId`, not equal when different `exerciseId` |

---

## Utility tests

### ImageUtilTest - 8 tests

| Test | What it verifies |
|---|---|
| `testLoadExistingAsset` | `load()` returns a correctly sized icon for `assets/logo.png` |
| `testLoadNonExistingAsset` | `load()` returns null gracefully for a missing path |
| `testTint` | Tinted icon has correct dimensions |
| `testTintNullSource` | `tint()` returns null when source is null |
| `testLoadTinted` | Combined load and tint succeeds for an existing asset |
| `testLoadTintedNonExisting` | Returns null gracefully for a missing path |
| `testIconLabel` | Returns a `JLabel` with icon set for an existing asset |
| `testTintedLabel` | Returns a `JLabel` with tinted icon for an existing asset |

---

### ModernTableTest - 10 tests

| Test | What it verifies |
|---|---|
| `testExerciseColoursNotNull` | Exercise colour map is populated and contains key `Yoga` |
| `testDayColoursNotNull` | Day colour map is populated and contains key `Saturday` |
| `testTimeColoursNotNull` | Time colour map is populated and contains key `Morning` |
| `testCreateTable` | `create()` returns a non-null `JTable` with the correct model |
| `testSetColumnWidths` | Column preferred widths set correctly |
| `testHideColumn` | Hidden column has zero width |
| `testSetBoldColumn` | Bold renderer assigned without error |
| `testSetRightAligned` | Right-aligned renderer assigned without error |
| `testSetCentreAligned` | Centre-aligned renderer assigned without error |
| `testSetPriceColumn` | Price renderer assigned without error |

---

## How to run tests

```powershell
mvn test
```

To run a specific test class:

```powershell
mvn test -Dtest=BookingControllerTest
```

To run a specific test method:

```powershell
mvn test -Dtest=BookingControllerTest#shouldCreateBookingSuccessfully
```

Test reports are written to `target/surefire-reports/` after each run.

---

[Back to project root](../README.md)