package tests;

import java.util.ArrayList;
import java.util.List;

/**
 * Standalone Automated Test Runner for the Smart Campus Complaint Management System.
 * Executes all unit test suites without requiring external maven/junit binaries.
 * Outputs detailed test cases, expected results, actual results, and Pass/Fail status.
 */
public class TestSuiteRunner {

    public static class TestResult {
        public final String testName;
        public final String expected;
        public final String actual;
        public final boolean passed;
        public final long durationMs;

        public TestResult(String testName, String expected, String actual, boolean passed, long durationMs) {
            this.testName = testName;
            this.expected = expected;
            this.actual = actual;
            this.passed = passed;
            this.durationMs = durationMs;
        }
    }

    public static class TestCollector {
        private final List<TestResult> results = new ArrayList<>();
        private long startTime = System.currentTimeMillis();

        public void recordPass(String testName, String expected, String actual) {
            long duration = System.currentTimeMillis() - startTime;
            results.add(new TestResult(testName, expected, actual, true, duration));
            startTime = System.currentTimeMillis();
        }

        public void recordFail(String testName, String expected, String actual) {
            long duration = System.currentTimeMillis() - startTime;
            results.add(new TestResult(testName, expected, actual, false, duration));
            startTime = System.currentTimeMillis();
        }

        public List<TestResult> getResults() {
            return results;
        }
    }

    public static void main(String[] args) {
        System.out.println("=========================================================================================");
        System.out.println("       SMART CAMPUS COMPLAINT MANAGEMENT SYSTEM - AUTOMATED UNIT TEST SUITE              ");
        System.out.println("=========================================================================================");
        System.out.println("Running test suites across all core modules...\n");

        TestCollector collector = new TestCollector();
        long suiteStart = System.currentTimeMillis();

        // 1. InputValidator Tests
        System.out.println("--> Executing InputValidatorTest...");
        InputValidatorTest.runAllTests(collector);

        // 2. AuthService Tests
        System.out.println("--> Executing AuthServiceTest...");
        AuthServiceTest.runAllTests(collector);

        // 3. ComplaintService Tests
        System.out.println("--> Executing ComplaintServiceTest...");
        ComplaintServiceTest.runAllTests(collector);

        // 4. FileRepository Persistence Tests
        System.out.println("--> Executing FileRepositoryTest...");
        FileRepositoryTest.runAllTests(collector);

        // 5. ReportService Analytics Tests
        System.out.println("--> Executing ReportServiceTest...");
        ReportServiceTest.runAllTests(collector);

        // 6. Integration Lifecycle Tests
        System.out.println("--> Executing IntegrationLifecycleTest...");
        IntegrationLifecycleTest.runAllTests(collector);

        long totalDuration = System.currentTimeMillis() - suiteStart;

        // Print Formatted Results Table
        System.out.println("\n" + "=".repeat(120));
        System.out.printf("%-4s | %-45s | %-32s | %-24s | %-8s%n",
                "#", "TEST CASE DESCRIPTION", "EXPECTED RESULT", "ACTUAL RESULT", "STATUS");
        System.out.println("-".repeat(120));

        int passedCount = 0;
        int index = 1;

        for (TestResult r : collector.getResults()) {
            String statusStr = r.passed ? "[PASS]" : "[FAIL]";
            if (r.passed) passedCount++;

            String shortExpected = truncate(r.expected, 32);
            String shortActual = truncate(r.actual, 24);
            String shortName = truncate(r.testName, 45);

            System.out.printf("%-4d | %-45s | %-32s | %-24s | %-8s%n",
                    index++, shortName, shortExpected, shortActual, statusStr);
        }

        System.out.println("=".repeat(120));
        int totalTests = collector.getResults().size();
        int failedCount = totalTests - passedCount;
        double passRate = totalTests > 0 ? ((double) passedCount / totalTests) * 100.0 : 0.0;

        System.out.printf("TEST RUN SUMMARY: %d Total | %d Passed | %d Failed | Pass Rate: %.1f%% | Time: %d ms%n",
                totalTests, passedCount, failedCount, passRate, totalDuration);
        System.out.println("=".repeat(120));

        // Cleanup test data directory
        try {
            deleteDirectory(new java.io.File("test_data"));
        } catch (Exception ignored) {}

        if (failedCount > 0) {
            System.err.println("FAILURE: One or more unit tests failed!");
            System.exit(1);
        } else {
            System.out.println("SUCCESS: All unit tests passed without any errors!");
            System.exit(0);
        }
    }

    private static String truncate(String s, int maxLen) {
        if (s == null) return "null";
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen - 3) + "...";
    }

    private static void deleteDirectory(java.io.File file) {
        if (file.isDirectory()) {
            java.io.File[] entries = file.listFiles();
            if (entries != null) {
                for (java.io.File child : entries) {
                    deleteDirectory(child);
                }
            }
        }
        file.delete();
    }
}
