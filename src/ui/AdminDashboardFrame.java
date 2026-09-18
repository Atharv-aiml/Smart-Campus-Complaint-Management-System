package ui;

import exception.ValidationException;
import model.*;
import service.AuthService;
import service.ComplaintService;
import service.ReportService;
import util.DateTimeUtil;
import util.UITheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

/**
 * Main dashboard frame for campus administrators.
 * Provides complete complaint management, assignment, status updates, analytics, and student inspection.
 */
public class AdminDashboardFrame extends JFrame {

    private final Admin admin;
    private final AuthService authService;
    private final ComplaintService complaintService;
    private final ReportService reportService;

    private JTabbedPane tabbedPane;

    // Complaints Table & Controls
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private JComboBox<String> categoryFilterCombo;
    private JComboBox<String> priorityFilterCombo;

    // KPI Labels
    private JLabel totalKpiLabel;
    private JLabel submittedKpiLabel;
    private JLabel inProgressKpiLabel;
    private JLabel resolvedKpiLabel;
    private JLabel urgentKpiLabel;

    // Analytics Panel
    private AnalyticsPanel analyticsPanel;

    public AdminDashboardFrame(Admin admin, AuthService authService,
                               ComplaintService complaintService, ReportService reportService) {
        super("Smart Campus Portal - Administrator Management Console [" + admin.getDepartment() + "]");
        this.admin = admin;
        this.authService = authService;
        this.complaintService = complaintService;
        this.reportService = reportService;

        initUI();
        refreshComplaints();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 780);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BODY_BOLD);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("Complaints Management", createManagementTab());
        tabbedPane.addTab("Campus Analytics & KPI Reports", createAnalyticsTab());
        tabbedPane.addTab("Admin Profile", createProfileTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setBackground(new Color(15, 23, 42)); // Deep Slate
        header.setBorder(new EmptyBorder(12, 24, 12, 24));

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);

        JLabel title = new JLabel("VIT BHOPAL - CAMPUS ADMINISTRATION PORTAL");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Logged in as: " + admin.getFullName() + " | " + admin.getRoleTitle());
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(new Color(148, 163, 184));

        left.add(title);
        left.add(sub);
        header.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JButton refreshBtn = UITheme.createSecondaryButton("Refresh Data");
        refreshBtn.addActionListener(e -> refreshComplaints());

        JButton logoutBtn = UITheme.createActionButton("Logout", UITheme.DANGER, Color.WHITE);
        logoutBtn.addActionListener(e -> handleLogout());

        right.add(refreshBtn);
        right.add(logoutBtn);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    private JPanel createManagementTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        // 1. KPI Cards Row
        JPanel kpiRow = new JPanel(new GridLayout(1, 5, 12, 0));
        kpiRow.setOpaque(false);

        totalKpiLabel = new JLabel("0");
        submittedKpiLabel = new JLabel("0");
        inProgressKpiLabel = new JLabel("0");
        resolvedKpiLabel = new JLabel("0");
        urgentKpiLabel = new JLabel("0");

        kpiRow.add(UITheme.createMetricCard("TOTAL COMPLAINTS", "0", UITheme.PRIMARY));
        kpiRow.add(UITheme.createMetricCard("AWAITING REVIEW", "0", UITheme.SECONDARY));
        kpiRow.add(UITheme.createMetricCard("IN PROGRESS", "0", UITheme.WARNING));
        kpiRow.add(UITheme.createMetricCard("RESOLVED TICKETS", "0", UITheme.SUCCESS));
        kpiRow.add(UITheme.createMetricCard("URGENT PENDING", "0", UITheme.DANGER));

        panel.add(kpiRow, BorderLayout.NORTH);

        // 2. Table and Actions Container
        JPanel mainCard = UITheme.createCardPanel();
        mainCard.setLayout(new BorderLayout(0, 12));

        // Top Filter Bar
        JPanel filterBar = new JPanel(new BorderLayout(10, 0));
        filterBar.setOpaque(false);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setOpaque(false);

        filters.add(new JLabel("Search:"));
        searchField = UITheme.createTextField(14);
        searchField.setToolTipText("Search title, student, reg no, ID...");
        filters.add(searchField);

        filters.add(new JLabel("Status:"));
        statusFilterCombo = new JComboBox<>(new String[]{"All Statuses", "Submitted", "In Progress", "Resolved", "Rejected"});
        statusFilterCombo.setBackground(Color.WHITE);
        statusFilterCombo.addActionListener(e -> applyFilters());
        filters.add(statusFilterCombo);

        filters.add(new JLabel("Category:"));
        String[] catOptions = new String[ComplaintCategory.values().length + 1];
        catOptions[0] = "All Categories";
        for (int i = 0; i < ComplaintCategory.values().length; i++) {
            catOptions[i + 1] = ComplaintCategory.values()[i].getDisplayName();
        }
        categoryFilterCombo = new JComboBox<>(catOptions);
        categoryFilterCombo.setBackground(Color.WHITE);
        categoryFilterCombo.addActionListener(e -> applyFilters());
        filters.add(categoryFilterCombo);

        filters.add(new JLabel("Priority:"));
        String[] priOptions = new String[ComplaintPriority.values().length + 1];
        priOptions[0] = "All Priorities";
        for (int i = 0; i < ComplaintPriority.values().length; i++) {
            priOptions[i + 1] = ComplaintPriority.values()[i].getDisplayName();
        }
        priorityFilterCombo = new JComboBox<>(priOptions);
        priorityFilterCombo.setBackground(Color.WHITE);
        priorityFilterCombo.addActionListener(e -> applyFilters());
        filters.add(priorityFilterCombo);

        JButton filterBtn = UITheme.createSecondaryButton("Filter");
        filterBtn.addActionListener(e -> applyFilters());
        filters.add(filterBtn);

        JButton resetBtn = UITheme.createSecondaryButton("Reset");
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            statusFilterCombo.setSelectedIndex(0);
            categoryFilterCombo.setSelectedIndex(0);
            priorityFilterCombo.setSelectedIndex(0);
            refreshComplaints();
        });
        filters.add(resetBtn);

        filterBar.add(filters, BorderLayout.CENTER);
        mainCard.add(filterBar, BorderLayout.NORTH);

        // Complaints Table
        String[] cols = {"ID", "Student Name", "Reg No", "Category", "Priority", "Title", "Location", "Status", "Assigned To", "Submitted"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(UITheme.FONT_BODY);
        table.getTableHeader().setFont(UITheme.FONT_BODY_BOLD);
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Renderers
        table.getColumnModel().getColumn(3).setCellRenderer(new UITheme.BadgeCellRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new UITheme.BadgeCellRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new UITheme.BadgeCellRenderer());

        table.getColumnModel().getColumn(0).setPreferredWidth(110);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(85);
        table.getColumnModel().getColumn(5).setPreferredWidth(220);
        table.getColumnModel().getColumn(6).setPreferredWidth(130);
        table.getColumnModel().getColumn(7).setPreferredWidth(105);
        table.getColumnModel().getColumn(8).setPreferredWidth(130);
        table.getColumnModel().getColumn(9).setPreferredWidth(125);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(new LineBorder(UITheme.BORDER, 1));
        mainCard.add(tableScroll, BorderLayout.CENTER);

        // Bottom Action Bar
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        actionRow.setOpaque(false);

        JButton viewDetailsBtn = UITheme.createSecondaryButton("View Full Details");
        viewDetailsBtn.addActionListener(e -> openSelectedDetails());

        JButton viewStudentBtn = UITheme.createSecondaryButton("Student Info");
        viewStudentBtn.addActionListener(e -> viewSelectedStudentInfo());

        JButton assignBtn = UITheme.createActionButton("Assign Staff", new Color(79, 70, 229), Color.WHITE);
        assignBtn.addActionListener(e -> handleAssignStaff());

        JButton updateStatusBtn = UITheme.createActionButton("Update Status", UITheme.WARNING, Color.WHITE);
        updateStatusBtn.addActionListener(e -> handleUpdateStatus());

        JButton resolveBtn = UITheme.createActionButton("Resolve Ticket", UITheme.SUCCESS, Color.WHITE);
        resolveBtn.addActionListener(e -> handleQuickResolve());

        actionRow.add(viewDetailsBtn);
        actionRow.add(viewStudentBtn);
        actionRow.add(assignBtn);
        actionRow.add(updateStatusBtn);
        actionRow.add(resolveBtn);

        mainCard.add(actionRow, BorderLayout.SOUTH);
        panel.add(mainCard, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAnalyticsTab() {
        analyticsPanel = new AnalyticsPanel(reportService, null);
        return analyticsPanel;
    }

    private JPanel createProfileTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Administrator Profile");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.PRIMARY);
        card.add(title);
        card.add(Box.createVerticalStrut(16));

        card.add(createProfileRow("Full Name", admin.getFullName()));
        card.add(createProfileRow("Admin ID", admin.getAdminId()));
        card.add(createProfileRow("Username", admin.getUsername()));
        card.add(createProfileRow("Designation", admin.getDesignation()));
        card.add(createProfileRow("Department", admin.getDepartment()));
        card.add(createProfileRow("Official Email", admin.getEmail()));
        card.add(createProfileRow("Contact Phone", admin.getPhone()));
        card.add(createProfileRow("Account Created", DateTimeUtil.format(admin.getCreatedAt())));

        panel.add(card, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createProfileRow(String label, String value) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(8, 0, 8, 0));

        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_BODY_BOLD);
        l.setForeground(UITheme.TEXT_SECONDARY);
        l.setPreferredSize(new Dimension(200, 20));

        JLabel v = new JLabel(value != null ? value : "N/A");
        v.setFont(UITheme.FONT_BODY);
        v.setForeground(UITheme.TEXT_PRIMARY);

        p.add(l, BorderLayout.WEST);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private void refreshComplaints() {
        List<Complaint> list = complaintService.getAllComplaints();
        populateTable(list);
        updateKpis();
        if (analyticsPanel != null) {
            analyticsPanel.refreshAnalytics();
        }
    }

    private void updateKpis() {
        ReportService.AnalyticsSummary s = reportService.generateSummary();
        totalKpiLabel.setText(String.valueOf(s.totalComplaints()));
        submittedKpiLabel.setText(String.valueOf(s.submittedCount()));
        inProgressKpiLabel.setText(String.valueOf(s.inProgressCount()));
        resolvedKpiLabel.setText(String.valueOf(s.resolvedCount()));
        urgentKpiLabel.setText(String.valueOf(s.urgentPendingCount()));
    }

    private void applyFilters() {
        String keyword = searchField.getText().trim();
        String selStatus = (String) statusFilterCombo.getSelectedItem();
        String selCat = (String) categoryFilterCombo.getSelectedItem();
        String selPri = (String) priorityFilterCombo.getSelectedItem();

        ComplaintStatus status = null;
        if (selStatus != null && !selStatus.equalsIgnoreCase("All Statuses")) {
            for (ComplaintStatus s : ComplaintStatus.values()) {
                if (s.getDisplayName().equalsIgnoreCase(selStatus)) {
                    status = s;
                    break;
                }
            }
        }

        ComplaintCategory category = null;
        if (selCat != null && !selCat.equalsIgnoreCase("All Categories")) {
            for (ComplaintCategory c : ComplaintCategory.values()) {
                if (c.getDisplayName().equalsIgnoreCase(selCat)) {
                    category = c;
                    break;
                }
            }
        }

        ComplaintPriority priority = null;
        if (selPri != null && !selPri.equalsIgnoreCase("All Priorities")) {
            for (ComplaintPriority p : ComplaintPriority.values()) {
                if (p.getDisplayName().equalsIgnoreCase(selPri)) {
                    priority = p;
                    break;
                }
            }
        }

        List<Complaint> filtered = complaintService.searchComplaints(keyword, status, category, priority);
        populateTable(filtered);
    }

    private void populateTable(List<Complaint> list) {
        tableModel.setRowCount(0);
        for (Complaint c : list) {
            tableModel.addRow(new Object[]{
                    c.getComplaintId(),
                    c.getStudentName(),
                    c.getStudentRegNo(),
                    c.getCategory().getDisplayName(),
                    c.getPriority().getDisplayName(),
                    c.getTitle(),
                    c.getLocation(),
                    c.getStatus().getDisplayName(),
                    c.getAssignedTo(),
                    DateTimeUtil.formatShort(c.getCreatedAt())
            });
        }
    }

    private Optional<Complaint> getSelectedComplaint() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a complaint row first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return Optional.empty();
        }
        String id = (String) tableModel.getValueAt(row, 0);
        try {
            return Optional.of(complaintService.getComplaintById(id));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error finding complaint: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return Optional.empty();
        }
    }

    private void openSelectedDetails() {
        getSelectedComplaint().ifPresent(complaint -> {
            ComplaintDetailsDialog dialog = new ComplaintDetailsDialog(this, complaint);
            dialog.setVisible(true);
        });
    }

    private void viewSelectedStudentInfo() {
        getSelectedComplaint().ifPresent(complaint -> {
            Optional<Student> studentOpt = complaintService.getStudentForComplaint(complaint);
            if (studentOpt.isPresent()) {
                Student s = studentOpt.get();
                String message = String.format(
                        "Student Information:\n\n" +
                                "Full Name: %s\n" +
                                "Registration No: %s\n" +
                                "Department: %s\n" +
                                "Hostel Block: %s\n" +
                                "Room No: %s\n" +
                                "Email: %s\n" +
                                "Phone: %s\n" +
                                "Username: %s",
                        s.getFullName(), s.getRegistrationNumber(), s.getDepartment(),
                        s.getHostelBlock(), s.getRoomNumber(), s.getEmail(), s.getPhone(), s.getUsername()
                );
                JOptionPane.showMessageDialog(this, message, "Student Details - " + s.getRegistrationNumber(), JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Student registration details could not be retrieved.\nUsername on ticket: " + complaint.getStudentUsername(),
                        "Student Info", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void handleAssignStaff() {
        getSelectedComplaint().ifPresent(complaint -> {
            JPanel p = new JPanel(new GridLayout(4, 1, 5, 5));
            p.add(new JLabel("Assign to Department / Staff Member:"));
            JTextField staffField = UITheme.createTextField(20);
            staffField.setText(complaint.getAssignedTo().equals("Unassigned") ? "" : complaint.getAssignedTo());
            p.add(staffField);

            p.add(new JLabel("Admin Notes / Instructions:"));
            JTextField remarksField = UITheme.createTextField(20);
            remarksField.setText(complaint.getAdminRemarks());
            p.add(remarksField);

            int opt = JOptionPane.showConfirmDialog(this, p, "Assign Complaint - " + complaint.getComplaintId(),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (opt == JOptionPane.OK_OPTION) {
                String staff = staffField.getText().trim();
                String notes = remarksField.getText().trim();
                try {
                    complaintService.assignComplaint(complaint.getComplaintId(), staff, notes);
                    JOptionPane.showMessageDialog(this, "Complaint assigned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshComplaints();
                } catch (ValidationException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void handleUpdateStatus() {
        getSelectedComplaint().ifPresent(complaint -> {
            JPanel p = new JPanel(new GridLayout(4, 1, 5, 5));
            p.add(new JLabel("Select New Status:"));

            JComboBox<ComplaintStatus> statusCombo = new JComboBox<>(ComplaintStatus.values());
            statusCombo.setSelectedItem(complaint.getStatus());
            p.add(statusCombo);

            p.add(new JLabel("Official Remarks / Update Notes:"));
            JTextField remarksField = UITheme.createTextField(20);
            remarksField.setText(complaint.getAdminRemarks());
            p.add(remarksField);

            int opt = JOptionPane.showConfirmDialog(this, p, "Update Status - " + complaint.getComplaintId(),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (opt == JOptionPane.OK_OPTION) {
                ComplaintStatus newStatus = (ComplaintStatus) statusCombo.getSelectedItem();
                String remarks = remarksField.getText().trim();
                try {
                    complaintService.updateStatus(complaint.getComplaintId(), newStatus, remarks);
                    JOptionPane.showMessageDialog(this, "Status updated to " + newStatus.getDisplayName() + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshComplaints();
                } catch (ValidationException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Status Transition Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void handleQuickResolve() {
        getSelectedComplaint().ifPresent(complaint -> {
            String notes = JOptionPane.showInputDialog(
                    this,
                    "Enter resolution notes / remarks for complaint " + complaint.getComplaintId() + ":",
                    "Resolve Complaint Ticket",
                    JOptionPane.PLAIN_MESSAGE
            );
            if (notes != null) {
                try {
                    complaintService.resolveComplaint(complaint.getComplaintId(), notes);
                    JOptionPane.showMessageDialog(this, "Ticket marked as RESOLVED!", "Resolved", JOptionPane.INFORMATION_MESSAGE);
                    refreshComplaints();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to resolve: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out of the admin console?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            authService.logout();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame(authService, complaintService, reportService).setVisible(true));
        }
    }
}
