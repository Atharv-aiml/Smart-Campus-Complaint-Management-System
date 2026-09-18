package exception;

/**
 * Base exception class for all application-specific exceptions in the
 * Smart Campus Complaint Management System.
 * Demonstrates Java Custom Exception hierarchy.
 */
public class ComplaintManagementException extends RuntimeException {

    public ComplaintManagementException(String message) {
        super(message);
    }

    public ComplaintManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}
