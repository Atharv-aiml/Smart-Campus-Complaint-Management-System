package tests;

import exception.ValidationException;
import model.*;
import repository.ComplaintRepository;
import repository.StudentRepository;
import service.ComplaintService;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

/**
 * Unit tests for ComplaintService.
 */
public class ComplaintServiceTest {

    private static final String TEST_DIR = "test_data" + File.separator;

    public static void runAllTests(TestSuiteRunner.TestCollector runner) {
        testComplaintSubmissionAndIdGeneration(runner);
        testSequentialIdIncrement(runner);
        testStatusTransitionValid(runner);
        testStatusTransitionInvalid(runner);
        testComplaintAssignment(runner);
        testComplaintResolution(runner);
        testKeywordSearch(runner);
        testMultiCriteriaFilter(runner);
    }

    private static ComplaintService createFreshService(String prefix) {
        new File(TEST_DIR).mkdirs();
        StudentRepository studentRepo = new StudentRepository(TEST_DIR + prefix + "_students.dat");
        ComplaintRepository complaintRepo = new ComplaintRepository(TEST_DIR + prefix + "_complaints.dat");
        studentRepo.clear();
        complaintRepo.clear();
        return new ComplaintService(complaintRepo, studentRepo);
    }

    private static Student createDummyStudent() {
        return new Student("STU-1", "atharv", "pass", "Atharv K", "a@vitbhopal.ac.in",
                "9876543210", "22BCE1001", "SCOPE", "Block 1", "312");
    }

    private static void testComplaintSubmissionAndIdGeneration(TestSuiteRunner.TestCollector runner) {
        String testName = "ComplaintService: Complaint submission generates unique CMP-YYYY-XXXX ID";
        try {
            ComplaintService service = createFreshService("submit_test");
            Student s = createDummyStudent();

            Complaint c = service.submitComplaint(s, ComplaintCategory.WATER, ComplaintPriority.HIGH,
                    "Water leakage in washroom", "Continuous dripping from pipeline", "Block 1 3rd Floor");

            String expectedPrefix = "CMP-" + LocalDate.now().getYear() + "-";
            if (c.getComplaintId().startsWith(expectedPrefix) && c.getStatus() == ComplaintStatus.SUBMITTED) {
                runner.recordPass(testName, "Valid ID format starting with " + expectedPrefix, c.getComplaintId());
            } else {
                runner.recordFail(testName, "Invalid complaint ID format", c.getComplaintId());
            }
        } catch (Exception e) {
            runner.recordFail(testName, "Submission failed", e.getMessage());
        }
    }

    private static void testSequentialIdIncrement(TestSuiteRunner.TestCollector runner) {
        String testName = "ComplaintService: Subsequent complaints increment sequential ID counter";
        try {
            ComplaintService service = createFreshService("seq_test");
            Student s = createDummyStudent();

            Complaint c1 = service.submitComplaint(s, ComplaintCategory.HOSTEL, ComplaintPriority.LOW,
                    "Bed frame squeaking", "Bed frame bolts are loose", "Room 312");
            Complaint c2 = service.submitComplaint(s, ComplaintCategory.ELECTRICITY, ComplaintPriority.MEDIUM,
                    "Switch board spark", "Loose wire in switch", "Room 312");

            int year = LocalDate.now().getYear();
            String id1 = c1.getComplaintId();
            String id2 = c2.getComplaintId();

            if (!id1.equals(id2) && id1.startsWith("CMP-" + year) && id2.startsWith("CMP-" + year)) {
                runner.recordPass(testName, "Distinct sequential IDs generated", id1 + " -> " + id2);
            } else {
                runner.recordFail(testName, "IDs not sequential or unique", id1 + ", " + id2);
            }
        } catch (Exception e) {
            runner.recordFail(testName, "Sequence generation failed", e.getMessage());
        }
    }

    private static void testStatusTransitionValid(TestSuiteRunner.TestCollector runner) {
        String testName = "ComplaintService: Valid status transition SUBMITTED -> IN_PROGRESS succeeds";
        try {
            ComplaintService service = createFreshService("valid_trans");
            Student s = createDummyStudent();
            Complaint c = service.submitComplaint(s, ComplaintCategory.CLASSROOM, ComplaintPriority.LOW,
                    "Whiteboard marker dry", "No working markers", "Room 201");

            Complaint updated = service.updateStatus(c.getComplaintId(), ComplaintStatus.IN_PROGRESS, "Work in progress");
            if (updated.getStatus() == ComplaintStatus.IN_PROGRESS) {
                runner.recordPass(testName, "Status updated to IN_PROGRESS", updated.getStatus().toString());
            } else {
                runner.recordFail(testName, "Status update failed", updated.getStatus().toString());
            }
        } catch (Exception e) {
            runner.recordFail(testName, "Valid transition failed", e.getMessage());
        }
    }

