package model;

import java.io.Serializable;

/**
 * Enumeration representing categories of complaints on campus.
 */
public enum ComplaintCategory implements Serializable {
    HOSTEL("Hostel", "Issues related to hostel rooms, furniture, corridors, or facilities", "HST"),
    ELECTRICITY("Electricity", "Power outages, faulty wiring, malfunctioning fans, lights, or switches", "ELE"),
    WATER("Water", "Water shortages, leakage, RO purifier issues, or plumbing failures", "WTR"),
    CLASSROOM("Classroom", "Projectors, podiums, ACs, microphones, or classroom benches", "CLS"),
    INTERNET_WIFI("Internet / Wi-Fi", "Wi-Fi connectivity, portal authentication, or network drops", "NET"),
    CLEANLINESS("Cleanliness", "Waste disposal, washroom sanitization, or campus hygiene", "CLN"),
    OTHER("Other", "Miscellaneous campus issues not covered by standard categories", "OTH");

    private final String displayName;
    private final String description;
    private final String prefix;

    ComplaintCategory(String displayName, String description, String prefix) {
        this.displayName = displayName;
        this.description = description;
        this.prefix = prefix;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
