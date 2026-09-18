# Use Case Diagram: Smart Campus Complaint Management System

```mermaid
graph LR
    subgraph Actors
        STU["Student User"]
        ADM["Campus Administrator"]
    end

    subgraph System_Boundary ["Smart Campus Complaint Management System"]
        UC1(["Register Student Profile"])
        UC2(["Login with Role Credentials"])
        UC3(["View Student Profile"])
        UC4(["Submit New Campus Complaint"])
        UC5(["Generate Unique Complaint ID"])
        UC6(["View Submitted Complaints"])
        UC7(["Search and Filter Complaints"])
        UC8(["View Full Complaint Details"])
        UC9(["Assign Complaint to Department/Staff"])
        UC10(["Update Ticket Status"])
        UC11(["Add Official Admin Remarks"])
        UC12(["Resolve Complaint Ticket"])
        UC13(["Inspect Student Information"])
        UC14(["View Analytics & Category Breakdown"])
        UC15(["Logout Session"])
    end

    %% Student Relationships
    STU --> UC1
    STU --> UC2
    STU --> UC3
    STU --> UC4
    STU --> UC6
    STU --> UC7
    STU --> UC8
    STU --> UC14
    STU --> UC15

    %% Admin Relationships
    ADM --> UC2
    ADM --> UC6
    ADM --> UC7
    ADM --> UC8
    ADM --> UC9
    ADM --> UC10
    ADM --> UC11
    ADM --> UC12
    ADM --> UC13
    ADM --> UC14
    ADM --> UC15

    %% Includes & Extends
    UC4 -.->|&lt;&lt;include&gt;&gt;| UC5
    UC9 -.->|&lt;&lt;extend&gt;&gt;| UC10
    UC12 -.->|&lt;&lt;include&gt;&gt;| UC11
```

### Use Case Specifications

| Use Case ID | Name | Primary Actor | Description |
| :--- | :--- | :--- | :--- |
| **UC-01** | Register Profile | Student | Enters registration number, contact, and hostel room to register. |
| **UC-02** | Login | Student / Admin | Validates credentials against serialized file store. |
| **UC-04** | Submit Complaint | Student | Files new maintenance ticket; validates non-empty and length constraints. |
| **UC-05** | Generate ID | System (Service) | Auto-assigns sequential formatted ID `CMP-YYYY-XXXX`. |
| **UC-07** | Search & Filter | Student / Admin | Filters complaints by keyword, category, status, and priority. |
| **UC-09** | Assign Staff | Admin | Allocates ticket to department or staff, moving status to `IN_PROGRESS`. |
| **UC-10** | Update Status | Admin | Advances status while validating permissible state transitions. |
| **UC-12** | Resolve Ticket | Admin | Marks ticket `RESOLVED`, adds resolution remarks, and timestamps completion. |
| **UC-14** | View Analytics | Student / Admin | Displays KPI metrics, category distribution meters, and resolution rates. |
