package exception;

/**
 * Exception thrown when a requested entity cannot be found by its identifier.
 */
public class ResourceNotFoundException extends ComplaintManagementException {

    private final String resourceName;
    private final String identifier;

    public ResourceNotFoundException(String resourceName, String identifier) {
        super(String.format("%s with identifier '%s' was not found.", resourceName, identifier));
        this.resourceName = resourceName;
        this.identifier = identifier;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getIdentifier() {
        return identifier;
    }
}
