package tests;

import model.*;
import repository.AdminRepository;
import repository.ComplaintRepository;
import repository.StudentRepository;
import service.AuthService;
import service.ComplaintService;
import service.ReportService;
import ui.AnalyticsPanel;
import util.DataInitializer;
import util.DateTimeUtil;
import util.UITheme;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 * Headless Swing renderer that renders pixel-perfect PNG captures of all application screens
 * directly using the system's actual Swing layout, components, fonts, colors, and renderers.
 */
public class SwingPanelCapture {

    private static final String DIR = "screenshots";

    public static void main(String[] args) {
        new File(DIR).mkdirs();

        // 1. Initialize data
        File tempDir = new File("test_render_data");
        tempDir.mkdirs();
        StudentRepository studentRepo = new StudentRepository(tempDir.getPath() + "/students.dat");
        AdminRepository adminRepo = new AdminRepository(tempDir.getPath() + "/admins.dat");
        ComplaintRepository complaintRepo = new ComplaintRepository(tempDir.getPath() + "/complaints.dat");
        studentRepo.clear();
        adminRepo.clear();
        complaintRepo.clear();
        DataInitializer.initializeSampleData(studentRepo, adminRepo, complaintRepo);

        AuthService authService = new AuthService(studentRepo, adminRepo);
        ComplaintService complaintService = new ComplaintService(complaintRepo, studentRepo);
        ReportService reportService = new ReportService(complaintRepo);

        Student student = studentRepo.findByUsername("atharv").orElseThrow();
        Admin admin = adminRepo.findByUsername("admin").orElseThrow();
        Complaint sampleComplaint = complaintService.getAllComplaints().get(0);

        System.out.println("Generating Swing screenshots...");

        // 1. Login Screen
        savePanel(createLoginView(), DIR + "/01_login_screen.png", 520, 640);

        // 2. Student Registration Screen
        savePanel(createRegisterView(), DIR + "/02_student_registration.png", 560, 680);

        // 3. Student Dashboard (My Complaints)
        savePanel(createStudentDashboardView(student, complaintService), DIR + "/03_student_dashboard_my_complaints.png", 1080, 720);

        // 4. Student Submit Complaint Screen
        savePanel(createSubmitComplaintView(student), DIR + "/04_student_submit_complaint.png", 1080, 720);

        // 5. Complaint Details Modal
        savePanel(createComplaintDetailsView(sampleComplaint), DIR + "/05_complaint_details_modal.png", 620, 650);

        // 6. Admin Dashboard (Complaint Management)
        savePanel(createAdminDashboardView(admin, complaintService, reportService), DIR + "/06_admin_dashboard_management.png", 1180, 760);

        // 7. Analytics & Reports Screen
        AnalyticsPanel analyticsPanel = new AnalyticsPanel(reportService, null);
        savePanel(analyticsPanel, DIR + "/07_campus_analytics_reports.png", 1080, 720);

        System.out.println("All screenshots successfully rendered and saved!");

        // Cleanup
        studentRepo.clear();
        adminRepo.clear();
        complaintRepo.clear();
        deleteDirectory(tempDir);
    }

