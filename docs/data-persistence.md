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

## Data Persistence

> Module 7COM1025 - University of Hertfordshire  
> Furzefield Leisure Centre - Booking Management System  
> All content sourced from `JsonStore.java`, `AppData.java`, `DashboardScreen.java`, and `flc-data.json`.

---

## Contents

- [Data Persistence](#data-persistence)
- [Contents](#contents)
- [Overview](#overview)
- [Architecture](#architecture)
- [DTO design](#dto-design)
- [Save process](#save-process)
- [Load process](#load-process)
- [File format](#file-format)
- [Save triggers](#save-triggers)
- [First launch behaviour](#first-launch-behaviour)
- [Error handling](#error-handling)
- [Design decisions](#design-decisions)

---

## Overview

The application uses a simple flat-file persistence strategy. All data lives in
memory in a singleton `DataStore` instance during a session. On every mutating
operation, the entire in-memory state is serialised to a single JSON file called
`flc-data.json` using the Gson library.

| Property | Value |
|---|---|
| Persistence class | `com.flc.data.persistence.JsonStore` |
| DTO class | `com.flc.data.persistence.AppData` |
| File name | `flc-data.json` |
| File location | Working directory (the folder from which the application is launched) |
| Format | Pretty-printed JSON (Gson `setPrettyPrinting()`) |
| Library | Gson 2.10.1 |
| Logging | SLF4J via `JsonStore` - INFO on success, ERROR on failure |

---

## Architecture

The persistence layer sits below the data layer and has no knowledge of the
view or controller layers. The view layer calls `JsonStore.save()` directly
after each mutating operation.

```
View layer          (BookingScreen, MemberScreen, ReviewScreen)
      |
      | calls JsonStore.save() after every write operation
      v
Persistence layer   (JsonStore, AppData DTOs)
      |
      | reads from / writes to
      v
Data layer          (DataStore singleton)
      |
      | model objects serialised as flat ID references
      v
flc-data.json       (working directory)
```

---

## DTO design

`AppData` is a plain data wrapper containing five inner DTO classes. Each DTO
holds only primitive values and strings - no object references. This is the
key design decision that allows Gson to serialise and deserialise the data
without encountering circular reference problems.

| DTO class | Fields | Maps from/to |
|---|---|---|
| `MemberDto` | `memberId`, `name`, `phone` | `Member` |
| `ExerciseTypeDto` | `exerciseId`, `name`, `price` | `ExerciseType` |
| `LessonDto` | `lessonId`, `exerciseTypeId`, `day`, `timeSlot`, `weekNumber`, `memberIds[]` | `Lesson` |
| `BookingDto` | `bookingId`, `memberId`, `lessonId` | `Booking` |
| `ReviewDto` | `reviewId`, `memberId`, `lessonId`, `rating`, `comment` | `Review` |

Note that `LessonDto` stores `exerciseTypeId` (a foreign key string) rather than
an `ExerciseType` object, and `memberIds` (a list of ID strings) rather than a
list of `Member` objects. `BookingDto` and `ReviewDto` similarly store only ID
strings for their member and lesson references.

---

## Save process

`JsonStore.save()` converts the entire `DataStore` state to DTOs and writes
to disk in one atomic operation. The steps run in this exact order:

| Step | Operation |
|---|---|
| 1 | Get the `DataStore` singleton instance |
| 2 | Create a new `AppData` wrapper |
| 3 | Convert each `Member` to `MemberDto` |
| 4 | Convert each `ExerciseType` to `ExerciseTypeDto` |
| 5 | Convert each `Lesson` to `LessonDto` - store `exerciseTypeId` and the list of `memberIds` |
| 6 | Convert each `Booking` to `BookingDto` - store `memberId` and `lessonId` |
| 7 | Convert each `Review` to `ReviewDto` - store `memberId` and `lessonId` |
| 8 | Write the `AppData` object to `flc-data.json` via `Gson.toJson(data, writer)` |
| 9 | Log INFO on success or ERROR on `IOException` |

The entire file is rewritten on every save. There is no incremental or
differential update mechanism.

---

## Load process

`JsonStore.load()` reads `flc-data.json` and reconstructs all object
references by ID lookup. The reconstruction order matters: entities that are
referenced by others must be loaded first.

| Step | Operation | Detail |
|---|---|---|
| 1 | Check file exists | Returns `false` immediately if `flc-data.json` is not present |
| 2 | Parse JSON | `Gson.fromJson(reader, AppData.class)` deserialises all DTOs |
| 3 | Clear DataStore | `DataStore.clearAll()` empties all collections before repopulating |
| 4 | Reconstruct Members | `MemberDto` fields mapped directly to `Member` objects |
| 5 | Reconstruct ExerciseTypes | `ExerciseTypeDto` fields mapped directly to `ExerciseType` objects |
| 6 | Reconstruct Lessons | `exerciseTypeId` looked up in DataStore, each `memberId` looked up and re-enrolled |
| 7 | Reconstruct Bookings | `memberId` and `lessonId` looked up in DataStore - skipped silently if either not found |
| 8 | Reconstruct Reviews | `memberId` and `lessonId` looked up in DataStore - skipped silently if either not found |
| 9 | Log result | INFO message with counts of loaded entities |
| 10 | Return | `true` on success, `false` on any exception |

---

## File format

`flc-data.json` contains five top-level arrays. The file below shows the
structure with one example entry per array.

```json
{
  "members": [
    {
      "memberId": "M001",
      "name": "Alice Carter",
      "phone": "07700900001"
    }
  ],
  "exerciseTypes": [
    {
      "exerciseId": "E001",
      "name": "Yoga",
      "price": 12.0
    }
  ],
  "lessons": [
    {
      "lessonId": "L01SAT1",
      "exerciseTypeId": "E001",
      "day": "SATURDAY",
      "timeSlot": "MORNING",
      "weekNumber": 1,
      "memberIds": ["M001", "M002", "M003"]
    }
  ],
  "bookings": [
    {
      "bookingId": "B001",
      "memberId": "M001",
      "lessonId": "L01SAT1"
    }
  ],
  "reviews": [
    {
      "reviewId": "R001",
      "memberId": "M001",
      "lessonId": "L01SAT1",
      "rating": 5,
      "comment": "Fantastic yoga session, very relaxing!"
    }
  ]
}
```

Key formatting details:

- `day` stored as enum name string: `"SATURDAY"` or `"SUNDAY"`
- `timeSlot` stored as enum name string: `"MORNING"`, `"AFTERNOON"`, or `"EVENING"`
- `memberIds` in a lesson is an array of ID strings, not nested objects
- `price` stored as a JSON number (double)
- `rating` stored as a JSON number (int)
- File is pretty-printed with 2-space indentation

---

## Save triggers

`JsonStore.save()` is called from the view layer immediately after every
operation that modifies data. The user never triggers a save manually.

| User action | Screen | Method that calls save |
|---|---|---|
| Create booking | `BookingScreen` | `BookingScreen.onBook()` |
| Change booking | `BookingScreen` | `BookingScreen.onChange()` |
| Cancel booking | `BookingScreen` | `BookingScreen.onCancel()` |
| Save member edits | `MemberScreen` | `MemberScreen.onSave()` |
| Add new member | `MemberScreen` | `MemberScreen.onAddMember()` |
| Submit review | `ReviewScreen` | `ReviewScreen.onSubmit()` |

Read-only operations (browsing the timetable, viewing reports, viewing reviews)
do not trigger a save.

---

## First launch behaviour

`DashboardScreen` controls the startup sequence in its constructor:

```java
if (!JsonStore.load()) {
    SampleData.load();
    JsonStore.save();
}
```

| Scenario | Behaviour |
|---|---|
| `flc-data.json` exists and is valid | `JsonStore.load()` returns `true` - existing data restored |
| `flc-data.json` does not exist | `JsonStore.load()` returns `false` - sample data loaded and immediately saved |
| `flc-data.json` exists but is malformed | `JsonStore.load()` catches the exception and returns `false` - sample data loaded as fallback |

To reset the application to its original state, delete `flc-data.json` and
relaunch the application.

---

## Error handling

| Scenario | Behaviour | Log output |
|---|---|---|
| Save succeeds | File written, execution continues | `INFO: Saved data to flc-data.json` |
| Save fails (`IOException`) | Exception caught, execution continues without crashing | `ERROR: Failed to save data to flc-data.json: ...` |
| Load succeeds | DataStore repopulated, returns `true` | `INFO` message with entity counts |
| Load file not found | Returns `false` silently | `INFO: No save file found at flc-data.json, will use sample data` |
| Load fails (parse error or other exception) | Exception caught, returns `false`, sample data loaded as fallback | `ERROR: Failed to load data from flc-data.json: ...` |
| Booking or review references missing entity on load | That record is skipped silently | No log entry - silent skip |

---

## Design decisions

**Why a single flat JSON file rather than a database?**

The coursework specification requires a self-contained desktop application with
no external server or internet connection. A flat JSON file satisfies this
requirement - it requires no installation, no configuration, and produces a
human-readable output that is easy to inspect and reset.

**Why the DTO pattern rather than serialising model objects directly?**

The model object graph contains circular-adjacent references. `Lesson` holds a
`List<Member>`, and both `Booking` and `Review` hold references to `Member` and
`Lesson` objects. Gson cannot serialise object graphs with shared references
reliably - it would either loop forever or produce duplicate data.

The DTO pattern breaks this by replacing all object references with plain ID
strings. `LessonDto` stores `exerciseTypeId` and `memberIds[]` instead of the
objects themselves. On load, the ID strings are used to look up the already
reconstructed objects from `DataStore`, rebuilding the full object graph cleanly.

**Why rewrite the entire file on every save rather than updating incrementally?**

The data set is small (48 lessons, up to a few hundred bookings and reviews at
most). Rewriting the full file on every save is simpler, safer, and fast enough
at this scale. It also eliminates the risk of partial writes leaving the file in
an inconsistent state.

**Why pretty-print the JSON?**

`GsonBuilder.setPrettyPrinting()` makes `flc-data.json` human-readable.
This is useful for debugging and for inspecting the state of the data without
running the application.

---

[Back to project root](../README.md)