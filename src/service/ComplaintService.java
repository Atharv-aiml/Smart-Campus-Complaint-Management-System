package service;

import exception.ResourceNotFoundException;
import exception.ValidationException;
import model.*;
import repository.ComplaintRepository;
import repository.StudentRepository;
import util.InputValidator;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service orchestrating complaint submissions, status workflows, assignments, and queries.
 * Demonstrates:
 * - Method Overloading
 * - State transition enforcement
 * - Java Streams API
 * - Thread-safe atomic ID generation
 */
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final StudentRepository studentRepository;
    private final AtomicInteger sequenceCounter;
    private static final Pattern ID_PATTERN = Pattern.compile("^CMP-\\d{4}-(\\d{4,})$");

    public ComplaintService(ComplaintRepository complaintRepository, StudentRepository studentRepository) {
        this.complaintRepository = Objects.requireNonNull(complaintRepository);
        this.studentRepository = Objects.requireNonNull(studentRepository);
        this.sequenceCounter = new AtomicInteger(initSequence());
    }

    private int initSequence() {
        int max = 1000;
        for (Complaint c : complaintRepository.findAll()) {
            Matcher m = ID_PATTERN.matcher(c.getComplaintId());
            if (m.matches()) {
                try {
                    int val = Integer.parseInt(m.group(1));
                    if (val > max) {
                        max = val;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return max;
    }

    /**
     * Generates an atomic, unique complaint ID in format: CMP-YYYY-XXXX.
     */
    public synchronized String generateComplaintId() {
        int year = LocalDate.now().getYear();
        int nextNum = sequenceCounter.incrementAndGet();
        return String.format("CMP-%d-%04d", year, nextNum);
    }

    /**
     * Submits a new complaint on behalf of an authenticated student.
     */
    public Complaint submitComplaint(Student student, ComplaintCategory category, ComplaintPriority priority,
                                     String title, String description, String location) {
        if (student == null) {
            throw new ValidationException("Student", "Valid student session required to file a complaint.");
        }
        InputValidator.validateComplaintInput(title, description, location);
        Objects.requireNonNull(category, "Category cannot be null");
        Objects.requireNonNull(priority, "Priority cannot be null");

        String complaintId = generateComplaintId();
        Complaint complaint = new Complaint(
                complaintId,
                student.getUsername(),
                student.getFullName(),
                student.getRegistrationNumber(),
                category,
                priority,
                title.trim(),
                description.trim(),
                location.trim()
        );

        return complaintRepository.save(complaint);
    }

    /**
     * Updates complaint status with validation of permitted transitions.
     */
    public Complaint updateStatus(String complaintId, ComplaintStatus newStatus, String adminRemarks) {
        Complaint complaint = getComplaintById(complaintId);
        if (newStatus == null) {
            throw new ValidationException("Status", "New status cannot be null.");
        }

        if (!complaint.getStatus().canTransitionTo(newStatus)) {
            throw new ValidationException("Status",
                    String.format("Invalid status transition from '%s' to '%s'.",
                            complaint.getStatus(), newStatus));
        }

        complaint.setStatus(newStatus);
        if (adminRemarks != null && !adminRemarks.isBlank()) {
            complaint.setAdminRemarks(adminRemarks.trim());
        }
        return complaintRepository.save(complaint);
    }

    /**
     * Assigns complaint to a specific campus department, officer, or supervisor.
     */
    public Complaint assignComplaint(String complaintId, String assignedTo, String remarks) {
        Complaint complaint = getComplaintById(complaintId);
        if (assignedTo == null || assignedTo.isBlank()) {
            throw new ValidationException("AssignedTo", "Assignee name/department cannot be empty.");
        }
        complaint.setAssignedTo(assignedTo.trim());
        if (complaint.getStatus() == ComplaintStatus.SUBMITTED) {
            complaint.setStatus(ComplaintStatus.IN_PROGRESS);
        }
        if (remarks != null && !remarks.isBlank()) {
            complaint.setAdminRemarks(remarks.trim());
        }
        return complaintRepository.save(complaint);
    }

    /**
     * Adds administrator remarks to a complaint ticket.
     */
    public Complaint addRemarks(String complaintId, String remarks) {
        Complaint complaint = getComplaintById(complaintId);
        if (remarks == null || remarks.isBlank()) {
            throw new ValidationException("Remarks", "Remarks cannot be empty.");
        }
        complaint.setAdminRemarks(remarks.trim());
        return complaintRepository.save(complaint);
    }

    /**
     * Resolves a complaint ticket directly.
     */
    public Complaint resolveComplaint(String complaintId, String resolutionNotes) {
        Complaint complaint = getComplaintById(complaintId);
        complaint.setStatus(ComplaintStatus.RESOLVED);
        if (resolutionNotes != null && !resolutionNotes.isBlank()) {
            complaint.setAdminRemarks(resolutionNotes.trim());
        }
        return complaintRepository.save(complaint);
    }

    /**
     * Retrieves a complaint by ID or throws ResourceNotFoundException.
     */
    public Complaint getComplaintById(String complaintId) {
        if (complaintId == null || complaintId.isBlank()) {
            throw new ValidationException("Complaint ID", "Complaint ID cannot be empty.");
        }
        return complaintRepository.findById(complaintId.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", complaintId));
    }

    /**
     * Retrieves the student entity associated with a complaint.
     */
    public Optional<Student> getStudentForComplaint(Complaint complaint) {
        if (complaint == null || complaint.getStudentUsername() == null) {
            return Optional.empty();
        }
        return studentRepository.findByUsername(complaint.getStudentUsername());
    }

    // ==========================================
    // METHOD OVERLOADING DEMONSTRATION: Search & Queries
    // ==========================================

    /**
     * Overload 1: Retrieves all complaints in system.
     */
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll().stream().sorted().collect(Collectors.toList());
    }

    /**
     * Overload 2: Keyword search across all fields.
     */
    public List<Complaint> searchComplaints(String keyword) {
        return complaintRepository.searchByKeyword(keyword);
    }

    /**
     * Overload 3: Filter by status only.
     */
    public List<Complaint> searchComplaints(ComplaintStatus status) {
        return complaintRepository.findByStatus(status);
    }

    /**
     * Overload 4: Multi-criteria filter with keyword, status, category, priority.
     */
    public List<Complaint> searchComplaints(String keyword, ComplaintStatus status,
                                           ComplaintCategory category, ComplaintPriority priority) {
        return complaintRepository.filterComplaints(keyword, status, category, priority);
    }

    /**
     * Overload 5: Student complaints (all).
     */
    public List<Complaint> getStudentComplaints(String studentUsername) {
        return complaintRepository.findByStudentUsername(studentUsername);
    }

    /**
     * Overload 6: Student complaints filtered by status.
     */
    public List<Complaint> getStudentComplaints(String studentUsername, ComplaintStatus status) {
        if (studentUsername == null) return Collections.emptyList();
        return complaintRepository.findByStudentUsername(studentUsername).stream()
                .filter(c -> status == null || c.getStatus() == status)
                .sorted()
                .collect(Collectors.toList());
    }
}
