package model;

import java.io.Serializable;

/**
 * Enumeration representing the lifecycle states of a complaint.
 */
public enum ComplaintStatus implements Serializable {
    SUBMITTED("Submitted", "#64748B", "Complaint has been filed and is awaiting review"),
    IN_PROGRESS("In Progress", "#F59E0B", "Complaint has been assigned and is under active resolution"),
    RESOLVED("Resolved", "#10B981", "Issue has been addressed and successfully resolved"),
    REJECTED("Rejected", "#EF4444", "Complaint was invalidated, duplicate, or declined with remarks");

    private final String displayName;
    private final String badgeColorHex;
    private final String description;

    ComplaintStatus(String displayName, String badgeColorHex, String description) {
        this.displayName = displayName;
        this.badgeColorHex = badgeColorHex;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeColorHex() {
        return badgeColorHex;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Checks if transitioning from this status to the target status is allowed.
     */
    public boolean canTransitionTo(ComplaintStatus target) {
        if (this == target) {
            return true;
        }
        return switch (this) {
            case SUBMITTED -> target == IN_PROGRESS || target == RESOLVED || target == REJECTED;
            case IN_PROGRESS -> target == RESOLVED || target == REJECTED;
            case RESOLVED -> target == IN_PROGRESS; // Allow reopening if needed
            case REJECTED -> target == SUBMITTED || target == IN_PROGRESS; // Allow review upon appeal
        };
    }

    @Override
    public String toString() {
        return displayName;
    }
}
