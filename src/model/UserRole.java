package model;

import java.io.Serializable;

/**
 * Enumeration representing user roles in the Smart Campus Complaint Management System.
 */
public enum UserRole implements Serializable {
    STUDENT("Student", "College Student accessing complaint portal"),
    ADMIN("Administrator", "Campus Authority / Department Administrator");

    private final String displayName;
    private final String description;

    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
