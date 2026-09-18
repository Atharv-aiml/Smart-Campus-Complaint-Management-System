package service;

import model.Complaint;
import model.ComplaintCategory;
import model.ComplaintPriority;
import model.ComplaintStatus;
import repository.ComplaintRepository;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service providing statistical analytics, aggregated reporting, and performance KPIs.
 * Demonstrates:
 * - Java Collections (Map, List, LinkedHashMap)
 * - Java Streams API (groupingBy, counting, filtering)
 */
public class ReportService {

    private final ComplaintRepository complaintRepository;

    public ReportService(ComplaintRepository complaintRepository) {
        this.complaintRepository = Objects.requireNonNull(complaintRepository);
    }

    /**
     * Data Transfer Object encapsulating complete system analytics metrics.
     */
    public record AnalyticsSummary(
            long totalComplaints,
            long submittedCount,
            long inProgressCount,
            long resolvedCount,
            long rejectedCount,
            double resolutionRatePercent,
            Map<ComplaintCategory, Long> categoryBreakdown,
            Map<ComplaintPriority, Long> priorityBreakdown,
            long urgentPendingCount
    ) implements Serializable {}

    /**
     * Generates a comprehensive analytical summary of all complaints.
     */
    public AnalyticsSummary generateSummary() {
        List<Complaint> all = complaintRepository.findAll();
        long total = all.size();

        long submitted = all.stream().filter(c -> c.getStatus() == ComplaintStatus.SUBMITTED).count();
        long inProgress = all.stream().filter(c -> c.getStatus() == ComplaintStatus.IN_PROGRESS).count();
        long resolved = all.stream().filter(c -> c.getStatus() == ComplaintStatus.RESOLVED).count();
        long rejected = all.stream().filter(c -> c.getStatus() == ComplaintStatus.REJECTED).count();

        double resolutionRate = total > 0 ? ((double) resolved / total) * 100.0 : 0.0;

        // Category breakdown with all categories guaranteed in map
        Map<ComplaintCategory, Long> categoryMap = new LinkedHashMap<>();
        for (ComplaintCategory cat : ComplaintCategory.values()) {
            categoryMap.put(cat, 0L);
        }
        all.stream()
                .collect(Collectors.groupingBy(Complaint::getCategory, Collectors.counting()))
                .forEach(categoryMap::put);

        // Priority breakdown with all priorities guaranteed in map
        Map<ComplaintPriority, Long> priorityMap = new LinkedHashMap<>();
        for (ComplaintPriority p : ComplaintPriority.values()) {
            priorityMap.put(p, 0L);
        }
        all.stream()
                .collect(Collectors.groupingBy(Complaint::getPriority, Collectors.counting()))
                .forEach(priorityMap::put);

        // Urgent complaints that are not yet resolved or rejected
        long urgentPending = all.stream()
                .filter(c -> c.getPriority() == ComplaintPriority.URGENT && !c.isClosed())
                .count();

        return new AnalyticsSummary(
                total,
                submitted,
                inProgress,
                resolved,
                rejected,
                Math.round(resolutionRate * 10.0) / 10.0,
                categoryMap,
                priorityMap,
                urgentPending
        );
    }

    /**
     * Generates analytics for a single student's complaints.
     */
    public AnalyticsSummary generateStudentSummary(String studentUsername) {
        List<Complaint> studentList = complaintRepository.findByStudentUsername(studentUsername);
        long total = studentList.size();

        long submitted = studentList.stream().filter(c -> c.getStatus() == ComplaintStatus.SUBMITTED).count();
        long inProgress = studentList.stream().filter(c -> c.getStatus() == ComplaintStatus.IN_PROGRESS).count();
        long resolved = studentList.stream().filter(c -> c.getStatus() == ComplaintStatus.RESOLVED).count();
        long rejected = studentList.stream().filter(c -> c.getStatus() == ComplaintStatus.REJECTED).count();

        double resolutionRate = total > 0 ? ((double) resolved / total) * 100.0 : 0.0;

        Map<ComplaintCategory, Long> categoryMap = new LinkedHashMap<>();
        for (ComplaintCategory cat : ComplaintCategory.values()) {
            categoryMap.put(cat, 0L);
        }
        studentList.stream()
                .collect(Collectors.groupingBy(Complaint::getCategory, Collectors.counting()))
                .forEach(categoryMap::put);

        Map<ComplaintPriority, Long> priorityMap = new LinkedHashMap<>();
        for (ComplaintPriority p : ComplaintPriority.values()) {
            priorityMap.put(p, 0L);
        }
        studentList.stream()
                .collect(Collectors.groupingBy(Complaint::getPriority, Collectors.counting()))
                .forEach(priorityMap::put);

        long urgentPending = studentList.stream()
                .filter(c -> c.getPriority() == ComplaintPriority.URGENT && !c.isClosed())
                .count();

        return new AnalyticsSummary(
                total,
                submitted,
                inProgress,
                resolved,
                rejected,
                Math.round(resolutionRate * 10.0) / 10.0,
                categoryMap,
                priorityMap,
                urgentPending
        );
    }
}
