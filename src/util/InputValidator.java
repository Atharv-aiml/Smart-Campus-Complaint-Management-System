package util;

import exception.ValidationException;

import java.util.regex.Pattern;

/**
 * Utility class for validating user input fields and data integrity.
 * Demonstrates:
 * - Regular Expressions
 * - Custom exception throwing with field metadata
 */
public final class InputValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[6-9]\\d{9}$"
    );

    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_]{3,20}$"
    );

    private static final Pattern REG_NO_PATTERN = Pattern.compile(
            "^[0-9]{2}[A-Za-z]{3}[0-9]{4,5}$"
    );

    private InputValidator() {
        // Prevent instantiation of utility class
    }

    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName, fieldName + " cannot be empty.");
        }
    }

    public static void validateMinLength(String value, int minLength, String fieldName) {
        validateNotEmpty(value, fieldName);
        if (value.trim().length() < minLength) {
            throw new ValidationException(fieldName,
                    String.format("%s must be at least %d characters long.", fieldName, minLength));
        }
    }

    public static void validateMaxLength(String value, int maxLength, String fieldName) {
        if (value != null && value.trim().length() > maxLength) {
            throw new ValidationException(fieldName,
                    String.format("%s cannot exceed %d characters.", fieldName, maxLength));
        }
    }

    public static void validateUsername(String username) {
        validateNotEmpty(username, "Username");
        if (!USERNAME_PATTERN.matcher(username.trim()).matches()) {
            throw new ValidationException("Username",
                    "Username must be 3-20 characters long and contain only letters, numbers, or underscores.");
        }
    }

    public static void validatePassword(String password) {
        validateNotEmpty(password, "Password");
        if (password.length() < 6) {
            throw new ValidationException("Password", "Password must be at least 6 characters long.");
        }
    }

    public static void validateEmail(String email) {
        validateNotEmpty(email, "Email");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Email", "Please provide a valid email address (e.g., student@vitbhopal.ac.in).");
        }
    }

    public static void validatePhone(String phone) {
        validateNotEmpty(phone, "Phone");
        String sanitized = phone.replaceAll("[\\s\\-+]", "");
        if (sanitized.startsWith("91") && sanitized.length() == 12) {
            sanitized = sanitized.substring(2);
        }
        if (!PHONE_PATTERN.matcher(sanitized).matches()) {
            throw new ValidationException("Phone", "Please enter a valid 10-digit mobile number starting with 6-9.");
        }
    }

    public static void validateRegistrationNumber(String regNo) {
        validateNotEmpty(regNo, "Registration Number");
        String trimmed = regNo.trim().toUpperCase();
        if (trimmed.length() < 6 || trimmed.length() > 12) {
            throw new ValidationException("Registration Number",
                    "Registration number should be 6-12 characters (e.g. 22BCE10001).");
        }
    }

    public static void validateComplaintInput(String title, String description, String location) {
        validateMinLength(title, 5, "Complaint Title");
        validateMaxLength(title, 100, "Complaint Title");
        validateMinLength(description, 10, "Complaint Description");
        validateMaxLength(description, 1000, "Complaint Description");
        validateNotEmpty(location, "Location / Room");
    }
}
