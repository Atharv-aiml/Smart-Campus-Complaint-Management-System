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

/**
 * Main dashboard frame for college students.
 * Provides complaint submission, tracking, searching, filtering, and profile viewing.
 */
public class StudentDashboardFrame extends JFrame {

    private final Student student;
    private final AuthService authService;
    private final ComplaintService complaintService;
    private final ReportService reportService;

    private JTabbedPane tabbedPane;

    // My Complaints Components
    private JTable complaintsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private JLabel totalCountLabel;
    private JLabel pendingCountLabel;
    private JLabel inProgressCountLabel;
    private JLabel resolvedCountLabel;

    // Submit Complaint Form Components
    private JComboBox<ComplaintCategory> categoryCombo;
    private JComboBox<ComplaintPriority> priorityCombo;
    private JTextField titleField;
    private JTextField locationField;
    private JTextArea descriptionArea;

    // Analytics Panel
    private AnalyticsPanel analyticsPanel;

    public StudentDashboardFrame(Student student, AuthService authService,
                                 ComplaintService complaintService, ReportService reportService) {
        super("Smart Campus Portal - Student Dashboard [" + student.getRegistrationNumber() + "]");
        this.student = student;
        this.authService = authService;
        this.complaintService = complaintService;
        this.reportService = reportService;

        initUI();
        refreshComplaintsTable();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Navigation Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Tabbed Main Area
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BODY_BOLD);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("My Complaints", createComplaintsTab());
        tabbedPane.addTab("File New Complaint", createSubmitComplaintTab());
        tabbedPane.addTab("My Analytics", createAnalyticsTab());
        tabbedPane.addTab("My Profile", createProfileTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        // Left branding
        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        JLabel title = new JLabel("VIT BHOPAL - SMART CAMPUS COMPLAINTS");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Student Portal | Logged in as: " + student.getFullName() + " (" + student.getRegistrationNumber() + ")");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(new Color(224, 231, 255));

        left.add(title);
        left.add(sub);
        header.add(left, BorderLayout.WEST);

        // Right Actions (File Complaint Shortcut + Logout)
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JButton submitQuickBtn = UITheme.createActionButton("+ File Complaint", UITheme.SUCCESS, Color.WHITE);
        submitQuickBtn.addActionListener(e -> tabbedPane.setSelectedIndex(1));

        JButton logoutBtn = UITheme.createActionButton("Logout", new Color(220, 38, 38), Color.WHITE);
        logoutBtn.addActionListener(e -> handleLogout());

        right.add(submitQuickBtn);
        right.add(logoutBtn);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    private JPanel createComplaintsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        // 1. KPI Cards Row
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 12, 0));
        kpiRow.setOpaque(false);

        totalCountLabel = new JLabel("0");
        pendingCountLabel = new JLabel("0");
        inProgressCountLabel = new JLabel("0");
        resolvedCountLabel = new JLabel("0");

        kpiRow.add(createMiniCard("TOTAL FILED", totalCountLabel, UITheme.PRIMARY));
        kpiRow.add(createMiniCard("PENDING REVIEW", pendingCountLabel, UITheme.SECONDARY));
        kpiRow.add(createMiniCard("IN PROGRESS", inProgressCountLabel, UITheme.WARNING));
        kpiRow.add(createMiniCard("RESOLVED", resolvedCountLabel, UITheme.SUCCESS));

        panel.add(kpiRow, BorderLayout.NORTH);

        // 2. Table and Controls Container
        JPanel tableContainer = UITheme.createCardPanel();
        tableContainer.setLayout(new BorderLayout(0, 10));

        // Filter Bar
        JPanel filterBar = new JPanel(new BorderLayout(10, 0));
        filterBar.setOpaque(false);

        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBox.setOpaque(false);

        searchBox.add(new JLabel("Search:"));
        searchField = UITheme.createTextField(16);
        searchField.setToolTipText("Search title, ID, location...");
        searchBox.add(searchField);

        JButton searchBtn = UITheme.createSecondaryButton("Search");
        searchBtn.addActionListener(e -> filterTable());
        searchBox.add(searchBtn);

        searchBox.add(new JLabel("Status:"));
        statusFilterCombo = new JComboBox<>(new String[]{"All Statuses", "Submitted", "In Progress", "Resolved", "Rejected"});
        statusFilterCombo.setFont(UITheme.FONT_BODY);
        statusFilterCombo.setBackground(Color.WHITE);
        statusFilterCombo.addActionListener(e -> filterTable());
        searchBox.add(statusFilterCombo);

        JButton resetBtn = UITheme.createSecondaryButton("Reset");
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            statusFilterCombo.setSelectedIndex(0);
            refreshComplaintsTable();
        });
        searchBox.add(resetBtn);

        filterBar.add(searchBox, BorderLayout.WEST);

        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tableActions.setOpaque(false);

        JButton viewDetailsBtn = UITheme.createPrimaryButton("View Complaint Details");
        viewDetailsBtn.addActionListener(e -> openSelectedComplaintDetails());
        tableActions.add(viewDetailsBtn);

        filterBar.add(tableActions, BorderLayout.EAST);
        tableContainer.add(filterBar, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Category", "Priority", "Title", "Location", "Status", "Date Submitted", "Assigned To"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        complaintsTable = new JTable(tableModel);
        complaintsTable.setRowHeight(32);
        complaintsTable.setFont(UITheme.FONT_BODY);
        complaintsTable.getTableHeader().setFont(UITheme.FONT_BODY_BOLD);
        complaintsTable.getTableHeader().setBackground(new Color(241, 245, 249));
        complaintsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Column Renderers
        complaintsTable.getColumnModel().getColumn(1).setCellRenderer(new UITheme.BadgeCellRenderer());
        complaintsTable.getColumnModel().getColumn(2).setCellRenderer(new UITheme.BadgeCellRenderer());
        complaintsTable.getColumnModel().getColumn(5).setCellRenderer(new UITheme.BadgeCellRenderer());

        // Column widths
        complaintsTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        complaintsTable.getColumnModel().getColumn(1).setPreferredWidth(110);
        complaintsTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        complaintsTable.getColumnModel().getColumn(3).setPreferredWidth(260);
        complaintsTable.getColumnModel().getColumn(4).setPreferredWidth(140);
        complaintsTable.getColumnModel().getColumn(5).setPreferredWidth(110);
        complaintsTable.getColumnModel().getColumn(6).setPreferredWidth(150);
        complaintsTable.getColumnModel().getColumn(7).setPreferredWidth(130);

        JScrollPane tableScroll = new JScrollPane(complaintsTable);
        tableScroll.setBorder(new LineBorder(UITheme.BORDER, 1));
        tableContainer.add(tableScroll, BorderLayout.CENTER);

        panel.add(tableContainer, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMiniCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(accent, 2, true),
                new EmptyBorder(10, 14, 10, 14)
        ));

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        valueLabel.setForeground(accent);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.FONT_SMALL);
        titleLabel.setForeground(UITheme.TEXT_SECONDARY);

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createSubmitComplaintTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("File a New Campus Complaint Ticket");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.PRIMARY);
        formCard.add(title);
        formCard.add(Box.createVerticalStrut(4));

        JLabel sub = new JLabel("Provide clear details to ensure prompt assignment and resolution by campus maintenance.");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);
        formCard.add(sub);
        formCard.add(Box.createVerticalStrut(18));

        // Row 1: Category & Priority
        JPanel row1 = new JPanel(new GridLayout(1, 2, 16, 0));
        row1.setOpaque(false);

        categoryCombo = new JComboBox<>(ComplaintCategory.values());
        categoryCombo.setFont(UITheme.FONT_BODY);
        categoryCombo.setBackground(Color.WHITE);

        priorityCombo = new JComboBox<>(ComplaintPriority.values());
        priorityCombo.setFont(UITheme.FONT_BODY);
        priorityCombo.setBackground(Color.WHITE);
        priorityCombo.setSelectedItem(ComplaintPriority.MEDIUM);

        row1.add(createFormField("Complaint Category *", categoryCombo));
        row1.add(createFormField("Severity / Priority Level *", priorityCombo));
        formCard.add(row1);
        formCard.add(Box.createVerticalStrut(14));

        // Row 2: Title
        titleField = UITheme.createTextField(30);
        titleField.setToolTipText("e.g., Water leakage near room 312 cooler dispenser");
        formCard.add(createFormField("Complaint Title (Brief & Clear) *", titleField));
        formCard.add(Box.createVerticalStrut(14));

        // Row 3: Location
        locationField = UITheme.createTextField(30);
        locationField.setText(student.getHostelBlock() + ", Room " + student.getRoomNumber());
        formCard.add(createFormField("Exact Location / Academic Block / Room No *", locationField));
        formCard.add(Box.createVerticalStrut(14));

        // Row 4: Description
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setFont(UITheme.FONT_BODY);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(new CompoundBorder(new LineBorder(UITheme.BORDER, 1), new EmptyBorder(6, 8, 6, 8)));
        JScrollPane descScroll = new JScrollPane(descriptionArea);

        formCard.add(createFormField("Detailed Description of Issue *", descScroll));
        formCard.add(Box.createVerticalStrut(20));

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actions.setOpaque(false);

        JButton clearBtn = UITheme.createSecondaryButton("Clear Form");
        clearBtn.addActionListener(e -> resetSubmitForm());

        JButton submitBtn = UITheme.createPrimaryButton("Submit Complaint Ticket");
        submitBtn.addActionListener(e -> handleSubmitComplaint());

        actions.add(clearBtn);
        actions.add(submitBtn);
        formCard.add(actions);

        JScrollPane mainScroll = new JScrollPane(formCard);
        mainScroll.setBorder(null);
        panel.add(mainScroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFormField(String labelText, JComponent component) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UITheme.FONT_BODY_BOLD);
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        p.add(lbl, BorderLayout.NORTH);
        p.add(component, BorderLayout.CENTER);
        return p;
    }

    private JPanel createAnalyticsTab() {
        analyticsPanel = new AnalyticsPanel(reportService, student.getUsername());
        return analyticsPanel;
    }

    private JPanel createProfileTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Student Profile Details");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.PRIMARY);
        card.add(title);
        card.add(Box.createVerticalStrut(16));

        card.add(createProfileRow("Full Name", student.getFullName()));
        card.add(createProfileRow("Username", student.getUsername()));
        card.add(createProfileRow("Registration Number", student.getRegistrationNumber()));
        card.add(createProfileRow("Department / School", student.getDepartment()));
        card.add(createProfileRow("Hostel Block", student.getHostelBlock()));
        card.add(createProfileRow("Room Number", student.getRoomNumber()));
        card.add(createProfileRow("Email Address", student.getEmail()));
        card.add(createProfileRow("Phone Number", student.getPhone()));
        card.add(createProfileRow("Account Created", DateTimeUtil.format(student.getCreatedAt())));

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

    private void handleSubmitComplaint() {
        ComplaintCategory cat = (ComplaintCategory) categoryCombo.getSelectedItem();
        ComplaintPriority pri = (ComplaintPriority) priorityCombo.getSelectedItem();
        String title = titleField.getText().trim();
        String location = locationField.getText().trim();
        String desc = descriptionArea.getText().trim();

        try {
            Complaint filed = complaintService.submitComplaint(student, cat, pri, title, desc, location);

            JOptionPane.showMessageDialog(
                    this,
                    "Complaint ticket created successfully!\n\nTicket ID: " + filed.getComplaintId() +
                            "\nCategory: " + filed.getCategory().getDisplayName() +
                            "\nPriority: " + filed.getPriority().getDisplayName() +
                            "\nStatus: " + filed.getStatus().getDisplayName() +
                            "\n\nYou can track updates anytime in 'My Complaints'.",
                    "Ticket Submitted",
                    JOptionPane.INFORMATION_MESSAGE
            );

            resetSubmitForm();
            refreshComplaintsTable();
            if (analyticsPanel != null) {
                analyticsPanel.refreshAnalytics();
            }
            tabbedPane.setSelectedIndex(0); // Switch back to complaints table
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to submit complaint: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetSubmitForm() {
        titleField.setText("");
        descriptionArea.setText("");
        categoryCombo.setSelectedIndex(0);
        priorityCombo.setSelectedItem(ComplaintPriority.MEDIUM);
        locationField.setText(student.getHostelBlock() + ", Room " + student.getRoomNumber());
    }

    private void refreshComplaintsTable() {
        List<Complaint> list = complaintService.getStudentComplaints(student.getUsername());
        populateTable(list);
        updateCounters(list);
    }

    private void filterTable() {
        String keyword = searchField.getText().trim();
        String selectedStatus = (String) statusFilterCombo.getSelectedItem();

        ComplaintStatus status = null;
        if (selectedStatus != null && !selectedStatus.equalsIgnoreCase("All Statuses")) {
            for (ComplaintStatus s : ComplaintStatus.values()) {
                if (s.getDisplayName().equalsIgnoreCase(selectedStatus)) {
                    status = s;
                    break;
                }
            }
        }

        List<Complaint> list = complaintService.getStudentComplaints(student.getUsername(), status);
        if (!keyword.isEmpty()) {
            String q = keyword.toLowerCase();
            list = list.stream().filter(c ->
                    c.getComplaintId().toLowerCase().contains(q) ||
                    c.getTitle().toLowerCase().contains(q) ||
                    c.getLocation().toLowerCase().contains(q) ||
                    c.getCategory().getDisplayName().toLowerCase().contains(q)
            ).toList();
        }
        populateTable(list);
    }

    private void populateTable(List<Complaint> list) {
        tableModel.setRowCount(0);
        for (Complaint c : list) {
            tableModel.addRow(new Object[]{
                    c.getComplaintId(),
                    c.getCategory().getDisplayName(),
                    c.getPriority().getDisplayName(),
                    c.getTitle(),
                    c.getLocation(),
                    c.getStatus().getDisplayName(),
                    DateTimeUtil.formatShort(c.getCreatedAt()),
                    c.getAssignedTo()
            });
        }
    }

    private void updateCounters(List<Complaint> list) {
        long total = list.size();
        long pending = list.stream().filter(c -> c.getStatus() == ComplaintStatus.SUBMITTED).count();
        long inProgress = list.stream().filter(c -> c.getStatus() == ComplaintStatus.IN_PROGRESS).count();
        long resolved = list.stream().filter(c -> c.getStatus() == ComplaintStatus.RESOLVED).count();

        totalCountLabel.setText(String.valueOf(total));
        pendingCountLabel.setText(String.valueOf(pending));
        inProgressCountLabel.setText(String.valueOf(inProgress));
        resolvedCountLabel.setText(String.valueOf(resolved));
    }

    private void openSelectedComplaintDetails() {
        int selectedRow = complaintsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a complaint row from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String complaintId = (String) tableModel.getValueAt(selectedRow, 0);
        try {
            Complaint complaint = complaintService.getComplaintById(complaintId);
            ComplaintDetailsDialog dialog = new ComplaintDetailsDialog(this, complaint);
            dialog.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load complaint details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out of your student session?",
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
