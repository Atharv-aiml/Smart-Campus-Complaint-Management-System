package util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for formatting and manipulating date-time objects.
 * Demonstrates Java 8+ Date and Time API.
 */
public final class DateTimeUtil {

    public static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public static final DateTimeFormatter SHORT_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static final DateTimeFormatter FILE_SAFE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private DateTimeUtil() {
        // Prevent instantiation
    }

    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DISPLAY_FORMAT);
    }

    public static String formatShort(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(SHORT_FORMAT);
    }

    /**
     * Returns a humanized relative time string (e.g., "5 mins ago", "2 hours ago", "Yesterday").
     */
    public static String getRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);
        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return "Just now";
        } else if (seconds < 3600) {
            long mins = seconds / 60;
            return mins + (mins == 1 ? " min ago" : " mins ago");
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        } else if (seconds < 172800) {
            return "Yesterday";
        } else {
            long days = seconds / 86400;
            return days + (days == 1 ? " day ago" : " days ago");
        }
    }
}
