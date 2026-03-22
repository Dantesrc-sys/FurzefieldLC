<div align="center">
  <img src="src/main/resources/assets/logo.png" alt="Furzefield LC Logo" width="72" height="72"/>
  <h1>Furzefield Leisure Centre</h1>
  <p><em>Group exercise booking management system</em></p>

  ![Java](https://img.shields.io/badge/Java-25.0.2-orange?style=flat-square)
  ![Maven](https://img.shields.io/badge/Maven-3.9.12-red?style=flat-square)
  ![Gson](https://img.shields.io/badge/Gson-2.10.1-blue?style=flat-square)
  ![Module](https://img.shields.io/badge/7COM1025-University%20of%20Hertfordshire-green?style=flat-square)
</div>

---

## Overview

Furzefield Leisure Centre (FLC) is a self-contained desktop application for managing group exercise bookings across an 8-weekend season. Everything runs locally - no server, no login, no internet connection required.

**What it does:**

- Browse the full lesson timetable filtered by day or exercise type
- Book members onto lessons and manage or cancel existing bookings
- Add and edit member records with live search
- Submit and view star-rated lesson reviews
- Generate attendance and income reports with visual data

---

## Documentation

| Document | Description |
|---|---|
| [UML Diagrams](docs/diagrams/README.md) | Class, use case, sequence, and package diagrams |
| [Data Dictionary](docs/data-dictionary.md) | All entities, fields, types, constraints, and valid values |
| [Business Rules](docs/business-rules.md) | All booking, member, review, and persistence rules |
| [Testing](docs/testing.md) | 159 tests across 15 classes - what is tested and why |
| [Data Persistence](docs/data-persistence.md) | JSON save/load strategy, DTO design, and file format |

> Diagrams are generated from PlantUML source files in `src/main/plantuml/`.
> Run `mvn generate-resources` to regenerate after editing a `.puml` file.

---

## Requirements

| Tool  | Version        | Notes                          |
|-------|---------------|--------------------------------|
| Java  | 25.0.2 LTS    | JDK required (not just JRE)    |
| Maven | 3.9.12        | Used for build and dependency management |

Verify your environment before running:

```powershell
java --version
javac --version
mvn --version
```

Expected output:

```
java 25.0.2 2026-01-20 LTS
javac 25.0.2
Apache Maven 3.9.12
```

---

## Project Structure

```
FurzefieldLC/
├── src/
│   ├── main/
│   │   ├── java/com/flc/
│   │   │   ├── config/          # Theme, AppConfig
│   │   │   ├── controller/      # Business logic layer
│   │   │   ├── data/            # DataStore singleton + JSON persistence
│   │   │   ├── model/           # Member, Lesson, Booking, Review, etc.
│   │   │   ├── util/            # ModernTable, ImageUtil
│   │   │   └── view/            # All Swing screens
│   │   ├── plantuml/            # PlantUML source files
│   │   └── resources/
│   │       └── assets/          # PNG icons (logo, nav, feature chips)
│   └── test/                    # JUnit 5 unit tests
├── docs/
│   ├── diagrams/                # UML diagrams (SVG) and dedicated pages
│   ├── data-dictionary.md       # Entity fields, types, and constraints
│   ├── business-rules.md        # All enforced business rules
│   ├── testing.md               # Test coverage documentation
│   └── data-persistence.md      # JSON persistence design
├── pom.xml
└── README.md
```

---

## How to Run

### Option 1 - Run directly with Maven (development)

```powershell
# Compile first
mvn compile

# Run
mvn exec:java
```

### Option 2 - Build a fat JAR and run it

**Step 1 - Package:**

```powershell
mvn package -DskipTests
```

This produces a self-contained JAR at:

```
target/FurzefieldLC-1.0-SNAPSHOT.jar
target/FurzefieldLC.jar
```

All dependencies (Gson) are bundled inside via the Maven Shade plugin.

**Step 2 - Run:**

```powershell
java -jar target/FurzefieldLC.jar
```

### Option 3 - Run tests only

```powershell
mvn test
```

---

## Data Persistence

On first launch, sample data is loaded automatically and saved to:

```
flc-data.json
```

This file is created in whichever directory you run the JAR from. Every change (new booking, member edit, review submission) is saved immediately. Delete the file to reset back to sample data.

See [docs/data-persistence.md](docs/data-persistence.md) for full details on the save/load strategy and file format.

---

## Dependencies

| Library | Version | Purpose                        |
|---------|---------|-------------------------------|
| Gson    | 2.10.1  | JSON serialisation for persistence |
| JUnit 5 | 5.10.0  | Unit testing (test scope only) |

All dependencies are fetched automatically by Maven on first build.

---

## Version

| Field    | Value                           |
|----------|---------------------------------|
| App      | Furzefield LC 1.0               |
| Season   | 2025 / 26                       |
| Java     | 25.0.2 LTS                      |
| Maven    | 3.9.12                          |
| Module   | 7COM1025                        |
| Platform | University of Hertfordshire     |

---

<div align="center">
  <sub>© 2026 Furzefield Leisure Centre &nbsp;·&nbsp; 7COM1025 &nbsp;·&nbsp; University of Hertfordshire</sub>
</div>