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

## Business Rules

> Module 7COM1025 - University of Hertfordshire  
> Furzefield Leisure Centre - Booking Management System  
> All rules sourced directly from `AppConfig.java`, controller classes, model classes, and `DataStore.java`.

---

## Contents

- [Business Rules](#business-rules)
- [Contents](#contents)
- [System constants](#system-constants)
- [Timetable structure](#timetable-structure)
- [Booking rules](#booking-rules)
  - [Creating a booking](#creating-a-booking)
  - [Changing a booking](#changing-a-booking)
  - [Cancelling a booking](#cancelling-a-booking)
- [Member rules](#member-rules)
  - [Adding a member](#adding-a-member)
  - [Updating a member](#updating-a-member)
- [Review rules](#review-rules)
- [Lesson capacity rules](#lesson-capacity-rules)
  - [Adding a member to a lesson](#adding-a-member-to-a-lesson)
  - [Removing a member from a lesson](#removing-a-member-from-a-lesson)
- [Data integrity rules](#data-integrity-rules)
- [Data quality rules](#data-quality-rules)
- [Persistence rules](#persistence-rules)

---

## System constants

Defined in `com.flc.config.AppConfig`. These values are the single source of
truth for all capacity and rating constraints throughout the application.

| Constant | Value | Enforced in |
|---|---|---|
| `MAX_LESSON_CAPACITY` | 4 | `Lesson.addMember()`, `Lesson.isFull()`, `BookingController` |
| `RATING_MIN` | 1 | `Review` constructor, `Review.setRating()`, `ReviewController.addReview()` |
| `RATING_MAX` | 5 | `Review` constructor, `Review.setRating()`, `ReviewController.addReview()` |
| `LESSONS_PER_DAY` | 3 | Defines the timetable structure: Morning, Afternoon, Evening |

---

## Timetable structure

Rules governing the shape of the season. Defined by `SampleData.java` and `AppConfig`.

| Rule | Detail | Source |
|---|---|---|
| Season length | 8 weekends per season | `SampleData.loadLessons()` iterates weeks 1 to 8 |
| Days offered | Saturday and Sunday only | `Day` enum has exactly two values |
| Slots per day | 3 per day: Morning (09:00), Afternoon (13:00), Evening (18:00) | `AppConfig.LESSONS_PER_DAY = 3`, `TimeSlot` enum |
| Total lessons | 48 per season (8 weeks x 2 days x 3 slots) | Calculated from the above |

**Fixed timetable pattern across all 8 weekends:**

| Day | Morning | Afternoon | Evening |
|---|---|---|---|
| Saturday | Yoga (£12.00) | Zumba (£10.00) | Box Fit (£11.00) |
| Sunday | Aquacise (£9.00) | Body Blitz (£13.00) | Yoga (£12.00) |

---

## Booking rules

Enforced in `com.flc.controller.BookingController`.

### Creating a booking

All three validation checks run in this exact order. The first failure stops
processing and throws an exception caught by `BookingScreen.onBook()`.

| Order | Rule | Exception thrown | Source method |
|---|---|---|---|
| 1 | Member and lesson must not be null | `IllegalArgumentException` | `ValidationUtil.requireNonNull()` |
| 2 | Lesson must not be full (enrolled count less than `MAX_LESSON_CAPACITY`) | `IllegalStateException: "Lesson is full: " + lessonId` | `Lesson.isFull()` |
| 3 | Member must not already be enrolled in this lesson | `IllegalStateException: "Member already booked in this lesson"` | `Lesson.hasMember()` |
| 4 | Member must not have an existing booking on the same week, day, and time slot | `IllegalStateException: "Time conflict: member already has a booking on..."` | `BookingController.hasTimeConflict()` |

### Changing a booking

The time conflict check during a change is performed after temporarily removing
the member from the old lesson, so the old slot does not count as a conflict.
If the check fails, the member is restored to the original lesson before the
exception is thrown.

| Rule | Exception thrown | Source method |
|---|---|---|
| Booking and new lesson must not be null | `IllegalArgumentException` | `ValidationUtil.requireNonNull()` |
| New lesson must not be the same as the current lesson | `IllegalStateException: "New lesson is the same as the current lesson"` | `BookingController.changeBooking()` |
| New lesson must not be full | `IllegalStateException: "New lesson is full: " + lessonId` | `Lesson.isFull()` |
| Member must not already be enrolled in the new lesson | `IllegalStateException: "Member is already booked in the new lesson"` | `Lesson.hasMember()` |
| No time conflict with the new lesson (checked after releasing old slot) | `IllegalStateException: "Time conflict..."` | `BookingController.hasTimeConflict()` |
| If time conflict detected: member is restored to old lesson | No exception from restore - exception from conflict check | `BookingController.changeBooking()` |

### Cancelling a booking

| Rule | Detail | Source method |
|---|---|---|
| Booking must not be null | `IllegalArgumentException` if null | `ValidationUtil.requireNonNull()` |
| Member is removed from lesson | Lesson enrolment list updated immediately | `Lesson.removeMember()` |
| Booking is removed from DataStore | Record deleted from in-memory store | `DataStore.removeBooking()` |

---

## Member rules

Enforced in `com.flc.controller.MemberController` and `com.flc.validation.ValidationUtil`.

### Adding a member

| Rule | Detail | Exception thrown | Source |
|---|---|---|---|
| Name format | Letters, spaces, hyphens, and apostrophes only | `IllegalArgumentException` | `ValidationUtil.validateName()` |
| Name length | Minimum 2 characters, maximum 50 characters | `IllegalArgumentException` | `ValidationUtil.validateName()` |
| Phone format | 10 to 15 digits, optional leading `+`, spaces removed before validation | `IllegalArgumentException` | `ValidationUtil.validatePhone()` |
| Name uniqueness | Name must not already exist (case-insensitive comparison) | `IllegalStateException: "A member with the name '...' already exists"` | `MemberController.addMember()` |

### Updating a member

| Rule | Detail | Exception thrown | Source |
|---|---|---|---|
| Name uniqueness on update | Updated name must not belong to a different member | `IllegalStateException: "Name '...' is already taken"` | `MemberController.updateName()` |
| Phone cannot be blank | Empty or whitespace-only phone rejected | `IllegalArgumentException` | `MemberController.updatePhone()` |

---

## Review rules

Enforced in `com.flc.controller.ReviewController` and the `Review` model class.

| Rule | Detail | Exception thrown | Source |
|---|---|---|---|
| Member and lesson must not be null | Both required | `IllegalArgumentException` | `ValidationUtil.requireNonNull()` |
| Rating range | Must be between `RATING_MIN` (1) and `RATING_MAX` (5) inclusive | `IllegalArgumentException: "Rating must be between 1 and 5"` | `ReviewController.addReview()`, `Review.validateRating()` |
| Enrolment required | Member must be enrolled in the lesson they are reviewing | `IllegalStateException: "Member has not attended this lesson and cannot review it"` | `ReviewController.addReview()` |
| One review per member per lesson | A member cannot submit a second review for the same lesson | `IllegalStateException: "Member has already submitted a review for this lesson"` | `ReviewController.hasReviewed()` |

---

## Lesson capacity rules

Enforced directly in `com.flc.model.Lesson`.

### Adding a member to a lesson

| Rule | Detail | Exception thrown |
|---|---|---|
| Member must not be null | Null check before any operation | `IllegalArgumentException: "Member cannot be null"` |
| Lesson must not be full | Checked before adding | `IllegalStateException: "Lesson is full"` |
| Member must not already be enrolled | Duplicate check before adding | `IllegalArgumentException: "Member already enrolled in this lesson"` |

### Removing a member from a lesson

| Rule | Detail | Exception thrown |
|---|---|---|
| Member must not be null | Null check before any operation | `IllegalArgumentException: "Member cannot be null"` |
| Member must be enrolled | Cannot remove a member who is not in the lesson | `IllegalArgumentException: "Member not enrolled in this lesson"` |

---

## Data integrity rules

Enforced in `com.flc.data.DataStore`. Every entity ID must be unique within
its collection. Violations are detected on add operations.

| Entity | ID format | Duplicate behaviour |
|---|---|---|
| `Member` | `M` + 3-digit integer (e.g. `M001`) | `IllegalArgumentException: "Member ID already exists: ..."` |
| `ExerciseType` | `E` + 3-digit integer (e.g. `E001`) | `IllegalArgumentException: "Exercise ID already exists: ..."` |
| `Lesson` | `L` + week + day + slot (e.g. `L01SAT1`) | `IllegalArgumentException: "Lesson ID already exists: ..."` |
| `Booking` | `B` + 3-digit integer (e.g. `B001`) | `IllegalArgumentException: "Booking ID already exists: ..."` |
| `Review` | `R` + 3-digit integer (e.g. `R001`) | `IllegalArgumentException: "Review ID already exists: ..."` |

IDs are generated sequentially by each controller using a counter initialised
from the current total count in DataStore at startup. They are never reused
within a session.

---

## Data quality rules

Enforced in `com.flc.model.Review`.

| Rule | Detail | Source |
|---|---|---|
| Null comment stored as empty string | `null` comment passed to constructor is converted to `""` | `Review` constructor |
| Comment whitespace trimmed | Leading and trailing whitespace removed before storage | `Review` constructor and `Review.setComment()` |

---

## Persistence rules

Enforced in `com.flc.data.persistence.JsonStore` and `com.flc.view.DashboardScreen`.

| Rule | Detail | Triggered by |
|---|---|---|
| Automatic save on every write | `JsonStore.save()` is called after every mutating operation | Create booking, change booking, cancel booking, add member, edit member, submit review |
| Sample data on first launch | If `flc-data.json` does not exist, `SampleData.load()` runs and the result is immediately saved | `DashboardScreen` constructor |
| File location | `flc-data.json` is written to the working directory (the folder from which the application is launched) | `JsonStore` constant `FILE_NAME = "flc-data.json"` |
| Reset behaviour | Deleting `flc-data.json` causes the application to reload sample data on next launch | `JsonStore.load()` returns false when file is absent |

---

[Back to project root](../README.md)