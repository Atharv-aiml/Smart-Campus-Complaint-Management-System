package tests;

import model.*;
import repository.AdminRepository;
import repository.ComplaintRepository;
import repository.StudentRepository;
import service.AuthService;
import service.ComplaintService;
import service.ReportService;

import java.io.File;
import java.util.List;

/**
 * End-to-end integration lifecycle test validating complete workflows from
 * registration and submission through administration, resolution, and disk persistence.
 */
public class IntegrationLifecycleTest {

    private static final String TEST_DIR = "test_data_integration" + File.separator;

    public static void runAllTests(TestSuiteRunner.TestCollector runner) {
        testFullLifecycleAndPersistence(runner);
    }

    private static void testFullLifecycleAndPersistence(TestSuiteRunner.TestCollector runner) {
        String testName = "Integration: End-to-end student filing -> admin resolution -> disk reload";
        try {
            File dir = new File(TEST_DIR);
            dir.mkdirs();

            String stuPath = TEST_DIR + "students.dat";
            String admPath = TEST_DIR + "admins.dat";
            String cmpPath = TEST_DIR + "complaints.dat";

            // 1. Initial State
            StudentRepository stuRepo = new StudentRepository(stuPath);
            AdminRepository admRepo = new AdminRepository(admPath);
            ComplaintRepository cmpRepo = new ComplaintRepository(cmpPath);
            stuRepo.clear(); admRepo.clear(); cmpRepo.clear();

            Admin testAdmin = new Admin("ADM-INT", "admin_int", "adm123", "Chief Warden",
                    "warden@vitbhopal.ac.in", "9876543210", "ADM-09", "Hostels", "Warden");
            admRepo.save(testAdmin);

            AuthService auth = new AuthService(stuRepo, admRepo);
            ComplaintService complaintService = new ComplaintService(cmpRepo, stuRepo);
            ReportService reportService = new ReportService(cmpRepo);

            // 2. Student Registration & Login
            Student registered = auth.registerStudent("vikram_s", "pass123", "Vikram Singh",
                    "vikram@vitbhopal.ac.in", "9876543210", "22BCE8888", "SCOPE", "Block 1", "404");
            auth.login("vikram_s", "pass123", UserRole.STUDENT);

            // 3. Complaint Submission
            Complaint ticket = complaintService.submitComplaint(
                    registered, ComplaintCategory.ELECTRICITY, ComplaintPriority.URGENT,
                    "Short circuit in corridor switchboard", "Sparking observed on 4th floor", "Block 1, 4th Floor Corridor"
            );
            String ticketId = ticket.getComplaintId();

            // 4. Admin Assignment & Resolution
            auth.logout();
            auth.login("admin_int", "adm123", UserRole.ADMIN);

            complaintService.assignComplaint(ticketId, "Emergency Electrical Response", "Power isolated to wing");
            Complaint inProgressTicket = complaintService.getComplaintById(ticketId);
            ComplaintStatus statusAfterAssign = inProgressTicket.getStatus();

            complaintService.resolveComplaint(ticketId, "Faulty breaker replaced and insulated. Safe for use.");
            Complaint resolvedTicket = complaintService.getComplaintById(ticketId);

            // 5. Analytics Validation
            ReportService.AnalyticsSummary summary = reportService.generateSummary();

            // 6. Simulate Full Application Restart & Storage Reload
            StudentRepository reloadedStuRepo = new StudentRepository(stuPath);
            ComplaintRepository reloadedCmpRepo = new ComplaintRepository(cmpPath);

            Student reloadedStudent = reloadedStuRepo.findByUsername("vikram_s").orElseThrow();
            Complaint reloadedComplaint = reloadedCmpRepo.findById(ticketId).orElseThrow();

            boolean passed = ticketId.startsWith("CMP-") &&
                    statusAfterAssign == ComplaintStatus.IN_PROGRESS &&
                    resolvedTicket.getStatus() == ComplaintStatus.RESOLVED &&
                    resolvedTicket.getResolvedAt() != null &&
                    summary.totalComplaints() == 1 &&
                    summary.resolvedCount() == 1 &&
                    reloadedStudent.getRegistrationNumber().equals("22BCE8888") &&
                    reloadedComplaint.getStatus() == ComplaintStatus.RESOLVED &&
                    reloadedComplaint.getAssignedTo().equals("Emergency Electrical Response");

            if (passed) {
                runner.recordPass(testName, "Lifecycle & persistence verified across reload",
                        "Ticket " + ticketId + " resolved and reloaded");
            } else {
                runner.recordFail(testName, "Lifecycle assertions failed", "State mismatch");
            }

            // Cleanup
            stuRepo.clear(); admRepo.clear(); cmpRepo.clear();
            dir.delete();
        } catch (Exception e) {
            runner.recordFail(testName, "Integration workflow failed", e.getMessage());
        }
    }
}
