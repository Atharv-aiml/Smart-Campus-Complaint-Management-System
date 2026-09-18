package exception;

/**
 * Exception thrown when attempting to register or create an entity that already exists
 * (e.g. duplicate username or registration number).
 */
public class DuplicateResourceException extends ComplaintManagementException {

    private final String resourceName;
    private final String key;

    public DuplicateResourceException(String resourceName, String key) {
        super(String.format("%s already exists with key: %s", resourceName, key));
        this.resourceName = resourceName;
        this.key = key;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getKey() {
        return key;
    }
}
