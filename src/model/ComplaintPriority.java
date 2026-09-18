package model;

import java.io.Serializable;

/**
 * Enumeration representing priority levels for campus complaints.
 */
public enum ComplaintPriority implements Serializable {
    LOW("Low", 1, "#10B981", "Routine issue with minimal impact"),
    MEDIUM("Medium", 2, "#3B82F6", "Standard issue affecting daily routine"),
    HIGH("High", 3, "#F59E0B", "Significant disruption requiring prompt attention"),
    URGENT("Urgent", 4, "#EF4444", "Critical emergency requiring immediate intervention");

    private final String displayName;
    private final int level;
    private final String colorHex;
    private final String description;

    ComplaintPriority(String displayName, int level, String colorHex, String description) {
        this.displayName = displayName;
        this.level = level;
        this.colorHex = colorHex;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }

    public String getColorHex() {
        return colorHex;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
