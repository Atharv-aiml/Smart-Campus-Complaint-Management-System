package ui;

import model.ComplaintCategory;
import model.ComplaintPriority;
import service.ReportService;
import util.UITheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Map;

/**
 * Modern analytical reporting panel showing graphical category distribution bars,
 * priority breakdown, and key performance indicators.
 */
public class AnalyticsPanel extends JPanel {

    private final ReportService reportService;
    private final String studentFilter; // null for system-wide admin view, or username for student view

    private JPanel kpiContainer;
    private JPanel categoryBarsContainer;
    private JPanel priorityBarsContainer;

    public AnalyticsPanel(ReportService reportService) {
        this(reportService, null);
    }

    public AnalyticsPanel(ReportService reportService, String studentFilter) {
        this.reportService = reportService;
        this.studentFilter = studentFilter;
        initUI();
        refreshAnalytics();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MAIN);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new CompoundBorder(
                new LineBorder(UITheme.BORDER, 1),
                new EmptyBorder(14, 20, 14, 20)
        ));

        JLabel title = new JLabel(studentFilter == null ? "Campus Analytics & Reports" : "My Complaint Analytics");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JButton refreshBtn = UITheme.createSecondaryButton("Refresh Analytics");
        refreshBtn.addActionListener(e -> refreshAnalytics());

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Main scrollable content
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(UITheme.BG_MAIN);
        mainContent.setBorder(new EmptyBorder(16, 20, 16, 20));

        // 1. KPI Cards Row
        kpiContainer = new JPanel(new GridLayout(1, 5, 12, 0));
        kpiContainer.setOpaque(false);
        mainContent.add(kpiContainer);
        mainContent.add(Box.createVerticalStrut(20));

        // 2. Visual Charts Container (Category & Priority side by side)
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        chartsPanel.setOpaque(false);

        // Category Card
        JPanel catCard = UITheme.createCardPanel();
        catCard.setLayout(new BorderLayout(0, 10));
        JLabel catTitle = new JLabel("Category Distribution");
        catTitle.setFont(UITheme.FONT_HEADER);
        catTitle.setForeground(UITheme.TEXT_PRIMARY);
        catCard.add(catTitle, BorderLayout.NORTH);

        categoryBarsContainer = new JPanel();
        categoryBarsContainer.setLayout(new BoxLayout(categoryBarsContainer, BoxLayout.Y_AXIS));
        categoryBarsContainer.setOpaque(false);
        catCard.add(categoryBarsContainer, BorderLayout.CENTER);

        // Priority Card
        JPanel priCard = UITheme.createCardPanel();
        priCard.setLayout(new BorderLayout(0, 10));
        JLabel priTitle = new JLabel("Priority Breakdown");
        priTitle.setFont(UITheme.FONT_HEADER);
        priTitle.setForeground(UITheme.TEXT_PRIMARY);
        priCard.add(priTitle, BorderLayout.NORTH);

        priorityBarsContainer = new JPanel();
        priorityBarsContainer.setLayout(new BoxLayout(priorityBarsContainer, BoxLayout.Y_AXIS));
        priorityBarsContainer.setOpaque(false);
        priCard.add(priorityBarsContainer, BorderLayout.CENTER);

        chartsPanel.add(catCard);
        chartsPanel.add(priCard);
        mainContent.add(chartsPanel);

        JScrollPane scroll = new JScrollPane(mainContent);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        add(scroll, BorderLayout.CENTER);
    }

    public void refreshAnalytics() {
        ReportService.AnalyticsSummary summary = studentFilter == null
                ? reportService.generateSummary()
                : reportService.generateStudentSummary(studentFilter);

        // 1. Update KPI Cards
        kpiContainer.removeAll();
        kpiContainer.add(UITheme.createMetricCard("Total Complaints", String.valueOf(summary.totalComplaints()), UITheme.PRIMARY));
        kpiContainer.add(UITheme.createMetricCard("Pending / Submitted", String.valueOf(summary.submittedCount()), UITheme.SECONDARY));
        kpiContainer.add(UITheme.createMetricCard("In Progress", String.valueOf(summary.inProgressCount()), UITheme.WARNING));
        kpiContainer.add(UITheme.createMetricCard("Resolved", String.valueOf(summary.resolvedCount()), UITheme.SUCCESS));
        kpiContainer.add(UITheme.createMetricCard("Resolution Rate", summary.resolutionRatePercent() + "%", new Color(13, 148, 136)));
        kpiContainer.revalidate();
        kpiContainer.repaint();

        // 2. Update Category Bars
        categoryBarsContainer.removeAll();
        long maxCat = 1;
        for (long val : summary.categoryBreakdown().values()) {
            if (val > maxCat) maxCat = val;
        }

        for (Map.Entry<ComplaintCategory, Long> entry : summary.categoryBreakdown().entrySet()) {
            categoryBarsContainer.add(createBarRow(
                    entry.getKey().getDisplayName(),
                    entry.getValue(),
                    summary.totalComplaints(),
                    maxCat,
                    UITheme.ACCENT
            ));
            categoryBarsContainer.add(Box.createVerticalStrut(8));
        }
        categoryBarsContainer.revalidate();
        categoryBarsContainer.repaint();

        // 3. Update Priority Bars
        priorityBarsContainer.removeAll();
        long maxPri = 1;
        for (long val : summary.priorityBreakdown().values()) {
            if (val > maxPri) maxPri = val;
        }

        for (Map.Entry<ComplaintPriority, Long> entry : summary.priorityBreakdown().entrySet()) {
            Color barColor = Color.decode(entry.getKey().getColorHex());
            priorityBarsContainer.add(createBarRow(
                    entry.getKey().getDisplayName(),
                    entry.getValue(),
                    summary.totalComplaints(),
                    maxPri,
                    barColor
            ));
            priorityBarsContainer.add(Box.createVerticalStrut(10));
        }
        priorityBarsContainer.revalidate();
        priorityBarsContainer.repaint();
    }

    private JPanel createBarRow(String label, long count, long total, long max, Color barColor) {
        JPanel row = new JPanel(new BorderLayout(8, 2));
        row.setOpaque(false);

        int pct = total > 0 ? (int) Math.round(((double) count / total) * 100.0) : 0;

        JPanel topText = new JPanel(new BorderLayout());
        topText.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BODY_BOLD);
        lbl.setForeground(UITheme.TEXT_PRIMARY);

        JLabel cnt = new JLabel(String.format("%d (%d%%)", count, pct));
        cnt.setFont(UITheme.FONT_SMALL);
        cnt.setForeground(UITheme.TEXT_SECONDARY);

        topText.add(lbl, BorderLayout.WEST);
        topText.add(cnt, BorderLayout.EAST);
        row.add(topText, BorderLayout.NORTH);

        // Progress meter bar
        JProgressBar bar = new JProgressBar(0, (int) Math.max(max, 1));
        bar.setValue((int) count);
        bar.setForeground(barColor);
        bar.setBackground(new Color(241, 245, 249));
        bar.setPreferredSize(new Dimension(200, 10));
        bar.setBorder(new LineBorder(UITheme.BORDER, 1, true));
        bar.setStringPainted(false);
        row.add(bar, BorderLayout.CENTER);

        return row;
    }
}
