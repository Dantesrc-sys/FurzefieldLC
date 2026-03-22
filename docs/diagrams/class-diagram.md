# Class diagram

**What this shows:** Every model class in the system — their fields, methods, visibility modifiers, and the relationships between them. This is the structural backbone of the application.

---

![Class diagram](class-diagram.svg)

---

## Classes

| Class | Responsibility |
|---|---|
| `Member` | A registered member of the leisure centre. Holds ID, name, and phone number. |
| `ExerciseType` | A type of group exercise (Yoga, Zumba, etc.) with a fixed price per session. |
| `Lesson` | A single scheduled session — one exercise type, one day, one time slot, one week. Enrolls up to 4 members. |
| `Booking` | Links one member to one lesson. The lesson reference is mutable to allow booking changes. |
| `Review` | A star-rated review (1 to 5) written by an enrolled member for a lesson they attended. |
| `Day` | Enumeration with values SATURDAY and SUNDAY. |
| `TimeSlot` | Enumeration with values MORNING, AFTERNOON, and EVENING. |

## Relationships

| Relationship | Type | Multiplicity | Reason |
|---|---|---|---|
| `Lesson` to `ExerciseType` | Composition | 1 to 1 | A lesson cannot exist without an exercise type |
| `Lesson` to `Member` | Aggregation | 1 to 0..4 | Members exist independently in `DataStore` |
| `Booking` to `Member` | Association | Many to 1 | Booking references an independently-managed member |
| `Booking` to `Lesson` | Association | Many to 1 | Booking references an independently-managed lesson |
| `Review` to `Member` | Association | Many to 1 | Review references the member who wrote it |
| `Review` to `Lesson` | Association | Many to 1 | Review references the lesson being reviewed |
| `Lesson` uses `Day` | Dependency | n/a | Lesson uses the enum but does not own it |
| `Lesson` uses `TimeSlot` | Dependency | n/a | Lesson uses the enum but does not own it |

## Business rules captured

- `MAX_LESSON_CAPACITY = 4` — enforced in `Lesson.addMember()`
- `RATING_MIN = 1`, `RATING_MAX = 5` — enforced in `Review` constructor and setter
- `weekNumber` range: 1 to 8 across the 8-weekend season
- `Booking.changeLesson()` validates that the new lesson is not full, has no time conflict, and is not the same as the current lesson

---

[Back to diagram index](README.md)
