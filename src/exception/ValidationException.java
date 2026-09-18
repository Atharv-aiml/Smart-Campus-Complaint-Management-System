package exception;

/**
 * Exception thrown when user input violates validation constraints.
 */
public class ValidationException extends ComplaintManagementException {

    private final String fieldName;

    public ValidationException(String message) {
        super(message);
        this.fieldName = "general";
    }

    public ValidationException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
