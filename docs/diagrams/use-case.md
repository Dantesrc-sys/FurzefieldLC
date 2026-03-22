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

## Use case diagram

**What this shows:** The system from the outside - one actor (the staff member) and every action they can perform across all six screens. Includes include relationships showing sub-cases and automatic system behaviours.

---

![Use case diagram](use-case-diagram.svg)

---

## Actor

| Actor | Description |
|---|---|
| Staff member | The single user role. Operates the desktop application directly at the leisure centre. No login or authentication - single-user local application. |

## Use cases by screen

| Screen | Use cases |
|---|---|
| Timetable | Browse timetable, filter by day, filter by exercise type, filter by week |
| Bookings | Create booking, change booking, cancel booking |
| Members | View all members, add new member, edit member details, search members |
| Reviews | Submit review (1 to 5 stars), view all reviews |
| Reports | View attendance and rating report, view income by exercise report |
| System (automatic) | Save data to file - triggered automatically after every mutating action |

## Key constraints

**Create booking** validates three rules before confirming:

- Lesson must not be full (maximum 4 members per lesson)
- Member must not already have a booking at the same week, day, and time slot
- Member must not already be enrolled in the same lesson

**Submit review** requires:

- The member must be enrolled in the lesson they are reviewing
- One review per member per lesson - duplicate submissions are rejected

**Save data to file** is an include relationship on every write operation - create booking, change booking, cancel booking, add member, edit member, and submit review. The user never triggers a save manually; it happens automatically.

---

[Back to diagram index](README.md)
