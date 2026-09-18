package util;

import model.*;
import repository.AdminRepository;
import repository.ComplaintRepository;
import repository.StudentRepository;

import java.time.LocalDateTime;

/**
 * Initializes and seeds default demo data if repository storage files are empty.
 * Guarantees that an evaluator can immediately log in and test all features without setup friction.
 */
public final class DataInitializer {

    private DataInitializer() {}

    public static void initializeSampleData(StudentRepository studentRepo,
                                           AdminRepository adminRepo,
                                           ComplaintRepository complaintRepo) {

        // 1. Seed Administrators if empty
        if (adminRepo.count() == 0) {
            Admin chiefAdmin = new Admin(
                    "ADM-1001",
                    "admin",
                    "admin123",
                    "Dr. Rajesh Sharma",
                    "admin@vitbhopal.ac.in",
                    "9876543210",
                    "ADM-001",
                    "Hostel & Campus Estate Office",
                    "Chief Campus Administrator"
            );
            adminRepo.save(chiefAdmin);

            Admin itAdmin = new Admin(
                    "ADM-1002",
                    "netadmin",
                    "admin123",
                    "Er. Vikram Patel",
                    "netadmin@vitbhopal.ac.in",
                    "9876543211",
                    "ADM-002",
                    "IT & Network Operations",
                    "Senior Network Engineer"
            );
            adminRepo.save(itAdmin);
        }

        // 2. Seed Students if empty
        if (studentRepo.count() == 0) {
            Student student1 = new Student(
                    "STU-2001",
                    "atharv",
                    "student123",
                    "Atharv Kulkarni",
                    "atharv.k@vitbhopal.ac.in",
                    "9823012345",
                    "22BCE10234",
                    "SCOPE - Computer Science",
                    "Boys Hostel Block 1",
                    "312"
            );
            studentRepo.save(student1);

            Student student2 = new Student(
                    "STU-2002",
                    "priya",
                    "student123",
                    "Priya Sharma",
                    "priya.s@vitbhopal.ac.in",
                    "9765432109",
                    "22BCE10589",
                    "SCSE - Cyber Security",
                    "Girls Hostel Block A",
                    "204"
            );
            studentRepo.save(student2);

            Student student3 = new Student(
                    "STU-2003",
                    "rahul",
                    "student123",
                    "Rahul Verma",
                    "rahul.v@vitbhopal.ac.in",
                    "9898989898",
                    "21BME10042",
                    "SEEE - Electrical & Electronics",
                    "Boys Hostel Block 2",
                    "108"
            );
            studentRepo.save(student3);
        }

        // 3. Seed Realistic Campus Complaints if empty
        if (complaintRepo.count() == 0) {
            // Complaint 1: In Progress Hostel Issue
            Complaint c1 = new Complaint(
                    "CMP-2026-1001",
                    "atharv",
                    "Atharv Kulkarni",
                    "22BCE10234",
                    ComplaintCategory.HOSTEL,
                    ComplaintPriority.HIGH,
                    "Study table chair broken and window latch loose",
                    "The study chair in room 312 has a broken backrest weld and the window latch on the northern side is rattling during wind.",
                    "Block 1, Room 312"
            );
            c1.setStatus(ComplaintStatus.IN_PROGRESS);
            c1.setAssignedTo("Hostel Carpentry Dept");
            c1.setAdminRemarks("Carpenter assigned (Work Ticket #402). Inspection scheduled for 3:00 PM today.");
            c1.setCreatedAt(LocalDateTime.now().minusHours(28));
            c1.setUpdatedAt(LocalDateTime.now().minusHours(4));
            complaintRepo.save(c1);

            // Complaint 2: Submitted Urgent Water Problem
            Complaint c2 = new Complaint(
                    "CMP-2026-1002",
                    "atharv",
                    "Atharv Kulkarni",
                    "22BCE10234",
                    ComplaintCategory.WATER,
                    ComplaintPriority.URGENT,
                    "Water cooler RO dispenser leak on 3rd floor corridor",
                    "Continuous dripping from the main water purifier outlet causing slippery puddle near the staircase.",
                    "Block 1, 3rd Floor Corridor Water Station"
            );
            c2.setStatus(ComplaintStatus.SUBMITTED);
            c2.setCreatedAt(LocalDateTime.now().minusHours(3));
            c2.setUpdatedAt(LocalDateTime.now().minusHours(3));
            complaintRepo.save(c2);

            // Complaint 3: Resolved Classroom AC
            Complaint c3 = new Complaint(
                    "CMP-2026-1003",
                    "priya",
                    "Priya Sharma",
                    "22BCE10589",
                    ComplaintCategory.CLASSROOM,
                    ComplaintPriority.MEDIUM,
                    "Projector flickering and HDMI cable loose in AB-204",
                    "The ceiling mount projector in AB-204 blinks every 3 minutes. HDMI cable connector is bent.",
                    "Academic Block 1, Room AB-204"
            );
            c3.setStatus(ComplaintStatus.RESOLVED);
            c3.setAssignedTo("AV Tech Support");
            c3.setAdminRemarks("Replaced damaged HDMI cable and cleaned projector air filter. Verified working display.");
            c3.setCreatedAt(LocalDateTime.now().minusDays(3));
            c3.setUpdatedAt(LocalDateTime.now().minusDays(1));
            c3.setResolvedAt(LocalDateTime.now().minusDays(1));
            complaintRepo.save(c3);

            // Complaint 4: In Progress Wi-Fi Issue
            Complaint c4 = new Complaint(
                    "CMP-2026-1004",
                    "priya",
                    "Priya Sharma",
                    "22BCE10589",
                    ComplaintCategory.INTERNET_WIFI,
                    ComplaintPriority.HIGH,
                    "Hostel Wi-Fi AP packet drop and captive portal timeout",
                    "Access point AP-GHA-204 disconnects frequently. Captive portal authentication takes over 60 seconds.",
                    "Girls Hostel Block A, 2nd Floor Wing"
            );
            c4.setStatus(ComplaintStatus.IN_PROGRESS);
            c4.setAssignedTo("Network Team - Mr. Vikram");
            c4.setAdminRemarks("Access point firmware being flashed and channel frequency reassigned to 5GHz.");
            c4.setCreatedAt(LocalDateTime.now().minusHours(18));
            c4.setUpdatedAt(LocalDateTime.now().minusHours(2));
            complaintRepo.save(c4);

            // Complaint 5: Rejected duplicate
            Complaint c5 = new Complaint(
                    "CMP-2026-1005",
                    "rahul",
                    "Rahul Verma",
                    "21BME10042",
                    ComplaintCategory.CLEANLINESS,
                    ComplaintPriority.LOW,
                    "Corridor trash bin full",
                    "Corridor trash bin near elevator is overflowing with cardboard boxes.",
                    "Block 2, 1st Floor"
            );
            c5.setStatus(ComplaintStatus.REJECTED);
            c5.setAssignedTo("Sanitation Staff");
            c5.setAdminRemarks("Duplicate complaint. Area was already cleared by morning housekeeping routine at 9:00 AM.");
            c5.setCreatedAt(LocalDateTime.now().minusDays(2));
            c5.setUpdatedAt(LocalDateTime.now().minusDays(2));
            complaintRepo.save(c5);

            // Complaint 6: Electricity issue
            Complaint c6 = new Complaint(
                    "CMP-2026-1006",
                    "rahul",
                    "Rahul Verma",
                    "21BME10042",
                    ComplaintCategory.ELECTRICITY,
                    ComplaintPriority.HIGH,
                    "Ceiling fan speed regulator sparking in room 108",
                    "The fan regulator switch sparks whenever switched above speed level 2. Potential fire hazard.",
                    "Boys Hostel Block 2, Room 108"
            );
            c6.setStatus(ComplaintStatus.SUBMITTED);
            c6.setCreatedAt(LocalDateTime.now().minusHours(1));
            c6.setUpdatedAt(LocalDateTime.now().minusHours(1));
            complaintRepo.save(c6);
        }
    }
}