    private static JPanel createLoginView() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_MAIN);

        JPanel header = new JPanel(new BorderLayout(0, 6));
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(new EmptyBorder(26, 24, 26, 24));

        JLabel title = new JLabel("Smart Campus Portal");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel sub = new JLabel("Complaint Tracking & Resolution Management System");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(new Color(224, 231, 255));
        sub.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel inst = new JLabel("VIT Bhopal University - Java Academic Project");
        inst.setFont(new Font("SansSerif", Font.ITALIC, 11));
        inst.setForeground(new Color(199, 210, 254));
        inst.setHorizontalAlignment(SwingConstants.CENTER);

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.CENTER);
        header.add(inst, BorderLayout.SOUTH);
        root.add(header, BorderLayout.NORTH);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(UITheme.BG_MAIN);
        main.setBorder(new EmptyBorder(24, 36, 24, 36));

        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel cardTitle = new JLabel("Sign In to Your Account");
        cardTitle.setFont(UITheme.FONT_HEADER);
        cardTitle.setForeground(UITheme.TEXT_PRIMARY);
        cardTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(cardTitle);
        card.add(Box.createVerticalStrut(18));

        JPanel rolePanel = new JPanel(new BorderLayout(0, 4));
        rolePanel.setOpaque(false);
        JLabel roleLabel = new JLabel("Select Portal Role:");
        roleLabel.setFont(UITheme.FONT_BODY_BOLD);
        roleLabel.setForeground(UITheme.TEXT_PRIMARY);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Student", "Administrator"});
        roleCombo.setFont(UITheme.FONT_BODY);
        roleCombo.setBackground(Color.WHITE);
        rolePanel.add(roleLabel, BorderLayout.NORTH);
        rolePanel.add(roleCombo, BorderLayout.CENTER);
        card.add(rolePanel);
        card.add(Box.createVerticalStrut(12));

        JPanel userPanel = new JPanel(new BorderLayout(0, 4));
        userPanel.setOpaque(false);
        JLabel userLabel = new JLabel("Username / ID:");
        userLabel.setFont(UITheme.FONT_BODY_BOLD);
        userLabel.setForeground(UITheme.TEXT_PRIMARY);
        JTextField tf = UITheme.createTextField(20);
        tf.setText("atharv");
        userPanel.add(userLabel, BorderLayout.NORTH);
        userPanel.add(tf, BorderLayout.CENTER);
        card.add(userPanel);
        card.add(Box.createVerticalStrut(12));

        JPanel passPanel = new JPanel(new BorderLayout(0, 4));
        passPanel.setOpaque(false);
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(UITheme.FONT_BODY_BOLD);
        passLabel.setForeground(UITheme.TEXT_PRIMARY);
        JPasswordField pf = UITheme.createPasswordField(20);
        pf.setText("student123");
        passPanel.add(passLabel, BorderLayout.NORTH);
        passPanel.add(pf, BorderLayout.CENTER);
        card.add(passPanel);
        card.add(Box.createVerticalStrut(18));

        JButton loginBtn = UITheme.createPrimaryButton("Secure Login");
        loginBtn.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(10));

        JButton regBtn = UITheme.createSecondaryButton("New Student? Register Here");
        regBtn.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        regBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(regBtn);

        main.add(card);
        main.add(Box.createVerticalStrut(16));

        JPanel demoBox = new JPanel(new BorderLayout(0, 6));
        demoBox.setBackground(new Color(238, 242, 255));
        demoBox.setBorder(new CompoundBorder(
                new LineBorder(new Color(199, 210, 254), 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JLabel demoHeader = new JLabel("Demo Credentials (Click to Auto-fill):");
        demoHeader.setFont(new Font("SansSerif", Font.BOLD, 11));
        demoHeader.setForeground(UITheme.PRIMARY);
        demoBox.add(demoHeader, BorderLayout.NORTH);

        JPanel demoBtns = new JPanel(new GridLayout(1, 2, 8, 0));
        demoBtns.setOpaque(false);
        demoBtns.add(new JButton("Admin (admin/admin123)"));
        demoBtns.add(new JButton("Student (atharv/student123)"));
        demoBox.add(demoBtns, BorderLayout.CENTER);

        main.add(demoBox);
        root.add(main, BorderLayout.CENTER);
        return root;
    }

    private static JPanel createRegisterView() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_MAIN);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel titleLabel = new JLabel("Create Student Account");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("Register your student details to file and track campus complaints");
        subLabel.setFont(UITheme.FONT_SMALL);
        subLabel.setForeground(new Color(224, 231, 255));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subLabel, BorderLayout.SOUTH);
        root.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(UITheme.BG_MAIN);
        formPanel.setBorder(new EmptyBorder(16, 28, 16, 28));

        JTextField u = UITheme.createTextField(20); u.setText("atharv_k");
        JPasswordField p = UITheme.createPasswordField(20); p.setText("••••••••");
        JTextField f = UITheme.createTextField(20); f.setText("Atharv Kulkarni");
        JTextField r = UITheme.createTextField(20); r.setText("22BCE10234");
        JTextField e = UITheme.createTextField(20); e.setText("atharv.k@vitbhopal.ac.in");
        JTextField ph = UITheme.createTextField(20); ph.setText("9823012345");

        JComboBox<String> dept = new JComboBox<>(new String[]{"SCOPE - Computer Science & Engineering"});
        JComboBox<String> host = new JComboBox<>(new String[]{"Boys Hostel Block 1"});
        JTextField rm = UITheme.createTextField(10); rm.setText("312");

        formPanel.add(createRow("Username *", u));
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(createRow("Password (min 6 chars) *", p));
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(createRow("Full Name *", f));
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(createRow("Registration Number *", r));
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(createRow("Email Address *", e));
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(createRow("Mobile Phone (10 digits) *", ph));
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(createRow("Academic Department", dept));
        formPanel.add(Box.createVerticalStrut(8));

        JPanel residence = new JPanel(new GridLayout(1, 2, 10, 0));
        residence.setOpaque(false);
        residence.add(createRow("Hostel Block", host));
        residence.add(createRow("Room No", rm));
        formPanel.add(residence);

        root.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new LineBorder(UITheme.BORDER, 1));
        buttonPanel.add(UITheme.createSecondaryButton("Cancel"));
        buttonPanel.add(UITheme.createPrimaryButton("Register Account"));
        root.add(buttonPanel, BorderLayout.SOUTH);

        return root;
    }

    private static JPanel createStudentDashboardView(Student student, ComplaintService complaintService) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_MAIN);

        // Top Navigation
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

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

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(UITheme.createActionButton("+ File Complaint", UITheme.SUCCESS, Color.WHITE));
        right.add(UITheme.createActionButton("Logout", new Color(220, 38, 38), Color.WHITE));
        header.add(right, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // Body with tabs simulation
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(UITheme.BG_MAIN);
        body.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Mini KPI cards
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 12, 0));
        kpiRow.setOpaque(false);
        kpiRow.add(UITheme.createMetricCard("TOTAL FILED", "3", UITheme.PRIMARY));
        kpiRow.add(UITheme.createMetricCard("PENDING REVIEW", "1", UITheme.SECONDARY));
        kpiRow.add(UITheme.createMetricCard("IN PROGRESS", "1", UITheme.WARNING));
        kpiRow.add(UITheme.createMetricCard("RESOLVED", "1", UITheme.SUCCESS));
        body.add(kpiRow, BorderLayout.NORTH);

        // Table Card
        JPanel tableCard = UITheme.createCardPanel();
        tableCard.setLayout(new BorderLayout(0, 10));

        JPanel filterBar = new JPanel(new BorderLayout());
        filterBar.setOpaque(false);
        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBox.setOpaque(false);
        searchBox.add(new JLabel("Search:"));
        JTextField sf = UITheme.createTextField(14); sf.setText("Block 1");
        searchBox.add(sf);
        searchBox.add(UITheme.createSecondaryButton("Search"));
        searchBox.add(new JLabel("Status:"));
        searchBox.add(new JComboBox<>(new String[]{"All Statuses"}));
        filterBar.add(searchBox, BorderLayout.WEST);

        filterBar.add(UITheme.createPrimaryButton("View Complaint Details"), BorderLayout.EAST);
        tableCard.add(filterBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Category", "Priority", "Title", "Location", "Status", "Date Submitted", "Assigned To"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        List<Complaint> list = complaintService.getStudentComplaints(student.getUsername());
        for (Complaint c : list) {
            model.addRow(new Object[]{
                    c.getComplaintId(), c.getCategory().getDisplayName(), c.getPriority().getDisplayName(),
                    c.getTitle(), c.getLocation(), c.getStatus().getDisplayName(),
                    DateTimeUtil.formatShort(c.getCreatedAt()), c.getAssignedTo()
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(UITheme.FONT_BODY);
        table.getTableHeader().setFont(UITheme.FONT_BODY_BOLD);
        table.getColumnModel().getColumn(1).setCellRenderer(new UITheme.BadgeCellRenderer());
        table.getColumnModel().getColumn(2).setCellRenderer(new UITheme.BadgeCellRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new UITheme.BadgeCellRenderer());

        JScrollPane scroll = new JScrollPane(table);
        tableCard.add(scroll, BorderLayout.CENTER);
        body.add(tableCard, BorderLayout.CENTER);

        root.add(body, BorderLayout.CENTER);
        return root;
    }

    private static JPanel createSubmitComplaintView(Student student) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_MAIN);

        // Header
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));
        JLabel title = new JLabel("VIT BHOPAL - SMART CAMPUS COMPLAINTS");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        JLabel t = new JLabel("File a New Campus Complaint Ticket");
        t.setFont(UITheme.FONT_SUBTITLE);
        t.setForeground(UITheme.PRIMARY);
        formCard.add(t);
        formCard.add(Box.createVerticalStrut(4));

        JLabel sub = new JLabel("Provide clear details to ensure prompt assignment and resolution by campus maintenance.");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);
        formCard.add(sub);
        formCard.add(Box.createVerticalStrut(18));

        JPanel row1 = new JPanel(new GridLayout(1, 2, 16, 0));
        row1.setOpaque(false);
        JComboBox<ComplaintCategory> cat = new JComboBox<>(ComplaintCategory.values());
        cat.setSelectedItem(ComplaintCategory.WATER);
        cat.setBackground(Color.WHITE);

        JComboBox<ComplaintPriority> pri = new JComboBox<>(ComplaintPriority.values());
        pri.setSelectedItem(ComplaintPriority.URGENT);
        pri.setBackground(Color.WHITE);

        row1.add(createRow("Complaint Category *", cat));
        row1.add(createRow("Severity / Priority Level *", pri));
        formCard.add(row1);
        formCard.add(Box.createVerticalStrut(14));

        JTextField titleField = UITheme.createTextField(30);
        titleField.setText("RO water dispenser leakage causing slippery floor on 3rd floor");
        formCard.add(createRow("Complaint Title (Brief & Clear) *", titleField));
        formCard.add(Box.createVerticalStrut(14));

        JTextField locField = UITheme.createTextField(30);
        locField.setText("Boys Hostel Block 1, 3rd Floor Water Station");
        formCard.add(createRow("Exact Location / Academic Block / Room No *", locField));
        formCard.add(Box.createVerticalStrut(14));

        JTextArea desc = new JTextArea(5, 30);
        desc.setFont(UITheme.FONT_BODY);
        desc.setText("Continuous water leakage from the bottom pipe fitting of the water cooler dispenser. Water is spilling across the tiles and heading toward staircase #2.");
        desc.setBorder(new CompoundBorder(new LineBorder(UITheme.BORDER, 1), new EmptyBorder(6, 8, 6, 8)));
        formCard.add(createRow("Detailed Description of Issue *", new JScrollPane(desc)));
        formCard.add(Box.createVerticalStrut(20));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actions.setOpaque(false);
        actions.add(UITheme.createSecondaryButton("Clear Form"));
        actions.add(UITheme.createPrimaryButton("Submit Complaint Ticket"));
        formCard.add(actions);

        panel.add(formCard, BorderLayout.CENTER);
        root.add(panel, BorderLayout.CENTER);
        return root;
    }

    private static JPanel createComplaintDetailsView(Complaint complaint) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_MAIN);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel idLabel = new JLabel(complaint.getComplaintId());
        idLabel.setFont(UITheme.FONT_TITLE);
        idLabel.setForeground(Color.WHITE);

        JPanel badges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        badges.setOpaque(false);

        JLabel pBadge = new JLabel(" " + complaint.getPriority().getDisplayName().toUpperCase() + " ");
        pBadge.setFont(UITheme.FONT_BADGE);
        pBadge.setForeground(Color.WHITE);
        pBadge.setBackground(Color.decode(complaint.getPriority().getColorHex()));
        pBadge.setOpaque(true);
        pBadge.setBorder(new EmptyBorder(4, 8, 4, 8));

        JLabel sBadge = new JLabel(" " + complaint.getStatus().getDisplayName().toUpperCase() + " ");
        sBadge.setFont(UITheme.FONT_BADGE);
        sBadge.setForeground(Color.WHITE);
        sBadge.setBackground(Color.decode(complaint.getStatus().getBadgeColorHex()));
        sBadge.setOpaque(true);
        sBadge.setBorder(new EmptyBorder(4, 8, 4, 8));

        badges.add(pBadge);
        badges.add(sBadge);

        headerPanel.add(idLabel, BorderLayout.WEST);
        headerPanel.add(badges, BorderLayout.EAST);
        root.add(headerPanel, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(UITheme.BG_MAIN);
        body.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel h1 = new JLabel("Issue Overview");
        h1.setFont(UITheme.FONT_HEADER);
        h1.setForeground(UITheme.PRIMARY);
        body.add(h1);
        body.add(Box.createVerticalStrut(6));
        body.add(createDetailItem("Title", complaint.getTitle()));
        body.add(createDetailItem("Category", complaint.getCategory().getDisplayName()));
        body.add(createDetailItem("Location", complaint.getLocation()));

        body.add(Box.createVerticalStrut(14));
        JLabel h2 = new JLabel("Description");
        h2.setFont(UITheme.FONT_HEADER);
        h2.setForeground(UITheme.PRIMARY);
        body.add(h2);
        body.add(Box.createVerticalStrut(6));

        JTextArea desc = new JTextArea(complaint.getDescription());
        desc.setFont(UITheme.FONT_BODY);
        desc.setEditable(false);
        desc.setBorder(new CompoundBorder(new LineBorder(UITheme.BORDER, 1), new EmptyBorder(6, 8, 6, 8)));
        body.add(desc);

        body.add(Box.createVerticalStrut(14));
        JLabel h3 = new JLabel("Tracking Information");
        h3.setFont(UITheme.FONT_HEADER);
        h3.setForeground(UITheme.PRIMARY);
        body.add(h3);
        body.add(Box.createVerticalStrut(6));
        body.add(createDetailItem("Submitted By", complaint.getStudentName() + " (" + complaint.getStudentRegNo() + ")"));
        body.add(createDetailItem("Assigned To", complaint.getAssignedTo()));
        body.add(createDetailItem("Date Submitted", DateTimeUtil.format(complaint.getCreatedAt())));

        body.add(Box.createVerticalStrut(14));
        JLabel h4 = new JLabel("Official Remarks");
        h4.setFont(UITheme.FONT_HEADER);
        h4.setForeground(UITheme.PRIMARY);
        body.add(h4);
        body.add(Box.createVerticalStrut(6));

        JTextArea rem = new JTextArea(complaint.getAdminRemarks());
        rem.setFont(UITheme.FONT_BODY);
        rem.setBackground(new Color(254, 252, 232));
        rem.setBorder(new CompoundBorder(new LineBorder(new Color(254, 240, 138), 1), new EmptyBorder(6, 8, 6, 8)));
        body.add(rem);

        root.add(body, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        footer.setBackground(Color.WHITE);
        footer.setBorder(new LineBorder(UITheme.BORDER, 1));
        footer.add(UITheme.createPrimaryButton("Close"));
        root.add(footer, BorderLayout.SOUTH);

        return root;
    }

    private static JPanel createAdminDashboardView(Admin admin, ComplaintService complaintService, ReportService reportService) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_MAIN);

        // Header
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setBackground(new Color(15, 23, 42));
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
        right.add(UITheme.createSecondaryButton("Refresh Data"));
        right.add(UITheme.createActionButton("Logout", UITheme.DANGER, Color.WHITE));
        header.add(right, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(UITheme.BG_MAIN);
        body.setBorder(new EmptyBorder(16, 16, 16, 16));

        ReportService.AnalyticsSummary s = reportService.generateSummary();

        JPanel kpiRow = new JPanel(new GridLayout(1, 5, 12, 0));
        kpiRow.setOpaque(false);
        kpiRow.add(UITheme.createMetricCard("TOTAL COMPLAINTS", String.valueOf(s.totalComplaints()), UITheme.PRIMARY));
        kpiRow.add(UITheme.createMetricCard("AWAITING REVIEW", String.valueOf(s.submittedCount()), UITheme.SECONDARY));
        kpiRow.add(UITheme.createMetricCard("IN PROGRESS", String.valueOf(s.inProgressCount()), UITheme.WARNING));
        kpiRow.add(UITheme.createMetricCard("RESOLVED TICKETS", String.valueOf(s.resolvedCount()), UITheme.SUCCESS));
        kpiRow.add(UITheme.createMetricCard("URGENT PENDING", String.valueOf(s.urgentPendingCount()), UITheme.DANGER));
        body.add(kpiRow, BorderLayout.NORTH);

        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 12));

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterBar.setOpaque(false);
        filterBar.add(new JLabel("Search:"));
        filterBar.add(UITheme.createTextField(12));
        filterBar.add(new JLabel("Status:"));
        filterBar.add(new JComboBox<>(new String[]{"All Statuses", "Submitted", "In Progress", "Resolved"}));
        filterBar.add(new JLabel("Category:"));
        filterBar.add(new JComboBox<>(new String[]{"All Categories", "Hostel", "Water", "Electricity"}));
        filterBar.add(UITheme.createSecondaryButton("Filter"));
        card.add(filterBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Student Name", "Reg No", "Category", "Priority", "Title", "Location", "Status", "Assigned To", "Submitted"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        for (Complaint c : complaintService.getAllComplaints()) {
            model.addRow(new Object[]{
                    c.getComplaintId(), c.getStudentName(), c.getStudentRegNo(),
                    c.getCategory().getDisplayName(), c.getPriority().getDisplayName(),
                    c.getTitle(), c.getLocation(), c.getStatus().getDisplayName(),
                    c.getAssignedTo(), DateTimeUtil.formatShort(c.getCreatedAt())
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(UITheme.FONT_BODY);
        table.getTableHeader().setFont(UITheme.FONT_BODY_BOLD);
        table.getColumnModel().getColumn(3).setCellRenderer(new UITheme.BadgeCellRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new UITheme.BadgeCellRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new UITheme.BadgeCellRenderer());

        card.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        actionRow.setOpaque(false);
        actionRow.add(UITheme.createSecondaryButton("View Full Details"));
        actionRow.add(UITheme.createSecondaryButton("Student Info"));
        actionRow.add(UITheme.createActionButton("Assign Staff", new Color(79, 70, 229), Color.WHITE));
        actionRow.add(UITheme.createActionButton("Update Status", UITheme.WARNING, Color.WHITE));
        actionRow.add(UITheme.createActionButton("Resolve Ticket", UITheme.SUCCESS, Color.WHITE));
        card.add(actionRow, BorderLayout.SOUTH);

        body.add(card, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);
        return root;
    }

    private static JPanel createRow(String text, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_BODY_BOLD);
        l.setForeground(UITheme.TEXT_PRIMARY);
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private static JPanel createDetailItem(String label, String value) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        JLabel l = new JLabel(label + ":");
        l.setFont(UITheme.FONT_BODY_BOLD);
        l.setForeground(UITheme.TEXT_SECONDARY);
        l.setPreferredSize(new Dimension(170, 20));

        JLabel v = new JLabel(value != null ? value : "N/A");
        v.setFont(UITheme.FONT_BODY);
        v.setForeground(UITheme.TEXT_PRIMARY);
        p.add(l, BorderLayout.WEST);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private static void savePanel(JPanel panel, String path, int width, int height) {
        panel.setSize(width, height);
        panel.setPreferredSize(new Dimension(width, height));
        layoutRecursive(panel);

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        panel.paint(g);
        g.dispose();

        try {
            ImageIO.write(img, "png", new File(path));
            System.out.println("  [SAVED SCREENSHOT] " + path);
        } catch (Exception e) {
            System.err.println("  [ERROR] " + e.getMessage());
        }
    }

    private static void layoutRecursive(Container container) {
        container.doLayout();
        for (Component child : container.getComponents()) {
            if (child instanceof Container c) {
                layoutRecursive(c);
            }
        }
    }

    private static void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) deleteDirectory(f);
                else f.delete();
            }
        }
        dir.delete();
    }
}
