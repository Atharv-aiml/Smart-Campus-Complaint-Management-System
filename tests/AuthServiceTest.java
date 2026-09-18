package tests;

import exception.AuthenticationException;
import exception.DuplicateResourceException;
import model.Admin;
import model.Student;
import model.User;
import model.UserRole;
import repository.AdminRepository;
import repository.StudentRepository;
import service.AuthService;

import java.io.File;

/**
 * Unit tests for AuthService.
 */
public class AuthServiceTest {

    private static final String TEST_DIR = "test_data" + File.separator;

    public static void runAllTests(TestSuiteRunner.TestCollector runner) {
        testStudentRegistrationSuccess(runner);
        testDuplicateUsernameThrowsException(runner);
        testDuplicateRegistrationThrowsException(runner);
        testStudentLoginSuccess(runner);
        testStudentLoginInvalidPassword(runner);
        testAdminLoginSuccess(runner);
        testLogoutClearsSession(runner);
    }

    private static AuthService createFreshAuthService(String prefix) {
        new File(TEST_DIR).mkdirs();
        StudentRepository studentRepo = new StudentRepository(TEST_DIR + prefix + "_students.dat");
        AdminRepository adminRepo = new AdminRepository(TEST_DIR + prefix + "_admins.dat");
        studentRepo.clear();
        adminRepo.clear();

        // Seed an admin for testing
        Admin admin = new Admin("ADM-TEST", "testadmin", "pass123", "Test Admin",
                "admin@vitbhopal.ac.in", "9876543210", "ADM-01", "Estate", "Officer");
        adminRepo.save(admin);

        return new AuthService(studentRepo, adminRepo);
    }

    private static void testStudentRegistrationSuccess(TestSuiteRunner.TestCollector runner) {
        String testName = "AuthService: Student registration creates new student record";
        try {
            AuthService auth = createFreshAuthService("reg_success");
            Student s = auth.registerStudent("rohit_s", "pass123", "Rohit Sharma",
                    "rohit@vitbhopal.ac.in", "9876543210", "22BCE2001", "SCOPE", "Block 1", "201");

            if (s != null && s.getUsername().equals("rohit_s") && s.getRegistrationNumber().equals("22BCE2001")) {
                runner.recordPass(testName, "Student registered with correct credentials", s.toString());
            } else {
                runner.recordFail(testName, "Student returned with mismatching fields", String.valueOf(s));
            }
        } catch (Exception e) {
            runner.recordFail(testName, "Registration should succeed", e.getMessage());
        }
    }

    private static void testDuplicateUsernameThrowsException(TestSuiteRunner.TestCollector runner) {
        String testName = "AuthService: Duplicate username registration throws DuplicateResourceException";
        try {
            AuthService auth = createFreshAuthService("dup_user");
            auth.registerStudent("user1", "pass123", "User One", "u1@vitbhopal.ac.in", "9876543210", "22BCE1001", "SCOPE", "Block 1", "101");
            auth.registerStudent("user1", "pass456", "User Two", "u2@vitbhopal.ac.in", "9876543211", "22BCE1002", "SCOPE", "Block 1", "102");
            runner.recordFail(testName, "Expected DuplicateResourceException", "Allowed duplicate registration");
        } catch (DuplicateResourceException e) {
            runner.recordPass(testName, "DuplicateResourceException caught", e.getMessage());
        } catch (Exception e) {
            runner.recordFail(testName, "Expected DuplicateResourceException", "Threw " + e.getClass().getSimpleName());
        }
    }

    private static void testDuplicateRegistrationThrowsException(TestSuiteRunner.TestCollector runner) {
        String testName = "AuthService: Duplicate registration number throws DuplicateResourceException";
        try {
            AuthService auth = createFreshAuthService("dup_reg");
            auth.registerStudent("userA", "pass123", "User A", "ua@vitbhopal.ac.in", "9876543210", "22BCE9999", "SCOPE", "Block 1", "101");
            auth.registerStudent("userB", "pass456", "User B", "ub@vitbhopal.ac.in", "9876543211", "22BCE9999", "SCOPE", "Block 1", "102");
            runner.recordFail(testName, "Expected DuplicateResourceException", "Allowed duplicate registration number");
        } catch (DuplicateResourceException e) {
            runner.recordPass(testName, "DuplicateResourceException caught", e.getMessage());
        } catch (Exception e) {
            runner.recordFail(testName, "Expected DuplicateResourceException", "Threw " + e.getClass().getSimpleName());
        }
    }

    private static void testStudentLoginSuccess(TestSuiteRunner.TestCollector runner) {
        String testName = "AuthService: Student login with valid credentials succeeds";
        try {
            AuthService auth = createFreshAuthService("login_success");
            auth.registerStudent("loginuser", "secret123", "Login User", "lu@vitbhopal.ac.in", "9876543210", "22BCE3001", "SCOPE", "Block 1", "101");

            User loggedIn = auth.login("loginuser", "secret123", UserRole.STUDENT);
            if (loggedIn instanceof Student && auth.isAuthenticated() && auth.getCurrentUser().getUsername().equals("loginuser")) {
                runner.recordPass(testName, "Authenticated student session created", loggedIn.getFullName());
            } else {
                runner.recordFail(testName, "Authentication state invalid", String.valueOf(loggedIn));
            }
        } catch (Exception e) {
            runner.recordFail(testName, "Login should succeed", e.getMessage());
        }
    }

    private static void testStudentLoginInvalidPassword(TestSuiteRunner.TestCollector runner) {
        String testName = "AuthService: Student login with bad password throws AuthenticationException";
        try {
            AuthService auth = createFreshAuthService("login_fail");
            auth.registerStudent("userX", "correctpass", "User X", "ux@vitbhopal.ac.in", "9876543210", "22BCE4001", "SCOPE", "Block 1", "101");

            auth.login("userX", "wrongpass", UserRole.STUDENT);
            runner.recordFail(testName, "Expected AuthenticationException", "Allowed login with bad password");
        } catch (AuthenticationException e) {
            runner.recordPass(testName, "AuthenticationException caught", e.getMessage());
        } catch (Exception e) {
            runner.recordFail(testName, "Expected AuthenticationException", "Threw " + e.getClass().getSimpleName());
        }
    }

    private static void testAdminLoginSuccess(TestSuiteRunner.TestCollector runner) {
        String testName = "AuthService: Admin login with valid credentials succeeds";
        try {
            AuthService auth = createFreshAuthService("admin_login");
            User admin = auth.login("testadmin", "pass123", UserRole.ADMIN);
            if (admin instanceof Admin && auth.isAdmin()) {
                runner.recordPass(testName, "Admin session verified", admin.getSummary());
            } else {
                runner.recordFail(testName, "Admin authentication failed", String.valueOf(admin));
            }
        } catch (Exception e) {
            runner.recordFail(testName, "Admin login should succeed", e.getMessage());
        }
    }

    private static void testLogoutClearsSession(TestSuiteRunner.TestCollector runner) {
        String testName = "AuthService: Logout resets session state to null";
        try {
            AuthService auth = createFreshAuthService("logout");
            auth.login("testadmin", "pass123", UserRole.ADMIN);
            auth.logout();

            if (!auth.isAuthenticated() && auth.getCurrentUser() == null) {
                runner.recordPass(testName, "Current user session cleared", "isAuthenticated = false");
            } else {
                runner.recordFail(testName, "Session should be empty", "Session not cleared");
            }
        } catch (Exception e) {
            runner.recordFail(testName, "Logout failed", e.getMessage());
        }
    }
}
