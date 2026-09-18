package exception;

/**
 * Exception thrown when authentication fails due to incorrect credentials
 * or unauthorized role access.
 */
public class AuthenticationException extends ComplaintManagementException {

    public AuthenticationException(String message) {
        super(message);
    }
}
