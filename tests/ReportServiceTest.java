package tests;

import model.Complaint;
import model.ComplaintCategory;
import model.ComplaintPriority;
import model.ComplaintStatus;
import repository.ComplaintRepository;
import service.ReportService;

import java.io.File;

/**
 * Unit tests for ReportService analytics and KPI calculations.
 */
public class ReportServiceTest {

    private static final String TEST_FILE = "test_data" + File.separator + "report_test.dat";

    public static void runAllTests(TestSuiteRunner.TestCollector runner) {
        testAnalyticsAggregationAndKPIs(runner);
        testCategoryAndPriorityBreakdown(runner);
    }

    public static void testAnalyticsAggregationAndKPIs(TestSuiteRunner.TestCollector runner) {
        String testName = "ReportService: Accurately calculates status counts and resolution rate";
        try {
            File f = new File(TEST_FILE);
            if (f.exists()) f.delete();

            ComplaintRepository repo = new ComplaintRepository(TEST_FILE);

            // 1 Submitted
            Complaint c1 = new Complaint("CMP-1", "user1", "U1", "REG1", ComplaintCategory.HOSTEL,
                    ComplaintPriority.LOW, "Title 1", "Desc 1", "Loc 1");
            c1.setStatus(ComplaintStatus.SUBMITTED);
            repo.save(c1);

            // 1 In Progress
            Complaint c2 = new Complaint("CMP-2", "user1", "U1", "REG1", ComplaintCategory.ELECTRICITY,
                    ComplaintPriority.HIGH, "Title 2", "Desc 2", "Loc 2");
            c2.setStatus(ComplaintStatus.IN_PROGRESS);
            repo.save(c2);

            // 2 Resolved
            Complaint c3 = new Complaint("CMP-3", "user1", "U1", "REG1", ComplaintCategory.WATER,
                    ComplaintPriority.MEDIUM, "Title 3", "Desc 3", "Loc 3");
            c3.setStatus(ComplaintStatus.RESOLVED);
            repo.save(c3);

            Complaint c4 = new Complaint("CMP-4", "user1", "U1", "REG1", ComplaintCategory.CLASSROOM,
                    ComplaintPriority.LOW, "Title 4", "Desc 4", "Loc 4");
            c4.setStatus(ComplaintStatus.RESOLVED);
            repo.save(c4);

            ReportService reportService = new ReportService(repo);
            ReportService.AnalyticsSummary s = reportService.generateSummary();

            // Total = 4, Submitted = 1, InProgress = 1, Resolved = 2, Resolution Rate = 50.0%
            boolean validCounts = s.totalComplaints() == 4 &&
                    s.submittedCount() == 1 &&
                    s.inProgressCount() == 1 &&
                    s.resolvedCount() == 2 &&
                    s.rejectedCount() == 0;

            boolean validRate = Math.abs(s.resolutionRatePercent() - 50.0) < 0.1;

            if (validCounts && validRate) {
                runner.recordPass(testName, "Total=4, Resolved=2, Rate=50.0%",
                        String.format("Total=%d, Resolved=%d, Rate=%.1f%%",
                                s.totalComplaints(), s.resolvedCount(), s.resolutionRatePercent()));
            } else {
                runner.recordFail(testName, "Expected Total=4, Resolved=2, Rate=50.0%",
                        String.format("Total=%d, Resolved=%d, Rate=%.1f%%",
                                s.totalComplaints(), s.resolvedCount(), s.resolutionRatePercent()));
            }
        } catch (Exception e) {
            runner.recordFail(testName, "Report test failed", e.getMessage());
        }
    }

    public static void testCategoryAndPriorityBreakdown(TestSuiteRunner.TestCollector runner) {
        String testName = "ReportService: Category and priority distribution maps contain all keys";
        try {
            ComplaintRepository repo = new ComplaintRepository(TEST_FILE);
            ReportService reportService = new ReportService(repo);
            ReportService.AnalyticsSummary s = reportService.generateSummary();

            boolean allCatsPresent = s.categoryBreakdown().size() == ComplaintCategory.values().length;
            boolean allPriPresent = s.priorityBreakdown().size() == ComplaintPriority.values().length;

            if (allCatsPresent && allPriPresent) {
                runner.recordPass(testName, "All category and priority keys mapped",
                        "Categories=" + s.categoryBreakdown().size() + ", Priorities=" + s.priorityBreakdown().size());
            } else {
                runner.recordFail(testName, "Missing keys in breakdown maps",
                        "Categories=" + s.categoryBreakdown().size() + ", Priorities=" + s.priorityBreakdown().size());
            }
        } catch (Exception e) {
            runner.recordFail(testName, "Distribution test failed", e.getMessage());
        }
    }
}
