# Smart Campus Complaint Management System

[![Java Version](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://www.oracle.com/java/)
[![GUI Framework](https://img.shields.io/badge/GUI-Java%20Swing-orange.svg)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![Architecture](https://img.shields.io/badge/Architecture-4--Tier%20Layered-emerald.svg)]()
[![Tests](https://img.shields.io/badge/Tests-29%2F29%20Passing-brightgreen.svg)]()
[![Academic Project](https://img.shields.io/badge/VIT%20Bhopal-VITyarthi%20BYOP-navy.svg)]()

> A complete, submission-ready academic Java desktop application for campus grievance tracking, departmental work order dispatch, and infrastructure analytics at **VIT Bhopal University**.

---

## 1. Project Overview
The **Smart Campus Complaint Management System** is a standalone Java application engineered to streamline how college students file campus issues and how campus maintenance authorities (Hostel Wardens, Estate Office, IT Infrastructure, Housekeeping) track, assign, update, and resolve those grievances.

Built adhering to the **VITyarthi "Build Your Own Project" guidelines**, the application utilizes **100% pure Java** with a modern Java Swing GUI and robust local binary file serialization (`.dat`). It requires **zero external database setup**, allowing any evaluator or student to run and test the complete system immediately.

---

## 2. Problem Statement
In large residential universities like VIT Bhopal, campus infrastructure undergoes non-stop usage across academic blocks, labs, and hostels. When breakdowns happen—such as water leaks, faulty wiring, Wi-Fi disconnections, or damaged classroom projectors—reporting them via physical registers or emails causes lost tickets, delayed resolutions, and zero tracking for students. This application replaces manual registers with an automated, auditable digital workflow.

---

## 3. Objectives
- Provide a responsive Java Swing interface for students and administrators.
- Auto-generate formatted unique complaint tracking IDs (`CMP-YYYY-XXXX`).
- Enforce business logic rules and status transitions (`SUBMITTED` $\rightarrow$ `IN_PROGRESS` $\rightarrow$ `RESOLVED` / `REJECTED`).
- Provide real-time analytics including resolution rates and category breakdown distributions.
- Implement a 4-tier layered architecture demonstrating core and advanced Java OOP principles.

---

## 4. Key Features & Functional Modules

```
Smart Campus Complaint Management System
│
├── Module 1: Student Management
│   ├── Student self-registration with RegEx validation
│   ├── Secure student login session
│   ├── Academic & hostel profile inspection
│   └── Safe session logout
│
├── Module 2: Complaint Management
│   ├── Form filing across 7 campus categories
│   ├── 4-tier priority levels (Low, Medium, High, Urgent)
│   ├── Atomic unique ticket ID generator (CMP-YYYY-XXXX)
│   ├── Search complaints by keyword, title, ID, or room
│   ├── Filter complaints by status, category, and priority
│   └── Ticket inspection modal with audit history and timestamps
│
├── Module 3: Administrator Console
│   ├── Dedicated admin login
│   ├── System-wide complaints directory table
│   ├── Assign work orders to maintenance departments/staff
│   ├── Update complaint statuses with validation checks
│   ├── Add official administrator remarks
│   ├── Quick 1-click ticket resolution with resolution notes
│   └── View student profile linked to complaint
│
└── Module 4: Reports & Analytics
    ├── High-level KPI metric cards (Total, Submitted, In Progress, Resolved, Urgent)
    ├── Dynamic Resolution Rate percentage calculation
    ├── Visual category distribution meter bars
    └── Priority distribution charts
```

---

## 5. Java Concepts Demonstrated

| Java Concept | Implementation in Codebase |
| :--- | :--- |
| **Classes and Objects** | Domain entities in `model/`: `User`, `Student`, `Admin`, `Complaint` |
| **Encapsulation** | Private variables, getters/setters, validation checks in setters |
| **Inheritance** | Abstract class `User` extended by `Student` and `Admin` |
| **Polymorphism** | Overridden methods (`getRoleTitle()`, `getSummary()`, `toString()`), generic repository contracts |
| **Abstraction & Interfaces** | `Identifiable<ID>`, `Repository<T, ID>`, abstract `FileRepository` |
| **Constructors** | Parameterized constructors and overloaded constructor chaining (`this(...)`) |
| **Method Overloading** | 6 overloaded signatures for search and filter queries in `ComplaintService.java` |
| **Method Overriding** | Custom implementation of `toString()`, `equals()`, `hashCode()`, and `compareTo()` |
| **Collections Framework** | `ArrayList`, `LinkedHashMap`, `Map`, Java Streams API (`Collectors.groupingBy`) |
| **Exception Handling** | Robust `try-catch-finally` blocks and `try-with-resources` ensuring stream closures |
| **Custom Exceptions** | Hierarchical exceptions in `exception/` (`ValidationException`, `AuthenticationException`, `DuplicateResourceException`, `ResourceNotFoundException`) |
| **File Persistence & Serialization** | Object serialization to `.dat` files with atomic temp-file replace pattern |
| **Date and Time API** | Modern `java.time.LocalDateTime`, `DateTimeFormatter`, and duration calculations in `DateTimeUtil.java` |
| **Enums** | Strongly typed `ComplaintCategory`, `ComplaintPriority`, `ComplaintStatus`, `UserRole` |
| **Modular Packages** | Clean separation: `model`, `service`, `repository`, `ui`, `util`, `exception`, `main` |
| **Input Validation** | RegEx validation for email, 10-digit Indian phone numbers, and VIT registration numbers |

---

## 6. Technologies Used
- **Core Technology**: Java SE 17 or higher
- **UI Framework**: Java Swing (with custom `UITheme` design tokens, badges, and card panels)
- **Data Persistence**: Java Object Serialization (`.dat` binary storage)
- **Testing**: Standalone automated test runner (`TestSuiteRunner.java`) with 29 automated unit & integration tests
- **Build System**: Standalone Bash scripts (`compile.sh`, `run.sh`, `test.sh`) + Maven `pom.xml`

---

## 7. Architecture & System Flow

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer (UI)                  │
│   LoginFrame  │  StudentDashboard  │  AdminDashboard        │
│   RegisterDialog │ ComplaintDetailsDialog │ AnalyticsPanel  │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│                  Service / Business Logic Layer             │
│    AuthService    │    ComplaintService   │  ReportService  │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│                 Repository / Data Access Layer              │
│  Repository<T, ID> (Interface) │ FileRepository<T, ID>      │
│  StudentRepository │ AdminRepository │ ComplaintRepository  │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│                    Persistent Storage Layer                 │
│         data/students.dat │ data/admins.dat                 │
│                 data/complaints.dat                         │
└─────────────────────────────────────────────────────────────┘
```

---

## 8. Repository Folder Structure

```
Smart-Campus-Complaint-Management/
├── src/
│   ├── exception/
│   │   ├── AuthenticationException.java
│   │   ├── ComplaintManagementException.java
│   │   ├── DuplicateResourceException.java
│   │   ├── ResourceNotFoundException.java
│   │   └── ValidationException.java
│   ├── main/
│   │   └── Main.java
│   ├── model/
│   │   ├── Admin.java
│   │   ├── Complaint.java
│   │   ├── ComplaintCategory.java
│   │   ├── ComplaintPriority.java
│   │   ├── ComplaintStatus.java
│   │   ├── Student.java
│   │   ├── User.java
│   │   └── UserRole.java
│   ├── repository/
│   │   ├── AdminRepository.java
│   │   ├── ComplaintRepository.java
│   │   ├── FileRepository.java
│   │   ├── Identifiable.java
│   │   ├── Repository.java
│   │   └── StudentRepository.java
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── ComplaintService.java
│   │   └── ReportService.java
│   ├── ui/
│   │   ├── AdminDashboardFrame.java
│   │   ├── AnalyticsPanel.java
│   │   ├── ComplaintDetailsDialog.java
│   │   ├── LoginFrame.java
│   │   ├── RegisterDialog.java
│   │   └── StudentDashboardFrame.java
│   └── util/
│       ├── DataInitializer.java
│       ├── DateTimeUtil.java
│       ├── InputValidator.java
│       └── UITheme.java
├── tests/
│   ├── AuthServiceTest.java
│   ├── ComplaintServiceTest.java
│   ├── FileRepositoryTest.java
│   ├── InputValidatorTest.java
│   ├── ReportServiceTest.java
│   ├── ScreenshotGenerator.java
│   ├── SwingPanelCapture.java
│   └── TestSuiteRunner.java
├── docs/
│   ├── architecture_diagram.md
│   ├── class_diagram.md
│   ├── PROJECT_REPORT.md
│   ├── sequence_diagrams.md
│   ├── storage_design.md
│   ├── use_case_diagram.md
│   └── workflow_diagram.md
├── screenshots/
│   ├── 01_login_screen.png
│   ├── 02_student_registration.png
│   ├── 03_student_dashboard_my_complaints.png
│   ├── 04_student_submit_complaint.png
│   ├── 05_complaint_details_modal.png
│   ├── 06_admin_dashboard_management.png
│   └── 07_campus_analytics_reports.png
├── data/
│   ├── .gitkeep
│   ├── admins.dat         (Generated on first run)
│   ├── students.dat       (Generated on first run)
│   └── complaints.dat     (Generated on first run)
├── compile.sh             (1-click compile script)
├── run.sh                 (1-click launch script)
├── test.sh                (1-click test execution script)
├── pom.xml                (Maven configuration for IDEs)
├── statement.md           (VITyarthi Project Statement)
├── README.md              (Project Documentation)
└── .gitignore             (Git exclusions)
```

---

## 9. Installation & How to Run

### Prerequisites
- **Operating System**: macOS, Windows, or Linux
- **Java**: Java Development Kit (JDK) 17 or higher

### Step-by-Step Execution

#### Option A: Quick Run using Provided Shell Scripts (Recommended)

1. **Clone or download** the repository:
   ```bash
   git clone <repository-url>
   cd "Smart-Campus-Complaint-Management"
   ```

2. **Compile the application**:
   ```bash
   ./compile.sh
   ```

3. **Run the application**:
   ```bash
   ./run.sh
   ```

4. **Run the automated unit test suite**:
   ```bash
   ./test.sh
   ```

#### Option B: Using Standard Maven
```bash
mvn compile
mvn exec:java -Dexec.mainClass="main.Main"
```

#### Option C: Import into Any IDE
- **IntelliJ IDEA**: File $\rightarrow$ Open $\rightarrow$ Select project root folder $\rightarrow$ Run `main.Main`.
- **Eclipse / NetBeans / VS Code**: Open as Java/Maven project $\rightarrow$ Run `src/main/Main.java`.

---

## 10. Default Demo Credentials

The application automatically seeds realistic demo data on initial launch so you can test all features immediately without manual data entry. You can also click the quick-fill buttons on the login screen!

| Role | Username | Password | Full Name / Description |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin` | `admin123` | Dr. Rajesh Sharma (Chief Campus Administrator, Estate Office) |
| **Administrator** | `netadmin` | `admin123` | Er. Vikram Patel (Senior Network Engineer, IT Operations) |
| **Student** | `atharv` | `student123` | Atharv Kulkarni (22BCE10234, SCOPE, Boys Hostel Block 1, Room 312) |
| **Student** | `priya` | `student123` | Priya Sharma (22BCE10589, SCSE, Girls Hostel Block A, Room 204) |
| **Student** | `rahul` | `student123` | Rahul Verma (21BME10042, SEEE, Boys Hostel Block 2, Room 108) |

---

## 11. Testing & Validation Results

To run the full suite of automated unit tests:
```bash
./test.sh
```

### Summary of Automated Unit Tests (29/29 Passing)
- **Input Validation**: Tests regex email checking, 10-digit mobile phone constraints, VIT registration number format, empty titles, and minimum text boundaries.
- **Authentication Service**: Tests student registration, duplicate detection, valid/invalid password logins, role isolation, and session logout.
- **Complaint Service**: Tests atomic sequential ID generation (`CMP-YYYY-XXXX`), valid status transitions, illegal transition prevention, staff assignment, ticket resolution, and multi-criteria queries.
- **File Persistence**: Tests saving records to disk, reloading across separate repository instances, in-place record updates, and deletion.
- **Reporting & Analytics**: Tests metric calculations, status counts, resolution rate percentage formulas, and category breakdown mapping.
- **End-to-End Integration Lifecycle**: Tests end-to-end student filing, admin staff assignment, resolution, report metric updates, and full storage reload across simulated restart.

```
========================================================================================================================
#    | TEST CASE DESCRIPTION                         | EXPECTED RESULT                  | ACTUAL RESULT            | STATUS  
------------------------------------------------------------------------------------------------------------------------
1    | InputValidator: Valid email passes            | Validation succeeded without ... | No exception thrown      | [PASS]  
2    | InputValidator: Malformed email throws Val... | ValidationException thrown       | Please provide a vali... | [PASS]  
3    | InputValidator: Valid 10-digit Indian mobi... | Validation succeeded without ... | Valid phone accepted     | [PASS]  
4    | InputValidator: 5-digit phone throws Valid... | ValidationException thrown       | Please enter a valid ... | [PASS]  
5    | InputValidator: Valid registration number ... | Validation succeeded without ... | Registration number a... | [PASS]  
6    | InputValidator: Short registration number ... | ValidationException thrown       | Registration number s... | [PASS]  
7    | InputValidator: Valid complaint input passes  | Validation succeeded without ... | Complaint input accepted | [PASS]  
8    | InputValidator: Empty complaint title thro... | ValidationException thrown       | Complaint Title canno... | [PASS]  
9    | AuthService: Student registration creates ... | Student registered with corre... | Student[regNo=22BCE20... | [PASS]  
10   | AuthService: Duplicate username registrati... | DuplicateResourceException ca... | Username already exis... | [PASS]  
11   | AuthService: Duplicate registration number... | DuplicateResourceException ca... | Registration Number a... | [PASS]  
12   | AuthService: Student login with valid cred... | Authenticated student session... | Login User               | [PASS]  
13   | AuthService: Student login with bad passwo... | AuthenticationException caught   | Invalid student usern... | [PASS]  
14   | AuthService: Admin login with valid creden... | Admin session verified           | Admin: Test Admin [AD... | [PASS]  
15   | AuthService: Logout resets session state t... | Current user session cleared     | isAuthenticated = false  | [PASS]  
16   | ComplaintService: Complaint submission gen... | Valid ID format starting with... | CMP-2026-1001            | [PASS]  
17   | ComplaintService: Subsequent complaints in... | Distinct sequential IDs gener... | CMP-2026-1001 -> CMP-... | [PASS]  
18   | ComplaintService: Valid status transition ... | Status updated to IN_PROGRESS    | In Progress              | [PASS]  
19   | ComplaintService: Invalid transition RESOL... | ValidationException caught fo... | Invalid status transi... | [PASS]  
20   | ComplaintService: Assignment sets staff an... | Assigned to staff and moved t... | Chief Electrician - M... | [PASS]  
21   | ComplaintService: Resolving complaint sets... | Resolved with timestamp          | 2026-09-18T23:11:16.0... | [PASS]  
22   | ComplaintService: Search finds complaints ... | Exact keyword match found        | Wi-Fi signal drop in ... | [PASS]  
23   | ComplaintService: Multi-criteria filter fi... | Filtered correctly to single ... | Door lock stuck          | [PASS]  
24   | FileRepository: Entities persist to disk a... | Entity successfully reloaded ... | Student[regNo=22BCE99... | [PASS]  
25   | FileRepository: Entity modification update... | Updated room number 505 verif... | 505                      | [PASS]  
26   | FileRepository: Deletion removes record fr... | Entity deleted and confirmed ... | Count = 0                | [PASS]  
27   | ReportService: Accurately calculates statu... | Total=4, Resolved=2, Rate=50.0%  | Total=4, Resolved=2, ... | [PASS]  
28   | ReportService: Category and priority distr... | All category and priority key... | Categories=7, Priorit... | [PASS]  
29   | Integration: End-to-end student filing -> ... | Lifecycle & persistence verif... | Ticket CMP-2026-1001 ... | [PASS]  
========================================================================================================================
TEST RUN SUMMARY: 29 Total | 29 Passed | 0 Failed | Pass Rate: 100.0% | Time: 51 ms
========================================================================================================================
SUCCESS: All unit tests passed without any errors!
```

---

## 12. Application Screenshots

### 12.1 Welcome & Authentication Portal
![Login Screen](screenshots/01_login_screen.png)

### 12.2 Student Registration Dialog
![Student Registration](screenshots/02_student_registration.png)

### 12.3 Student Dashboard - My Complaints
![Student Dashboard](screenshots/03_student_dashboard_my_complaints.png)

### 12.4 Filing a New Campus Complaint
![Submit Complaint Form](screenshots/04_student_submit_complaint.png)

### 12.5 Complaint Audit & Inspection Modal
![Complaint Details](screenshots/05_complaint_details_modal.png)

### 12.6 Administrator Management Console
![Admin Dashboard](screenshots/06_admin_dashboard_management.png)

### 12.7 Campus Analytics & KPI Reports
![Analytics Reports](screenshots/07_campus_analytics_reports.png)

---

## 13. Future Enhancements
- **Email & SMS Alerts**: Automated notification triggers to students when status changes to `IN_PROGRESS` or `RESOLVED`.
- **Photo Attachments**: Ability for students to attach image evidence to complaints.
- **PDF Report Exporter**: Direct export of campus maintenance KPI summaries to formal PDF reports.
- **Student Feedback**: Star ratings and review comments upon issue resolution.

---

## 14. Academic Integrity & License
This project was authored independently for the **Java Academic Course** at **VIT Bhopal University** under the **VITyarthi "Build Your Own Project" Framework**. It is licensed under the MIT License for educational and academic evaluation purposes.
