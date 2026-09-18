# Project Statement: Smart Campus Complaint Management System

**Academic Subject**: Java Programming (VITyarthi "Build Your Own Project" Framework)  
**Institution**: Vellore Institute of Technology (VIT), Bhopal  
**Course Code**: CSE2001 / CSE2006  

---

## 1. Problem Statement
In large residential higher-education institutions such as VIT Bhopal University, campus facilities, hostel infrastructure, utilities (electricity, water supply, RO purifiers), internet access, and classroom equipment undergo continuous usage by thousands of students and faculty members. 

Currently, students often face operational friction when attempting to lodge grievance tickets:
- Complaints are reported through fragmented channels (manual registers at hostel warden offices, unorganized email threads, or verbal complaints to floor caretakers).
- Students lack visibility into ticket status, assigned personnel, or expected turnaround times.
- Campus administrative staff and maintenance departments lack a centralized tracking and analytics system, leading to delayed escalations, duplicate work orders, and unaddressed critical infrastructure issues (e.g., electrical sparks or plumbing emergencies).

There is an acute need for a lightweight, robust, self-contained desktop system built with pure Java that empowers students to submit and monitor complaints with unique tracking identifiers, while equipping administrators with role-based consoles to assign, update, resolve, and analyze campus maintenance workflows.

---

## 2. Project Scope
The **Smart Campus Complaint Management System** is a standalone, cross-platform Java desktop application featuring:
- **Zero External Database Overhead**: Employs structured Java Object Serialization (`.dat` files) for local persistence with zero server installation requirements, making it completely offline-capable and simple to evaluate.
- **Strict Separation of Concerns**: Built on a 4-tier layered architecture (Presentation Layer $\rightarrow$ Service/Business Logic Layer $\rightarrow$ Repository/Data Access Layer $\rightarrow$ Persistent File Storage).
- **Core OOP Rigor**: Purposefully designed to demonstrate fundamental and advanced Java concepts including Encapsulation, Inheritance, Polymorphism, Abstraction, Generics, Collections Framework, Exception Handling with custom exception hierarchies, and the modern Java Date/Time API.

### Out of Scope
- Direct internet/cloud database sync (omitted by design to satisfy offline and zero-configuration evaluation requirements).
- Third-party commercial SMS gateways (handled locally via structured ticket audit logs and admin remarks).

---

## 3. Target Users

| User Persona | Key Responsibilities & Capabilities |
| :--- | :--- |
| **College Students** | - Register student profiles with academic and hostel credentials.<br>- Submit maintenance tickets with category and priority tagging.<br>- Track real-time progress and view official administrative remarks.<br>- Search and filter historical complaints. |
| **Campus Administrators** | - Chief Warden, Estate Officers, IT Support, Maintenance Engineers.<br>- Review system-wide complaints across all hostels and academic blocks.<br>- Assign work orders to specialized departmental staff.<br>- Enforce status transitions (`SUBMITTED` $\rightarrow$ `IN_PROGRESS` $\rightarrow$ `RESOLVED` / `REJECTED`).<br>- Review student profiles associated with complaints.<br>- Analyze category distributions and resolution rates via analytics dashboards. |

---

## 4. High-Level Features

### Module 1: Student Management
- Student self-registration with multi-field input validation (RegEx email, 10-digit Indian phone, registration number format).
- Secure authentication session handling.
- Student profile view reflecting department, hostel block, room number, and contact details.
- Clean session logout.

### Module 2: Complaint Lifecycle Management
- Interactive complaint submission form with priority assignment (`LOW`, `MEDIUM`, `HIGH`, `URGENT`).
- 7 campus categories: Hostel, Electricity, Water, Classroom, Internet / Wi-Fi, Cleanliness, Other.
- Auto-generated atomic unique Complaint IDs formatted as `CMP-YYYY-XXXX`.
- Search and multi-criteria filtering by keywords, status, category, and priority.
- Comprehensive Complaint Inspection Dialog displaying audit history, timestamps, and admin remarks.

### Module 3: Administrator Console & Work Orders
- Centralized administrative management table.
- Work order assignment to departments and maintenance staff.
- Controlled status updates enforcing valid state transitions.
- Resolution workflows with compulsory resolution notes.
- Quick student contact inspection linked to tickets.

### Module 4: Campus Analytics & Reporting
- Summary KPI metrics: Total tickets, Pending/Submitted, In-Progress, Resolved, and Urgent Pending tickets.
- Resolution Rate percentage calculations.
- Visual distribution meters for campus problem categories and priority levels.
- Real-time refresh of metrics following administrative actions.
