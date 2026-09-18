package tests;

import exception.ValidationException;
import util.InputValidator;

/**
 * Unit tests for InputValidator.
 */
public class InputValidatorTest {

    public static void runAllTests(TestSuiteRunner.TestCollector runner) {
        testEmailValidationValid(runner);
        testEmailValidationInvalid(runner);
        testPhoneValidationValid(runner);
        testPhoneValidationInvalid(runner);
        testRegistrationNumberValid(runner);
        testRegistrationNumberInvalid(runner);
        testComplaintInputValid(runner);
        testComplaintInputEmptyTitle(runner);
    }

    private static void testEmailValidationValid(TestSuiteRunner.TestCollector runner) {
        String testName = "InputValidator: Valid email passes";
        try {
            InputValidator.validateEmail("atharv.k@vitbhopal.ac.in");
            runner.recordPass(testName, "Validation succeeded without exception", "No exception thrown");
        } catch (Exception e) {
            runner.recordFail(testName, "Expected validation to pass", e.getMessage());
        }
    }

    private static void testEmailValidationInvalid(TestSuiteRunner.TestCollector runner) {
        String testName = "InputValidator: Malformed email throws ValidationException";
        try {
            InputValidator.validateEmail("invalid-email-without-at");
            runner.recordFail(testName, "Expected ValidationException", "No exception was thrown");
        } catch (ValidationException e) {
            runner.recordPass(testName, "ValidationException thrown", e.getMessage());
        } catch (Exception e) {
            runner.recordFail(testName, "Expected ValidationException", "Threw " + e.getClass().getSimpleName());
        }
    }

    private static void testPhoneValidationValid(TestSuiteRunner.TestCollector runner) {
        String testName = "InputValidator: Valid 10-digit Indian mobile passes";
        try {
            InputValidator.validatePhone("9876543210");
            runner.recordPass(testName, "Validation succeeded without exception", "Valid phone accepted");
        } catch (Exception e) {
            runner.recordFail(testName, "Expected validation to pass", e.getMessage());
        }
    }

    private static void testPhoneValidationInvalid(TestSuiteRunner.TestCollector runner) {
        String testName = "InputValidator: 5-digit phone throws ValidationException";
        try {
            InputValidator.validatePhone("12345");
            runner.recordFail(testName, "Expected ValidationException", "No exception was thrown");
        } catch (ValidationException e) {
            runner.recordPass(testName, "ValidationException thrown", e.getMessage());
        } catch (Exception e) {
            runner.recordFail(testName, "Expected ValidationException", "Threw " + e.getClass().getSimpleName());
        }
    }

    private static void testRegistrationNumberValid(TestSuiteRunner.TestCollector runner) {
        String testName = "InputValidator: Valid registration number passes";
        try {
            InputValidator.validateRegistrationNumber("22BCE10234");
            runner.recordPass(testName, "Validation succeeded without exception", "Registration number accepted");
        } catch (Exception e) {
            runner.recordFail(testName, "Expected validation to pass", e.getMessage());
        }
    }

    private static void testRegistrationNumberInvalid(TestSuiteRunner.TestCollector runner) {
        String testName = "InputValidator: Short registration number throws ValidationException";
        try {
            InputValidator.validateRegistrationNumber("12");
            runner.recordFail(testName, "Expected ValidationException", "No exception was thrown");
        } catch (ValidationException e) {
            runner.recordPass(testName, "ValidationException thrown", e.getMessage());
        } catch (Exception e) {
            runner.recordFail(testName, "Expected ValidationException", "Threw " + e.getClass().getSimpleName());
        }
    }

    private static void testComplaintInputValid(TestSuiteRunner.TestCollector runner) {
        String testName = "InputValidator: Valid complaint input passes";
        try {
            InputValidator.validateComplaintInput("Broken window pane", "Corridor window glass shattered due to heavy rain.", "Block 1, 2nd Floor");
            runner.recordPass(testName, "Validation succeeded without exception", "Complaint input accepted");
        } catch (Exception e) {
            runner.recordFail(testName, "Expected validation to pass", e.getMessage());
        }
    }

    private static void testComplaintInputEmptyTitle(TestSuiteRunner.TestCollector runner) {
        String testName = "InputValidator: Empty complaint title throws ValidationException";
        try {
            InputValidator.validateComplaintInput("", "Valid description of issue", "Room 101");
            runner.recordFail(testName, "Expected ValidationException", "No exception was thrown");
        } catch (ValidationException e) {
            runner.recordPass(testName, "ValidationException thrown", e.getMessage());
        } catch (Exception e) {
            runner.recordFail(testName, "Expected ValidationException", "Threw " + e.getClass().getSimpleName());
        }
    }
}
