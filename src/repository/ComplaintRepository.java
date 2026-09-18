package repository;

import model.Complaint;
import model.ComplaintCategory;
import model.ComplaintPriority;
import model.ComplaintStatus;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for managing Complaint entities persisted to complaints.dat.
 * Demonstrates:
 * - Java Streams API for filtering and sorting
 * - Method Overloading for multiple query signatures
 */
public class ComplaintRepository extends FileRepository<Complaint, String> {

    public ComplaintRepository(String filePath) {
        super(filePath);
    }

    /**
     * Retrieves all complaints submitted by a specific student, sorted by priority & date.
     */
    public List<Complaint> findByStudentUsername(String username) {
        if (username == null) return Collections.emptyList();
        String normalized = username.trim().toLowerCase();
        return findAll().stream()
                .filter(c -> c.getStudentUsername().equalsIgnoreCase(normalized))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Retrieves complaints by lifecycle status.
     */
    public List<Complaint> findByStatus(ComplaintStatus status) {
        if (status == null) return findAll();
        return findAll().stream()
                .filter(c -> c.getStatus() == status)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Retrieves complaints by problem category.
     */
    public List<Complaint> findByCategory(ComplaintCategory category) {
        if (category == null) return findAll();
        return findAll().stream()
                .filter(c -> c.getCategory() == category)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Retrieves complaints by priority level.
     */
    public List<Complaint> findByPriority(ComplaintPriority priority) {
        if (priority == null) return findAll();
        return findAll().stream()
                .filter(c -> c.getPriority() == priority)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Full text search across complaint ID, title, description, location, student name, and registration number.
     */
    public List<Complaint> searchByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll().stream().sorted().collect(Collectors.toList());
        }
        String q = keyword.trim().toLowerCase();
        return findAll().stream()
                .filter(c -> (c.getComplaintId() != null && c.getComplaintId().toLowerCase().contains(q)) ||
                             (c.getTitle() != null && c.getTitle().toLowerCase().contains(q)) ||
                             (c.getDescription() != null && c.getDescription().toLowerCase().contains(q)) ||
                             (c.getLocation() != null && c.getLocation().toLowerCase().contains(q)) ||
                             (c.getStudentName() != null && c.getStudentName().toLowerCase().contains(q)) ||
                             (c.getStudentRegNo() != null && c.getStudentRegNo().toLowerCase().contains(q)))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Multi-criteria filtering with search keyword, status, category, and priority filters.
     */
    public List<Complaint> filterComplaints(String keyword, ComplaintStatus status,
                                            ComplaintCategory category, ComplaintPriority priority) {
        return findAll().stream()
                .filter(c -> status == null || c.getStatus() == status)
                .filter(c -> category == null || c.getCategory() == category)
                .filter(c -> priority == null || c.getPriority() == priority)
                .filter(c -> {
                    if (keyword == null || keyword.isBlank()) return true;
                    String q = keyword.trim().toLowerCase();
                    return (c.getComplaintId() != null && c.getComplaintId().toLowerCase().contains(q)) ||
                           (c.getTitle() != null && c.getTitle().toLowerCase().contains(q)) ||
                           (c.getDescription() != null && c.getDescription().toLowerCase().contains(q)) ||
                           (c.getLocation() != null && c.getLocation().toLowerCase().contains(q)) ||
                           (c.getStudentName() != null && c.getStudentName().toLowerCase().contains(q)) ||
                           (c.getStudentRegNo() != null && c.getStudentRegNo().toLowerCase().contains(q));
                })
                .sorted()
                .collect(Collectors.toList());
    }
}