    private static void testStatusTransitionInvalid(TestSuiteRunner.TestCollector runner) {
        String testName = "ComplaintService: Invalid transition RESOLVED -> REJECTED throws ValidationException";
        try {
            ComplaintService service = createFreshService("invalid_trans");
            Student s = createDummyStudent();
            Complaint c = service.submitComplaint(s, ComplaintCategory.CLASSROOM, ComplaintPriority.LOW,
                    "Projector remote battery", "Remote battery dead", "Room 202");

            service.updateStatus(c.getComplaintId(), ComplaintStatus.RESOLVED, "Done");
            service.updateStatus(c.getComplaintId(), ComplaintStatus.REJECTED, "Invalid attempt");
            runner.recordFail(testName, "Expected ValidationException", "Allowed invalid transition");
        } catch (ValidationException e) {
            runner.recordPass(testName, "ValidationException caught for illegal transition", e.getMessage());
        } catch (Exception e) {
            runner.recordFail(testName, "Expected ValidationException", "Threw " + e.getClass().getSimpleName());
        }
    }

    private static void testComplaintAssignment(TestSuiteRunner.TestCollector runner) {
        String testName = "ComplaintService: Assignment sets staff and updates status to IN_PROGRESS";
        try {
            ComplaintService service = createFreshService("assign_test");
            Student s = createDummyStudent();
            Complaint c = service.submitComplaint(s, ComplaintCategory.ELECTRICITY, ComplaintPriority.URGENT,
                    "Power tripped in wing", "Entire 3rd floor wing has no power", "Block 1 3rd Floor");

            Complaint assigned = service.assignComplaint(c.getComplaintId(), "Chief Electrician - Mr. Gupta", "Dispatched team");
            if (assigned.getAssignedTo().equals("Chief Electrician - Mr. Gupta") &&
                    assigned.getStatus() == ComplaintStatus.IN_PROGRESS) {
                runner.recordPass(testName, "Assigned to staff and moved to IN_PROGRESS", assigned.getAssignedTo());
            } else {
                runner.recordFail(testName, "Assignment failed", assigned.toString());
            }
        } catch (Exception e) {
            runner.recordFail(testName, "Assignment failed", e.getMessage());
        }
    }

    private static void testComplaintResolution(TestSuiteRunner.TestCollector runner) {
        String testName = "ComplaintService: Resolving complaint sets RESOLVED and populates resolvedAt timestamp";
        try {
            ComplaintService service = createFreshService("resolve_test");
            Student s = createDummyStudent();
            Complaint c = service.submitComplaint(s, ComplaintCategory.CLEANLINESS, ComplaintPriority.LOW,
                    "Dustbin cleared", "Trash bin needed clearing", "Corridor");

            Complaint resolved = service.resolveComplaint(c.getComplaintId(), "Sanitation completed successfully");
            if (resolved.getStatus() == ComplaintStatus.RESOLVED && resolved.getResolvedAt() != null) {
                runner.recordPass(testName, "Resolved with timestamp", resolved.getResolvedAt().toString());
            } else {
                runner.recordFail(testName, "Resolution failed", resolved.toString());
            }
        } catch (Exception e) {
            runner.recordFail(testName, "Resolution failed", e.getMessage());
        }
    }

    private static void testKeywordSearch(TestSuiteRunner.TestCollector runner) {
        String testName = "ComplaintService: Search finds complaints by partial title or keyword";
        try {
            ComplaintService service = createFreshService("search_test");
            Student s = createDummyStudent();

            service.submitComplaint(s, ComplaintCategory.INTERNET_WIFI, ComplaintPriority.HIGH,
                    "Wi-Fi signal drop in block 1", "Router disconnects every 10 minutes", "Block 1");
            service.submitComplaint(s, ComplaintCategory.WATER, ComplaintPriority.MEDIUM,
                    "RO water filter clogged", "Low flow rate", "Mess");

            List<Complaint> results = service.searchComplaints("Wi-Fi");
            if (results.size() == 1 && results.get(0).getTitle().contains("Wi-Fi")) {
                runner.recordPass(testName, "Exact keyword match found", results.get(0).getTitle());
            } else {
                runner.recordFail(testName, "Search did not return expected match", "Found " + results.size());
            }
        } catch (Exception e) {
            runner.recordFail(testName, "Search failed", e.getMessage());
        }
    }

    private static void testMultiCriteriaFilter(TestSuiteRunner.TestCollector runner) {
        String testName = "ComplaintService: Multi-criteria filter filters by category, priority, and status";
        try {
            ComplaintService service = createFreshService("multi_filter");
            Student s = createDummyStudent();

            service.submitComplaint(s, ComplaintCategory.HOSTEL, ComplaintPriority.URGENT,
                    "Door lock stuck", "Unable to lock room", "Block 1");
            service.submitComplaint(s, ComplaintCategory.HOSTEL, ComplaintPriority.LOW,
                    "Curtain rod loose", "Curtain rod came off", "Block 1");

            List<Complaint> results = service.searchComplaints(
                    null, ComplaintStatus.SUBMITTED, ComplaintCategory.HOSTEL, ComplaintPriority.URGENT);

            if (results.size() == 1 && results.get(0).getTitle().equals("Door lock stuck")) {
                runner.recordPass(testName, "Filtered correctly to single matching urgent hostel complaint", results.get(0).getTitle());
            } else {
                runner.recordFail(testName, "Filter returned incorrect count", "Found " + results.size());
            }
        } catch (Exception e) {
            runner.recordFail(testName, "Multi filter failed", e.getMessage());
        }
    }
}
