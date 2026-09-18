package model;

import repository.Identifiable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Core domain entity representing a student campus complaint.
 * Demonstrates:
 * - Encapsulation (validated fields, lifecycle state checks)
 * - Interfaces (implements Identifiable, Comparable, Serializable)
 * - Modern Java Date and Time API (LocalDateTime)
 */
public class Complaint implements Identifiable<String>, Comparable<Complaint>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String complaintId;
    private String studentUsername;
    private String studentName;
    private String studentRegNo;
    private ComplaintCategory category;
    private ComplaintPriority priority;
    private String title;
    private String description;
    private String location;
    private ComplaintStatus status;
    private String assignedTo;
    private String adminRemarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    /**
     * Default constructor for serialization support.
     */
    public Complaint() {
        this.status = ComplaintStatus.SUBMITTED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.assignedTo = "Unassigned";
        this.adminRemarks = "Awaiting review";
    }

    /**
     * Parameterized constructor for new complaint creation.
     */
    public Complaint(String complaintId, String studentUsername, String studentName,
                     String studentRegNo, ComplaintCategory category, ComplaintPriority priority,
                     String title, String description, String location) {
        this.complaintId = Objects.requireNonNull(complaintId, "Complaint ID cannot be null").trim();
        this.studentUsername = Objects.requireNonNull(studentUsername, "Student username cannot be null").trim();
        this.studentName = Objects.requireNonNull(studentName, "Student name cannot be null").trim();
        this.studentRegNo = Objects.requireNonNull(studentRegNo, "Student reg number cannot be null").trim();
        this.category = Objects.requireNonNull(category, "Category cannot be null");
        this.priority = Objects.requireNonNull(priority, "Priority cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null").trim();
        this.description = Objects.requireNonNull(description, "Description cannot be null").trim();
        this.location = location != null ? location.trim() : "Campus";
        this.status = ComplaintStatus.SUBMITTED;
        this.assignedTo = "Unassigned";
        this.adminRemarks = "Ticket filed successfully. Pending administrator review.";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.resolvedAt = null;
    }

    @Override
    public String getId() {
        return complaintId;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getStudentUsername() {
        return studentUsername;
    }

    public void setStudentUsername(String studentUsername) {
        this.studentUsername = studentUsername;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentRegNo() {
        return studentRegNo;
    }

    public void setStudentRegNo(String studentRegNo) {
        this.studentRegNo = studentRegNo;
    }

    public ComplaintCategory getCategory() {
        return category;
    }

    public void setCategory(ComplaintCategory category) {
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }

    public ComplaintPriority getPriority() {
        return priority;
    }

    public void setPriority(ComplaintPriority priority) {
        this.priority = priority;
        this.updatedAt = LocalDateTime.now();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
        this.updatedAt = LocalDateTime.now();
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        if (status == ComplaintStatus.RESOLVED) {
            this.resolvedAt = LocalDateTime.now();
        } else if (status != ComplaintStatus.RESOLVED) {
            this.resolvedAt = null;
        }
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo != null && !assignedTo.isBlank() ? assignedTo.trim() : "Unassigned";
        this.updatedAt = LocalDateTime.now();
    }

    public String getAdminRemarks() {
        return adminRemarks;
    }

    public void setAdminRemarks(String adminRemarks) {
        this.adminRemarks = adminRemarks != null ? adminRemarks.trim() : "";
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    /**
     * Checks if this complaint has reached a closed resolution state.
     */
    public boolean isClosed() {
        return status == ComplaintStatus.RESOLVED || status == ComplaintStatus.REJECTED;
    }

    /**
     * Ordering: Most urgent priority first; if equal, most recently created first.
     */
    @Override
    public int compareTo(Complaint o) {
        if (o == null) return -1;
        int priorityDiff = Integer.compare(o.getPriority().getLevel(), this.getPriority().getLevel());
        if (priorityDiff != 0) {
            return priorityDiff;
        }
        return o.getCreatedAt().compareTo(this.getCreatedAt());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Complaint complaint = (Complaint) o;
        return Objects.equals(complaintId, complaint.complaintId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(complaintId);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %s by %s [%s]",
                complaintId, title, category, status, studentName, priority);
    }
}
