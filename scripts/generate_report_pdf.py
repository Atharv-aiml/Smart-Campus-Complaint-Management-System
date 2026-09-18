#!/usr/bin/env python3
"""
Academic Project Report PDF Generator for VIT Bhopal University.
Generates docs/PROJECT_REPORT.pdf adhering to all VITyarthi formatting requirements.
"""

import os
from reportlab.lib.pagesizes import letter
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib import colors
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, Image, PageBreak, KeepTogether, HRFlowable
)

def create_report_pdf():
    pdf_path = "docs/PROJECT_REPORT.pdf"
    doc = SimpleDocTemplate(
        pdf_path,
        pagesize=letter,
        leftMargin=54,
        rightMargin=54,
        topMargin=54,
        bottomMargin=54
    )

    styles = getSampleStyleSheet()
    
    # Custom Palette
    NAVY = colors.HexColor("#1E3A8A")
    SLATE = colors.HexColor("#0F172A")
    MUTED = colors.HexColor("#475569")
    LIGHT_BG = colors.HexColor("#F8FAFC")
    BORDER_CLR = colors.HexColor("#CBD5E1")

    # Custom Typography Styles
    title_style = ParagraphStyle(
        'DocTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=24,
        leading=30,
        textColor=NAVY,
        alignment=1, # Center
        spaceAfter=15
    )

    subtitle_style = ParagraphStyle(
        'DocSubtitle',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=12,
        leading=16,
        textColor=MUTED,
        alignment=1,
        spaceAfter=25
    )

    h1_style = ParagraphStyle(
        'Header1',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=16,
        leading=20,
        textColor=NAVY,
        spaceBefore=18,
        spaceAfter=8,
        keepWithNext=True
    )

    h2_style = ParagraphStyle(
        'Header2',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=13,
        leading=16,
        textColor=SLATE,
        spaceBefore=12,
        spaceAfter=6,
        keepWithNext=True
    )

    body_style = ParagraphStyle(
        'Body',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=10,
        leading=14,
        textColor=SLATE,
        spaceAfter=6
    )

    bullet_style = ParagraphStyle(
        'Bullet',
        parent=body_style,
        leftIndent=15,
        firstLineIndent=-10,
        spaceAfter=4
    )

    meta_style = ParagraphStyle(
        'Meta',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=10,
        leading=13,
        textColor=NAVY
    )

    story = []

    # ==========================================
    # COVER PAGE
    # ==========================================
    story.append(Spacer(1, 40))
    story.append(Paragraph("VELLORE INSTITUTE OF TECHNOLOGY (VIT), BHOPAL", ParagraphStyle('Inst', fontName='Helvetica-Bold', fontSize=14, leading=18, alignment=1, textColor=NAVY)))
    story.append(Paragraph("School of Computing Science and Engineering (SCOPE / SCSE)", subtitle_style))
    story.append(Spacer(1, 40))

    story.append(Paragraph("SMART CAMPUS COMPLAINT MANAGEMENT SYSTEM", title_style))
    story.append(Paragraph("A Desktop Grievance Tracking, Automated Dispatch, and Infrastructure Analytics System", subtitle_style))
    story.append(Spacer(1, 30))

    cover_meta = [
        [Paragraph("<b>Course:</b>", body_style), Paragraph("Java Programming (CSE2001 / CSE2006)", body_style)],
        [Paragraph("<b>Framework:</b>", body_style), Paragraph("VITyarthi 'Build Your Own Project' (BYOP)", body_style)],
        [Paragraph("<b>Academic Year:</b>", body_style), Paragraph("2026 – 2027", body_style)],
        [Paragraph("<b>Core Technology:</b>", body_style), Paragraph("Pure Java (Java SE 17+), Java Swing GUI", body_style)],
        [Paragraph("<b>Persistence:</b>", body_style), Paragraph("Native Java Object Serialization (.dat files)", body_style)],
        [Paragraph("<b>Architecture:</b>", body_style), Paragraph("4-Tier Layered Architecture (UI - Service - Repo - Storage)", body_style)],
        [Paragraph("<b>Test Suite:</b>", body_style), Paragraph("29 Automated Unit & Integration Tests (100% Passing)", body_style)],
        [Paragraph("<b>Deliverables:</b>", body_style), Paragraph("Source Code, Data Store, Design Diagrams, Tests, Report", body_style)]
    ]
    t_cover = Table(cover_meta, colWidths=[150, 350])
    t_cover.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), LIGHT_BG),
        ('GRID', (0,0), (-1,-1), 1, BORDER_CLR),
        ('PADDING', (0,0), (-1,-1), 8),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
    ]))
    story.append(t_cover)
    story.append(PageBreak())

    # ==========================================
    # 2. INTRODUCTION & PROBLEM STATEMENT
    # ==========================================
    story.append(Paragraph("1. Executive Summary & Introduction", h1_style))
    story.append(Paragraph(
        "Modern residential universities like VIT Bhopal host thousands of resident students across extensive campus infrastructure. "
        "Operational efficiency requires instant detection, tracking, and resolution of infrastructural breakdowns—including electrical hazards, "
        "water leakage, Wi-Fi drops, and classroom equipment failures. The Smart Campus Complaint Management System replaces fragmented physical registers "
        "with an automated, robust Java desktop system offering end-to-end transparency for both students and campus administration.",
        body_style
    ))
    story.append(Spacer(1, 10))

    story.append(Paragraph("2. Problem Statement", h1_style))
    story.append(Paragraph(
        "Manual complaint handling at university hostels suffers from lost paper registers, delayed escalations, zero student tracking visibility, "
        "and a total lack of quantitative performance analytics. Critical safety emergencies (e.g. electrical sparking) compete with routine requests "
        "without priority triage. There is an urgent need for a standalone, zero-server-dependency Java system enabling automated ticket generation, "
        "controlled state transitions, and real-time maintenance analytics.",
        body_style
    ))
    story.append(Spacer(1, 10))

    story.append(Paragraph("3. Project Objectives", h1_style))
    story.append(Paragraph("• Provide a responsive, professional Java Swing desktop interface for students and campus authorities.", bullet_style))
    story.append(Paragraph("• Automatically generate unique, standardized tracking IDs formatted as <code>CMP-YYYY-XXXX</code>.", bullet_style))
    story.append(Paragraph("• Implement role-based separation of concerns across Student and Administrator personas.", bullet_style))
    story.append(Paragraph("• Enforce valid status transitions (SUBMITTED → IN_PROGRESS → RESOLVED / REJECTED) with administrative remarks.", bullet_style))
    story.append(Paragraph("• Deliver live KPI summaries, resolution rate metrics, and category distribution charts.", bullet_style))
    story.append(Paragraph("• Ensure 100% offline portability and file persistence via safe, atomic Java Object Serialization.", bullet_style))
    story.append(Spacer(1, 10))

    # ==========================================
    # 4. FUNCTIONAL & NON-FUNCTIONAL REQUIREMENTS
    # ==========================================
    story.append(Paragraph("4. Functional Requirements (4 Core Modules)", h1_style))
    modules_data = [
        [Paragraph("<b>Module</b>", meta_style), Paragraph("<b>Key Functional Capabilities</b>", meta_style)],
        [Paragraph("<b>Module 1: Student Management</b>", body_style), Paragraph("Student self-registration with RegEx validation (email, 10-digit phone, VIT reg number), credential login, profile inspection (hostel room, department), and session logout.", body_style)],
        [Paragraph("<b>Module 2: Complaint Management</b>", body_style), Paragraph("Ticket filing across 7 categories (Hostel, Electricity, Water, Classroom, Wi-Fi, Cleanliness, Other), 4 priorities (Low, Medium, High, Urgent), atomic ID generation, keyword search, and detailed inspection modal.", body_style)],
        [Paragraph("<b>Module 3: Admin Management</b>", body_style), Paragraph("System-wide complaint directory, departmental work order assignment (moves status to IN_PROGRESS), transition validation, official remarks, 1-click resolution, and linked student info lookup.", body_style)],
        [Paragraph("<b>Module 4: Reports & Analytics</b>", body_style), Paragraph("KPI summary cards (Total, Pending, In-Progress, Resolved, Urgent), Resolution Rate % computation, visual category distribution progress meters, and priority charts.", body_style)]
    ]
    t_mod = Table(modules_data, colWidths=[160, 340])
    t_mod.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#E2E8F0")),
        ('GRID', (0,0), (-1,-1), 1, BORDER_CLR),
        ('PADDING', (0,0), (-1,-1), 6),
        ('VALIGN', (0,0), (-1,-1), 'TOP')
    ]))
    story.append(t_mod)
    story.append(Spacer(1, 12))

    story.append(Paragraph("5. Non-Functional Requirements", h1_style))
    story.append(Paragraph("• <b>Usability:</b> Tailored UI design tokens (Navy, Slate, Emerald, Amber) with styled cards, table badges, and responsive layouts.", bullet_style))
    story.append(Paragraph("• <b>Reliability:</b> Atomic file write pattern with temporary files and atomic moves, preventing data corruption upon unexpected termination.", bullet_style))
    story.append(Paragraph("• <b>Maintainability:</b> Strict 4-tier layered architecture separating presentation from business logic and data access.", bullet_style))
    story.append(Paragraph("• <b>Performance:</b> In-memory LinkedHashMap indexing enabling sub-millisecond table queries and multi-criteria filters.", bullet_style))
    story.append(Paragraph("• <b>Security & Input Validation:</b> RegEx validation for email, phone, and registration numbers preventing malformed data.", bullet_style))
    story.append(Spacer(1, 10))

    # ==========================================
    # 6. ARCHITECTURE & OOP CONCEPTS
    # ==========================================
    story.append(Paragraph("6. System Architecture & Java OOP Implementation", h1_style))
    story.append(Paragraph(
        "The application strictly enforces a 4-tier layered architecture: Presentation Layer (Swing GUI) → "
        "Service Layer (Business Rules & ID Generation) → Repository Layer (Data Access & Synchronization) → "
        "File Storage Layer (Binary .dat Files).",
        body_style
    ))
    story.append(Spacer(1, 6))

    oop_data = [
        [Paragraph("<b>Java Concept</b>", meta_style), Paragraph("<b>Implementation in Codebase</b>", meta_style)],
        [Paragraph("<b>Encapsulation</b>", body_style), Paragraph("Private fields with validation guards in setters across User, Student, Admin, Complaint.", body_style)],
        [Paragraph("<b>Inheritance</b>", body_style), Paragraph("Abstract User class extended by concrete Student and Admin subclasses.", body_style)],
        [Paragraph("<b>Polymorphism</b>", body_style), Paragraph("Polymorphic methods getRoleTitle() and getSummary(); generic Repository CRUD contracts.", body_style)],
        [Paragraph("<b>Abstraction & Interfaces</b>", body_style), Paragraph("Identifiable<ID> and Repository<T, ID> decouple high-level services from physical storage.", body_style)],
        [Paragraph("<b>Method Overloading</b>", body_style), Paragraph("6 overloaded search and filter query signatures in ComplaintService.java.", body_style)],
        [Paragraph("<b>Method Overriding</b>", body_style), Paragraph("Overridden toString(), equals(), hashCode(), and compareTo() across domain models.", body_style)],
        [Paragraph("<b>Collections & Streams</b>", body_style), Paragraph("ArrayList, LinkedHashMap, Map, and Java Streams API (Collectors.groupingBy, counting).", body_style)],
        [Paragraph("<b>Custom Exceptions</b>", body_style), Paragraph("Hierarchy rooted in ComplaintManagementException (ValidationException, AuthenticationException, etc.).", body_style)],
        [Paragraph("<b>Date & Time API</b>", body_style), Paragraph("Modern java.time.LocalDateTime and DateTimeFormatter in DateTimeUtil.java.", body_style)],
        [Paragraph("<b>File Serialization</b>", body_style), Paragraph("ObjectInputStream, ObjectOutputStream with atomic move pattern in FileRepository.java.", body_style)]
    ]
    t_oop = Table(oop_data, colWidths=[150, 350])
    t_oop.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#E2E8F0")),
        ('GRID', (0,0), (-1,-1), 1, BORDER_CLR),
        ('PADDING', (0,0), (-1,-1), 5),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE')
    ]))
    story.append(t_oop)
    story.append(PageBreak())

    # ==========================================
    # 7. SCREENSHOTS GALLERY
    # ==========================================
    story.append(Paragraph("7. Application Screenshots & Results", h1_style))
    story.append(Paragraph("Actual screenshots captured directly from the live Java Swing desktop components:", body_style))
    story.append(Spacer(1, 8))

    shots = [
        ("screenshots/01_login_screen.png", "Figure 1: Welcome & Login Portal with Role Switcher and Demo Credentials", 240, 290),
        ("screenshots/03_student_dashboard_my_complaints.png", "Figure 2: Student Dashboard with KPI Cards and Filterable Complaints Table", 450, 300),
        ("screenshots/04_student_submit_complaint.png", "Figure 3: Complaint Submission Form with Category and Priority Tiers", 450, 300),
        ("screenshots/06_admin_dashboard_management.png", "Figure 4: Administrator Management Console with Multi-Criteria Filters & Actions", 450, 290),
        ("screenshots/07_campus_analytics_reports.png", "Figure 5: Real-Time Campus Analytics with Category Meters and Resolution Rates", 450, 300)
    ]

    for img_path, caption, w, h in shots:
        if os.path.exists(img_path):
            story.append(KeepTogether([
                Image(img_path, width=w, height=h),
                Spacer(1, 4),
                Paragraph(f"<i>{caption}</i>", ParagraphStyle('Cap', fontName='Helvetica-Oblique', fontSize=9, alignment=1, textColor=MUTED)),
                Spacer(1, 14)
            ]))

    story.append(PageBreak())

    # ==========================================
    # 8. TESTING & COMPLIANCE
    # ==========================================
    story.append(Paragraph("8. Testing Approach & Automated Test Results", h1_style))
    story.append(Paragraph(
        "The project includes a standalone automated unit and integration test runner (TestSuiteRunner.java) "
        "executing 29 tests across all core modules with a 100% pass rate:",
        body_style
    ))
    story.append(Spacer(1, 6))

    test_table_data = [
        [Paragraph("<b>#</b>", meta_style), Paragraph("<b>Test Case Description</b>", meta_style), Paragraph("<b>Expected Result</b>", meta_style), Paragraph("<b>Status</b>", meta_style)],
        ["1-8", Paragraph("InputValidator: Email, Phone, RegNo, Length boundaries", body_style), Paragraph("Valid inputs pass; invalid throw ValidationException", body_style), Paragraph("<font color='#10B981'><b>PASS</b></font>", body_style)],
        ["9-15", Paragraph("AuthService: Student registration, duplicates, login, logout", body_style), Paragraph("Accounts created; duplicates prevented; sessions isolated", body_style), Paragraph("<font color='#10B981'><b>PASS</b></font>", body_style)],
        ["16-23", Paragraph("ComplaintService: IDs, transitions, assignments, search", body_style), Paragraph("Sequential CMP-YYYY-XXXX generated; transitions guarded", body_style), Paragraph("<font color='#10B981'><b>PASS</b></font>", body_style)],
        ["24-26", Paragraph("FileRepository: Persistence, reload, modification, deletion", body_style), Paragraph("Records reload cleanly across fresh repo instances", body_style), Paragraph("<font color='#10B981'><b>PASS</b></font>", body_style)],
        ["27-28", Paragraph("ReportService: Counts, resolution rate %, distribution maps", body_style), Paragraph("Accurate formula calculation and 100% key completeness", body_style), Paragraph("<font color='#10B981'><b>PASS</b></font>", body_style)],
        ["29", Paragraph("Integration: End-to-end filing → assignment → resolution → disk reload", body_style), Paragraph("Complete lifecycle verified across storage reload", body_style), Paragraph("<font color='#10B981'><b>PASS</b></font>", body_style)]
    ]
    t_test = Table(test_table_data, colWidths=[30, 210, 200, 60])
    t_test.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#E2E8F0")),
        ('GRID', (0,0), (-1,-1), 1, BORDER_CLR),
        ('PADDING', (0,0), (-1,-1), 6),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE')
    ]))
    story.append(t_test)
    story.append(Spacer(1, 14))

    # ==========================================
    # 9. CHALLENGES & REFERENCES
    # ==========================================
    story.append(Paragraph("9. Challenges Faced & Engineering Solutions", h1_style))
    story.append(Paragraph("• <b>File Integrity under Sudden Termination:</b> Directly serializing to live files risks corruption. Solved via atomic write-and-rename pattern utilizing temporary files and Files.move with StandardCopyOption.ATOMIC_MOVE.", bullet_style))
    story.append(Paragraph("• <b>Thread-Safe Complaint ID Incrementation:</b> Generated sequential IDs (CMP-YYYY-XXXX) across restarts by initializing an AtomicInteger from the maximum existing ID on storage startup.", bullet_style))
    story.append(Paragraph("• <b>Modern Swing Aesthetics without Heavy Dependencies:</b> Default Swing components look dated. Developed a dedicated UITheme token class with custom card borders, table badge renderers, and modern color tokens.", bullet_style))
    story.append(Spacer(1, 10))

    story.append(Paragraph("10. Future Enhancements & References", h1_style))
    story.append(Paragraph("• <b>Automated SMS/Email Alerts:</b> Notification triggers when status advances to IN_PROGRESS or RESOLVED.", bullet_style))
    story.append(Paragraph("• <b>Photo Attachments:</b> Enabling students to attach image evidence to maintenance complaints.", bullet_style))
    story.append(Paragraph("• <b>References:</b> Bloch, Joshua. Effective Java (3rd Ed.); Oracle Java SE 17 Documentation; VITyarthi BYOP Guidelines.", bullet_style))

    doc.build(story)
    print(f"Successfully generated academic project report: {pdf_path}")

if __name__ == "__main__":
    create_report_pdf()
