<div align="center">
  <img src="../../src/main/resources/assets/logo.png" alt="Furzefield LC Logo" width="72" height="72"/>
  <h1>Furzefield Leisure Centre</h1>
  <p><em>Group exercise booking management system</em></p>

  ![Java](https://img.shields.io/badge/Java-25.0.2-orange?style=flat-square)
  ![Maven](https://img.shields.io/badge/Maven-3.9.12-red?style=flat-square)
  ![Gson](https://img.shields.io/badge/Gson-2.10.1-blue?style=flat-square)
  ![Module](https://img.shields.io/badge/7COM1025-University%20of%20Hertfordshire-green?style=flat-square)
</div>

---

## Sequence diagram

**What this shows:** The create-booking flow traced end-to-end through all layers - from the user clicking Book in `BookingScreen`, through `BookingController` validation, into `DataStore`, and finally persisted by `JsonStore`.

---

![Sequence diagram](sequence-diagram.svg)

---

## Participants

| Participant | Layer | Role in this flow |
|---|---|---|
| `BookingScreen` | View | Captures member and lesson selection, calls controller |
| `BookingController` | Controller | Enforces all booking business rules |
| `Lesson` | Model | Checks capacity, checks membership, adds member |
| `DataStore` | Data | Stores the new `Booking` object in memory |
| `JsonStore` | Persistence | Serialises entire state to `flc-data.json` |

## Flow summary

1. Staff selects a member and a lesson then clicks Book
2. `BookingScreen` calls `BookingController.createBooking(member, lesson)`
3. Controller asks `Lesson` whether it is full - throws `IllegalStateException` if yes
4. Controller asks `Lesson` whether the member is already enrolled - throws if yes
5. Controller queries `DataStore` for existing bookings to detect time conflicts - throws if a conflict is found
6. Controller calls `Lesson.addMember()` to enrol the member
7. Controller creates a `Booking` object and passes it to `DataStore.addBooking()`
8. Controller calls `JsonStore.save()` - entire state is written to disk
9. `BookingScreen` refreshes both the lesson table and the bookings table

---

[Back to diagram index](README.md)
